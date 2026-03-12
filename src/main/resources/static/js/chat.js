document.addEventListener("DOMContentLoaded", function () {

const chatModal = document.getElementById('chatModal');
  
  chatModal.addEventListener('show.bs.modal', function(event) {
    const clickedElement = event.relatedTarget; // el elemento que se pulsó
    const chatId = clickedElement.getAttribute('data-id');
    
    // Actualizar el título del modal
    document.getElementById('chatModalLabel').textContent = 'Chat Nº ' + chatId;
  });

});