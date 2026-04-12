document.addEventListener('DOMContentLoaded', function () {

    /* ====== WEBSOCKET CALENDARIO ====== */
    let currentEventChatId = null;
    let eventStompSubscription = null;
    const eventChatMessages = document.getElementById('eventChatMessages');
    const eventMessageInput = document.getElementById('eventMessageInput');
    const eventMessageForm = document.getElementById('eventMessageForm');

    const eventSocket = new SockJS('/ws');
    const eventStompClient = Stomp.over(eventSocket);
    eventStompClient.connect({}, function () {
        console.log('Conectado a WebSocket para calendario');
    });

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

    var calendarEl = document.getElementById('calendar');

    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'es',
        height: 'auto',
        dayMaxEvents: true,
        displayEventTime: false,

        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },

        buttonText: {
            today: 'Hoy',
            month: 'Mes',
            week: 'Semana',
            day: 'Día'
        },

        events: '/eventos/api/mis-eventos',

        eventDidMount: function(info) {

            let estado = info.event.extendedProps.estado;

            if (estado === 'en_curso') info.el.style.backgroundColor = '#198754';
            if (estado === 'proximo') info.el.style.backgroundColor = '#ffc107';
            if (estado === 'terminado') info.el.style.backgroundColor = '#6c757d';
            if (estado === 'cancelado') info.el.style.backgroundColor = '#dc3545';
        },

        eventClick: async function(info) {

            const event = info.event;
            const eventId = event.id;

            document.getElementById("modalEventTitle").textContent = event.title;
            document.getElementById("modalEventDesc").textContent = event.extendedProps.description;
            const fecha = new Date(event.start);
            document.getElementById("modalDate").textContent = fecha.toLocaleDateString('es-ES');
            document.getElementById("modalTime").textContent = fecha.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
            document.getElementById("modalCapacity").textContent = event.extendedProps.maxCapacity;

            // Cargar datos del evento (participantes y chat)
            try {
                const response = await fetch(`/eventos/${eventId}`);
                const eventDetails = await response.json();

                // Cargar participantes
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

                    const isParticipant = eventDetails.isUserParticipant || eventDetails.isUserCreator;
                    const messageForm = document.getElementById('eventMessageForm');
                    
                    if (isParticipant) {
                        await loadEventChatMessages(currentEventChatId);
                        subscriteToEventChat(currentEventChatId);
                        messageForm.style.display = 'block';
                    } else {
                        messageForm.style.display = 'none';
                        const info = document.createElement('p');
                        info.className = 'text-muted small text-center';
                        info.textContent = 'Únete al evento para participar en el chat';
                        messageForm.parentElement.appendChild(info);
                    }
                } else {
                    eventChatMessages.innerHTML = '<p class="text-muted">No hay chat para este evento</p>';
                }
            } catch (error) {
                console.error('Error cargando datos del evento:', error);
                eventChatMessages.innerHTML = '<p class="text-muted">Error al cargar el chat</p>';
            }

            const modal = new bootstrap.Modal(document.getElementById('viewEventModal'));
            modal.show();
        }
    });

    calendar.render();

    // Funciones del chat
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

    // Enviar mensaje del chat
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

    // Limpiar al cerrar modal
    const viewModal = document.getElementById('viewEventModal');
    viewModal.addEventListener('hidden.bs.modal', () => {
        if (eventStompSubscription) {
            eventStompSubscription.unsubscribe();
            eventStompSubscription = null;
        }
        currentEventChatId = null;
        eventChatMessages.innerHTML = '';

        const eventMessageForm = document.getElementById('eventMessageForm');
        eventMessageForm.style.display = 'block';
        const infoMsg = eventMessageForm.parentElement.querySelector('.text-muted.small');
        if (infoMsg) infoMsg.remove();
    });
});
