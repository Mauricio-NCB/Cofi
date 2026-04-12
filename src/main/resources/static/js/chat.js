document.addEventListener('DOMContentLoaded', () => {

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
      alert('Agrega al menos un participante');
      return;
    }

    const res = await fetch('/chat/crear', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
        name: document.getElementById('chatName').value,
        participants: participants
       })
    });

    if (res.ok) {
      window.location.href = '/chat';
    } else {
      alert('Error al crear el chat');    
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