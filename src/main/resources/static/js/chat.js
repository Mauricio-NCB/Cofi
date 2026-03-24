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

  chatModal.addEventListener('show.bs.modal', (event) => {
    const trigger = event.relatedTarget;
    currentChatId = trigger.getAttribute('data-id');
    document.getElementById('chatModalLabel').textContent = 'Chat Nº ' + currentChatId;
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