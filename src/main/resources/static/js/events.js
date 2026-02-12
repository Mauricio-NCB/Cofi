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

    document.getElementById('modalStart').textContent =
      card.getAttribute('data-start');

    document.getElementById('modalEnd').textContent =
      card.getAttribute('data-end');

    document.getElementById('modalCapacity').textContent =
      card.getAttribute('data-capacity');
  });

});
