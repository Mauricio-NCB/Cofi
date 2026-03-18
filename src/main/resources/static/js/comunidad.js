document.addEventListener("DOMContentLoaded", function () {

    const postModal = document.getElementById("postModal");

    postModal.addEventListener("show.bs.modal", function (event) {

        const card = event.relatedTarget;

        if (!card) return;

        const title = card.dataset.title || "";
        const content = card.dataset.content || "";
        const author = card.dataset.author || "";
        const date = card.dataset.date || "";
        const image = card.dataset.image || "";
        const postId = card.dataset.id || "";

        document.getElementById("modalTitle").textContent = title;
        document.getElementById("modalContent").textContent = content;
        document.getElementById("modalAuthor").textContent = author;
        document.getElementById("modalDate").textContent = date;
        document.getElementById("modalImage").src = image;

        document.getElementById("modalPostId").value = postId;

        cargarComentarios(postId);
        console.log("post abierto: ", postId);
    });

    // ====== BUSCADOR POSTS ======
    const searchInput = document.getElementById("postSearch");

    if (searchInput) {
        const postCards = document.querySelectorAll(".post-card");

        searchInput.addEventListener("input", function () {

            const query = this.value.toLowerCase().trim();

            postCards.forEach(card => {

                const title = card.querySelector(".card-title").textContent.toLowerCase();
                const content = card.querySelector(".card-text").textContent.toLowerCase();
                const wrapper = card.closest(".col-md-4");

                if (title.includes(query) || content.includes(query)) {

                    wrapper.style.display = "block";

                } else {

                    wrapper.style.display = "none";

                }

            });

        });
    }

});