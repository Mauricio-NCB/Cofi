document.addEventListener('DOMContentLoaded', () => {

  /* ====== BUSCADOR ====== */
  const searchInput = document.getElementById('eventSearch');
  const eventCards = document.querySelectorAll('.event-card');

  searchInput.addEventListener('input', function () {
    const query = this.value.toLowerCase();

    eventCards.forEach(card => {
      const title = card.querySelector('.card-title').textContent.toLowerCase();
      const description = card.querySelector('.description-container p')
        .textContent.toLowerCase();

      const wrapper = card.closest('.flex-shrink-0');

      if (title.includes(query) || description.includes(query)) {
        wrapper.style.display = 'block';
      } else {
        wrapper.style.display = 'none';
      }
    });
  });

  /* ====== VER EVENTO ====== */
  const viewModal = document.getElementById('viewEventModal');

  viewModal.addEventListener('show.bs.modal', function (event) {
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
      card.getAttribute('data-capacity');
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
          alert(result.message);
          location.reload();
        } else {
          alert('Error: ' + result.message);
          button.disabled = false;
          button.textContent = originalText;
        }
      } catch (error) {
        console.error('Error:', error);
        alert('Error al unirse al evento');
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
      
      // Confirmar antes de salir
      if (!confirm('¿Estás seguro de que quieres salir de este evento?')) {
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
          alert(result.message);
          location.reload();
        } else {
          alert('Error: ' + result.message);
          button.disabled = false;
          button.textContent = originalText;
        }
      } catch (error) {
        console.error('Error:', error);
        alert('Error al salir del evento');
        button.disabled = false;
        button.textContent = originalText;
      }
    });
  });

});
