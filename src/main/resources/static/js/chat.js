document.addEventListener('DOMContentLoaded', () => {

  /* ====== NOTIFICACIONES ====== */
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
      modal.id = 'confirmationModal';
      modal.innerHTML = `
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">${title}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              ${message}
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                Cancelar
              </button>
              <button type="button" class="btn btn-danger" id="confirmBtn">
                Sí, eliminar
              </button>
            </div>
          </div>
        </div>
      `;
      document.body.appendChild(modal);
      
      const bsModal = new bootstrap.Modal(modal);
      bsModal.show();
      
      const confirmBtn = document.getElementById('confirmBtn');
      confirmBtn.addEventListener('click', () => {
        resolved = true;
        bsModal.hide();
      });
      
      modal.addEventListener('hidden.bs.modal', () => {
        bsModal.dispose();
        const backdrop = document.querySelector('.modal-backdrop');
        if (backdrop) backdrop.remove();
        modal.remove();
        if (!resolved) resolve(false);
        if (resolved) resolve(true);
      });
    });
  }

  /* ====== ELIMINAR CHAT ====== */
  window.deleteChat = async function(chatId, event) {
    event.stopPropagation();
    const confirmed = await showConfirmModal(
      'Eliminar Chat',
      '¿Estás seguro de que deseas eliminar este chat?'
    );
    
    if (confirmed) {
      try {
        const res = await fetch(`/chat/${chatId}/delete`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' }
        });

        if (res.ok) {
          showNotification('✓ Chat eliminado correctamente', 'success');
          setTimeout(() => {
            window.location.href = '/chat';
          }, 1000);
        } else {
          showNotification('❌ Error al eliminar el chat', 'error');
        }
      } catch (err) {
        console.error('Error eliminando chat:', err);
        showNotification('❌ Error al eliminar el chat', 'error');
      }
    }
  }

  const socket = new SockJS('/ws');
  const stompClient = Stomp.over(socket);

  let stompSubscription = null;
  let currentChatId = null;

  stompClient.connect({}, () => {
    console.log('Conectado a WebSocket');
  });

  function suscribeToChat(chatId) {

    // Desuscribirse del chat anterior si existe
    if(stompSubscription) {
      console.log('Desuscribiendo del chat ' + currentChatId);
      stompSubscription.unsubscribe();
      stompSubscription = null;
    }

    currentChatId = chatId;
    console.log('Suscribiendo al chat ' + chatId);

    stompSubscription = stompClient.subscribe(`/topic/chat.${chatId}`, (message) => {
      const msg = JSON.parse(message.body);
      appendMessage(msg);
      chatMessages.scrollTop = chatMessages.scrollHeight;
    });
  }

  const chatModal = document.getElementById('chatModal');
  const chatMessages = document.getElementById('chatMessages');
  const messageForm = document.getElementById('messageForm');
  const messageInput = document.getElementById('messageInput');

  async function loadMessages(chatId) {
    chatMessages.innerHTML = '';
    const res = await fetch(`/chat/${chatId}/messages`);
    const msgs = await res.json();
    msgs.forEach(appendMessage);
    chatMessages.scrollTop = chatMessages.scrollHeight;
  }

  function appendMessage(msg) {
    const time = new Date(msg.timestamp).toLocaleString();
    const div = document.createElement('div');
    div.className = 'mb-2';
    div.innerHTML = `
      <div><strong>${msg.userName}</strong> <small class="text-muted">${time}</small></div>
      <div>${msg.content}</div>`;
    chatMessages.appendChild(div);
  }

  window.addParticipant = function() {
    const div = document.createElement('div');
    div.className = 'participant-field d-flex flex-column mb-2';
    div.innerHTML = `
      <div class="participant-row d-flex mb-2">
        <input type="text" class="form-control me-2 participant-name" placeholder="Nombre" required>
        <input type="text" class="form-control participant-lastname" placeholder="Apellido" required>
      </div>
        <button type="button" class="btn btn-danger remove-participant mt-2 align-self-end">Eliminar</button>
    `;
    document.getElementById('participantsList').appendChild(div);
    
    // Agregar evento al botón eliminar
    const removeBtn = div.querySelector('.remove-participant');
    removeBtn.addEventListener('click', function() {
      div.remove();
    });
  }

  window.createChat = async function() {
    // Validar nombre del chat
    const chatName = document.getElementById('chatName').value.trim();
    if (!chatName) {
      showNotification('❌ Debes escribir el nombre del chat', 'error');
      return;
    }

    const rows = document.querySelectorAll('.participant-row');
    const participants = [];

    rows.forEach(row => {
      const name = row.querySelector('.participant-name').value.trim();
      const lastName = row.querySelector('.participant-lastname').value.trim();
      if (name && lastName) {
        participants.push({ name, lastName });
      }
    });

    if (participants.length == 0) {
      showNotification('❌ Debes agregar al menos un participante', 'error');
      return;
    }

    // Verifica que todos los participantes existen
    let allExist = true;
    let notFoundParticipants = [];

    for (const participant of participants) {
      const exists = await fetch(`/chat/verify-user`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: participant.name,
          lastName: participant.lastName
        })
      })
        .then(res => res.json())
        .catch(err => {
          console.error('Error verificando usuario:', err);
          return false;
        });

      if (!exists) {
        allExist = false;
        notFoundParticipants.push(`${participant.name} ${participant.lastName}`);
      }
    }

    if (!allExist) {
      const notFound = notFoundParticipants.join(', ');
      showNotification(`❌ Usuario(s) ${notFound} no existen`, 'error');
      return;
    }

    const res = await fetch('/chat/crear', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
        name: chatName,
        participants: participants
       })
    });

    if (res.ok) {
      showNotification('✓ Chat creado correctamente', 'success');
      setTimeout(() => {
        window.location.href = '/chat';
      }, 1000);
    } else {
      showNotification('❌ Error al crear el chat', 'error');    
    }

  }

  chatModal.addEventListener('show.bs.modal', (event) => {
    const trigger = event.relatedTarget;
    
    currentChatId = trigger.getAttribute('data-id');
    
    const chatName = trigger.getAttribute('data-name')
    document.getElementById('chatModalLabel').textContent = chatName;
    loadMessages(currentChatId);
    suscribeToChat(currentChatId);
  });

  chatModal.addEventListener('hidden.bs.modal', () => {
    if(stompSubscription) {
      console.log('Desuscribiendo del chat ' + currentChatId);
      stompSubscription.unsubscribe();
      stompSubscription = null;
      currentChatId = null;
    }
  });

  messageForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const content = messageInput.value.trim();
    if (!content) return;

    const res = await fetch(`/chat/${currentChatId}/messages/ajax`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ content })
    });

    if (!res.ok) return;

    messageInput.value = '';
  });
});