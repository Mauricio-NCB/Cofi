document.addEventListener("DOMContentLoaded", function () {

    const postModal = document.getElementById('postModal');

    postModal.addEventListener('show.bs.modal', function (event) {

        const card = event.relatedTarget;

        if (!card) return;

        const title = card.dataset.title || "";
        const content = card.dataset.content || "";
        const author = card.dataset.author || "";
        const date = card.dataset.date || "";
        const image = card.querySelector("img")?.src || "";

        document.getElementById('modalTitle').textContent = title;
        document.getElementById('modalContent').textContent = content;
        document.getElementById('modalAuthor').textContent = author;
        document.getElementById('modalDate').textContent = date;
        document.getElementById('modalImage').src = image;

    });

});