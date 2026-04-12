document.addEventListener('DOMContentLoaded', () => {

  /* ====== NOTIFICACION ENTRAR / SALIR EVENTO ====== */
  function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'success' ? 'success' : 'danger'} position-fixed`;
    notification.style.cssText = `
      top: 80px;
      right: 20px;
      z-index: 2000;
      min-width: 300px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      animation: slideIn 0.3s ease forwards;
    `;
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(() => {
      notification.style.animation = 'slideOut 0.3s ease forwards';
      setTimeout(() => notification.remove(), 300);
    }, 3000);
  }

  if (!document.getElementById('notificationStyles')) {
    const style = document.createElement('style');
    style.id = 'notificationStyles';
    style.textContent = `
      @keyframes slideIn {
        from {
          transform: translateX(400px);
          opacity: 0;
        }
        to {
          transform: translateX(0);
          opacity: 1;
        }
      }
      @keyframes slideOut {
        from {
          transform: translateX(0);
          opacity: 1;
        }
        to {
          transform: translateX(400px);
          opacity: 0;
        }
      }
    `;
    document.head.appendChild(style);
  }

  /* ====== MODAL DE CONFIRMACIÓN ====== */
  function showConfirmModal(title, message) {
    return new Promise((resolve) => {
      let resolved = false;

      const modal = document.createElement('div');
      modal.className = 'modal fade';
      modal.style.zIndex = '9999';
      modal.innerHTML = `
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">${title}</h5>
              <button type="button" class="btn-close" onclick="this.closest('.modal').remove()"></button>
            </div>
            <div class="modal-body">
              ${message}
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" onclick="this.closest('.modal').remove()">
                Cancelar
              </button>
              <button type="button" class="btn btn-danger" id="confirmBtn">
                Sí, salir
              </button>
            </div>
          </div>
        </div>
      `;
      document.body.appendChild(modal);
      
      const bsModal = new bootstrap.Modal(modal);
      bsModal.show();
      
      document.getElementById('confirmBtn').addEventListener('click', () => {
        resolved = true;
        bsModal.hide();
        setTimeout(() => {
          modal.remove();
          resolve(true);
        }, 300);
      });
      
      modal.addEventListener('hidden.bs.modal', () => {
        modal.remove();
        if (!resolved) resolve(false);
      });
    });
  }

  /* ====== BUSCADOR ====== */
  const searchInput = document.getElementById('eventSearch');
  const eventCards = document.querySelectorAll('.event-card');

  searchInput.addEventListener('input', function () {
    const query = this.value.toLowerCase();

    eventCards.forEach(card => {
      const title = card.querySelector('.card-title').textContent.toLowerCase();

      const wrapper = card.closest('.flex-shrink-0');

      if (title.includes(query)) {
        wrapper.style.display = 'block';
      } else {
        wrapper.style.display = 'none';
      }
    });
  });

  /* ====== VER EVENTO ====== */
  const viewModal = document.getElementById('viewEventModal');
  const eventChatMessages = document.getElementById('eventChatMessages');
  const eventMessageForm = document.getElementById('eventMessageForm');
  const eventMessageInput = document.getElementById('eventMessageInput');

  let currentEventChatId = null;
  let eventStompSubscription = null;

  const eventSocket = new SockJS('/ws');
  const eventStompClient = Stomp.over(eventSocket);
  eventStompClient.connect({}, function () {
    console.log('Conectado a WebSocket para eventos');
  });
  

  viewModal.addEventListener('show.bs.modal', async (event) => {
    const card = event.relatedTarget;

    document.getElementById('modalEventTitle').textContent =
      card.getAttribute('data-title');

    document.getElementById('modalEventDesc').textContent =
      card.getAttribute('data-desc');

    document.getElementById('modalDate').textContent =
      card.getAttribute('data-date');

    document.getElementById('modalTime').textContent =
      card.getAttribute('data-time');

    document.getElementById('modalCapacity').textContent =
      card.getAttribute('data-available-spots');

    const eventId = card.getAttribute('data-id');

    // Carga participantes y chatId
    const response = await fetch(`/eventos/${eventId}`);
    const eventDetails = await response.json();

    const participantsList = document.getElementById('modalParticipants');
    participantsList.innerHTML = '';

    if (eventDetails.participants && eventDetails.participants.length > 0) {
      eventDetails.participants.forEach(participant => {
        const li = document.createElement('li');
        li.className = 'list-group-item py-1';
        li.innerHTML = `<i class="bi bi-person-circle me-2"></i>${participant.name} ${participant.lastName}`;
        participantsList.appendChild(li);
      });
    } else {
      participantsList.innerHTML = '<li class="list-group-item py-1">No hay participantes aún</li>';
    }

    // Cargar chat si existe
    if (eventDetails.chatId) {
      currentEventChatId = eventDetails.chatId;

      // mostrar u ocultar el formulario según si el usuario es participante
      const isParticipant = eventDetails.isUserParticipant || eventDetails.isUserCreator;
      const messageForm = document.getElementById('eventMessageForm');
      
      if (isParticipant) {
        await loadEventChatMessages(currentEventChatId);
        subscriteToEventChat(currentEventChatId);
        messageForm.style.display = 'block';
      } else {
          messageForm.style.display = 'none';
          // mostrar mensaje informativo
          const info = document.createElement('p');
          info.className = 'text-muted small text-center';
          info.textContent = 'Únete al evento para participar en el chat';
          messageForm.parentElement.appendChild(info);
      }
    } else {
      eventChatMessages.innerHTML = '<p class="text-muted">No hay chat para este evento</p>';
    }
  });

  viewModal.addEventListener('hidden.bs.modal', () => {
    // Limpiar chat y cancelar suscripción
    if (eventStompSubscription) {
        eventStompSubscription.unsubscribe();
        eventStompSubscription = null;
    }
    currentEventChatId = null;
    eventChatMessages.innerHTML = '';

    // limpiar el mensaje informativo si existe
    const eventMessageForm = document.getElementById('eventMessageForm');
    eventMessageForm.style.display = 'block';
    const infoMsg = eventMessageForm.parentElement.querySelector('.text-muted.small');
    if (infoMsg) infoMsg.remove();

  });


  /* ====== UNIRSE A UN EVENTO ====== */
  const joinButtons = document.querySelectorAll('.join-event-btn');
  
  joinButtons.forEach(button => {
    button.addEventListener('click', async (e) => {
      e.stopPropagation();
      
      const eventId = button.getAttribute('data-event-id');
      const originalText = button.textContent;
      
      try {
        button.disabled = true;
        button.textContent = 'Uniéndose...';
        
        const response = await fetch(`/eventos/${eventId}/unirse`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          }
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
          // Mostrar notificación mejorada
          showNotification('✓ Te has unido al evento correctamente', 'success');
          setTimeout(() => location.reload(), 1500);
        } else {
          showNotification('Error: ' + result.message, 'error');
          button.disabled = false;
          button.textContent = originalText;
        }
      } catch (error) {
        console.error('Error:', error);
        showNotification('Error al unirse al evento', 'error');
        button.disabled = false;
        button.textContent = originalText;
      }
    });
  });

  /* ====== SALIR DE UN EVENTO ====== */
  const leaveButtons = document.querySelectorAll('.leave-event-btn');
  
  leaveButtons.forEach(button => {
    button.addEventListener('click', async (e) => {
      e.stopPropagation();
      
      const eventId = button.getAttribute('data-event-id');
      const originalText = button.textContent;
      
      // Confirmar antes de salir con modal personalizado
      const confirmed = await showConfirmModal(
        'Salir del evento',
        '¿Estás seguro de que quieres salir de este evento?'
      );
      
      if (!confirmed) {
        return;
      }
      
      try {
        button.disabled = true;
        button.textContent = 'Saliendo...';
        
        const response = await fetch(`/eventos/${eventId}/salir`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          }
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
          showNotification('✓ ' + result.message, 'success');
          setTimeout(() => location.reload(), 1500);
        } else {
          showNotification('Error: ' + result.message, 'error');
          button.disabled = false;
          button.textContent = originalText;
        }
      } catch (error) {
        console.error('Error:', error);
        showNotification('Error al salir del evento', 'error');
        button.disabled = false;
        button.textContent = originalText;
      }
    });
  });

  async function loadEventChatMessages(chatId) {
    eventChatMessages.innerHTML = '';
    const response = await fetch(`/chat/${chatId}/messages`);
    const messages = await response.json();
    messages.forEach(appendEventMessage);
    eventChatMessages.scrollTop = eventChatMessages.scrollHeight;
  }

  function appendEventMessage(msg) {
    const time = new Date(msg.timestamp).toLocaleString();
    const div = document.createElement('div');
    div.className = 'mb-2';
    div.innerHTML = `
        <div><strong>${msg.userName}</strong> <small class="text-muted">${time}</small></div>
        <div>${msg.content}</div>`;
    eventChatMessages.appendChild(div);
  }

  function subscriteToEventChat(chatId) {
    if (eventStompSubscription) {
      eventStompSubscription.unsubscribe();
    }
    eventStompSubscription = eventStompClient.subscribe(`/topic/chat.${chatId}`, (message) => {
      const msg = JSON.parse(message.body);
      appendEventMessage(msg);
      eventChatMessages.scrollTop = eventChatMessages.scrollHeight;
    });
  }

  eventMessageForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const content = eventMessageInput.value.trim();
    if (!content) return;

    await fetch(`/chat/${currentEventChatId}/messages/ajax`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ content })
    });

    eventMessageInput.value = '';
  });

  /* ====== SCROLL HORIZONTAL ====== */
  document.querySelectorAll('.scroll-outer').forEach(scrollOuter => {
    const leftBtn = scrollOuter.querySelector('.scroll-left');
    const rightBtn = scrollOuter.querySelector('.scroll-right');
    const container = scrollOuter.querySelector('[data-scroll-container]');

    if (leftBtn && rightBtn && container) {
      const scrollDistance = 400;

      leftBtn.addEventListener('click', () => {
        container.scrollBy({ left: -scrollDistance, behavior: 'smooth' });
      });

      rightBtn.addEventListener('click', () => {
        container.scrollBy({ left: scrollDistance, behavior: 'smooth' });
      });

      // Mostrar/ocultar botones según posición del scroll
      const updateButtonVisibility = () => {
        leftBtn.style.display = container.scrollLeft > 0 ? 'flex' : 'none';
        rightBtn.style.display = 
          container.scrollLeft < container.scrollWidth - container.clientWidth ? 'flex' : 'none';
      };

      container.addEventListener('scroll', updateButtonVisibility);
      window.addEventListener('resize', updateButtonVisibility);
      updateButtonVisibility();
    }
  });

  /* ====== FILTRADO POR CÓDIGO POSTAL ====== */
  const postcodeFilter = document.getElementById('postcodeFilter');
  if (postcodeFilter) {
    postcodeFilter.addEventListener('change', function() {
      const selectedPostcode = this.value;
      const firstScrollOuter = document.querySelector('.activities-section .scroll-outer');
      if (firstScrollOuter) {
        const eventCardsInSection = firstScrollOuter.querySelectorAll('[data-postcode]');

        eventCardsInSection.forEach(cardWrapper => {
          const cardPostcode = cardWrapper.getAttribute('data-postcode');
          if (selectedPostcode === '') {
            // Mostrar todos
            cardWrapper.style.display = 'block';
          } else if (cardPostcode === selectedPostcode) {
            // Mostrar solo los que coinciden
            cardWrapper.style.display = 'block';
          } else {
            // Ocultar los que no coinciden
            cardWrapper.style.display = 'none';
          }
        });
      }
    });
  }
});

const selectedCategories = new Set();

async function loadCategories() {
    const res = await fetch("/api/categories");
    const categories = await res.json();
    const container = document.getElementById("categoriesContainer");
    const inputsContainer = document.getElementById("categoriesInputs");

    categories.forEach(category => {
        const card = document.createElement("div");
        card.className = "category-card";
        card.textContent = category.name;
        card.dataset.id = category.id;

        card.addEventListener("click", () => {
            const id = category.id;

            if (selectedCategories.has(id)) {
                selectedCategories.delete(id);
                card.classList.remove("selected");

                // eliminar input
                const input = document.querySelector(`input[value="${id}"]`);
                if (input) input.remove();

            } else {
                selectedCategories.add(id);
                card.classList.add("selected");

                // crear input oculto
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = "categories";
                input.value = id;

                inputsContainer.appendChild(input);
            }
        });

        container.appendChild(card);
    });
}

loadCategories();
