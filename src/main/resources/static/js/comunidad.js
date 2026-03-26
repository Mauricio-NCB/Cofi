document.addEventListener("DOMContentLoaded", function () {

    document.querySelectorAll('.post-card').forEach(card => {
        const postId = card.dataset.id;
        loadPostReactions(postId);
        
        card.addEventListener('click', function(e) {
            if (!e.target.closest('.reactions-container')) {
                openPostModal(card);
            }
        });
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

                wrapper.style.display = (title.includes(query) || content.includes(query)) ? "block" : "none";
            });
        });
    }

});

function openPostModal(card) {
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
    
    const modal = new bootstrap.Modal(document.getElementById('postModal'));
    modal.show();
}

async function loadPostReactions(postId){
    const container = document.getElementById(`reactions-post-${postId}`);
    container.innerHTML = '';

    const allReactions = await fetch('/comunidad/reacciones/disponibles').then(r => r.json());
    const postReactionsData = await fetch(`/comunidad/reacciones/${postId}`).then(r => r.json());

    // Crear un mapa de reacciones por ID para búsqueda rápida
    const reactionMap = {};
    postReactionsData.forEach(reaction => {
        reactionMap[reaction.reaction.id] = reaction;
    });

    allReactions.forEach(r => {
        const btn = document.createElement('button');
        btn.classList.add('btn','btn-sm','me-1','reaction-btn');
        btn.type = 'button';

        btn.innerHTML = r.emojiUnicode || `<i class="${r.iconCss}"></i>`;

        const reactionData = reactionMap[r.id];
        const count = reactionData ? reactionData.count : 0;
        const counterSpan = document.createElement('span');
        counterSpan.textContent = ` ${count}`;
        btn.appendChild(counterSpan);

        // Marcar si el usuario actual ya reaccionó
        if(reactionData && reactionData.userReacted) {
            btn.classList.add('active');
        }

        // Verificar si la reacción está desbloqueada
        const isUnlocked = r.unlocked;
        if (!isUnlocked) {
            btn.disabled = true;
            btn.classList.add('locked-reaction');
            const badge = document.createElement('span');
            badge.classList.add('badge', 'bg-danger', 'ms-1');
            badge.textContent = 'BLOQUEADO';
            btn.appendChild(badge);
        }

        btn.addEventListener('click', e => {
            e.stopPropagation();
            if (!isUnlocked) {
                alert('Esta reacción está bloqueada. Completa un logro para desbloquearla.');
                return;
            }
            reactToPost(postId, r.id);
        });

        container.appendChild(btn);
    });
}

function reactToPost(postId, reactionId){
    fetch('/comunidad/react', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({postId, reactionId})
    }).then(res => res.json()).then(data => {
        if (data.status === 'error') {
            alert('Error: ' + data.message());
        } else {
            loadPostReactions(postId);
        }
    });
}