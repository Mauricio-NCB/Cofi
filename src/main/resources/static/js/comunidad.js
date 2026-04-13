document.addEventListener("DOMContentLoaded", function () {

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

    /* ====== FORMULARIO DE CREACIÓN DE POST ====== */
    const createPostForm = document.getElementById('createPostFormElement');
    if (createPostForm) {
        createPostForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = new FormData();
            formData.append('title', document.getElementById('postTitle').value);
            formData.append('imageUrl', document.getElementById('postImageUrl').value);
            formData.append('content', document.getElementById('postContent').value);
            formData.append('tags', Array.from(selectedTags).join(","));
            
            const submitBtn = document.getElementById('submitPostBtn');
            const originalText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.textContent = 'Publicando...';
            
            try {
                const response = await fetch('/comunidad/crear', {
                    method: 'POST',
                    body: formData
                });
                
                if (response.ok) {
                    showNotification('✓ Post publicado correctamente', 'success');
                    createPostForm.reset();
                    selectedTags.clear();
                    renderTags();
                    toggleCreatePostForm();
                    
                    // Recargar posts después de 1 segundo
                    setTimeout(() => location.reload(), 1000);
                } else {
                    const text = await response.text();
                    if (text.includes('url')) {
                        showNotification('❌ La URL debe comenzar con http:// o https://', 'error');
                    } else if (text.includes('url_long')) {
                        showNotification('❌ La URL es muy larga (máx 100 caracteres)', 'error');
                    } else {
                        showNotification('❌ Error al publicar el post', 'error');
                    }
                    submitBtn.disabled = false;
                    submitBtn.textContent = originalText;
                }
            } catch (error) {
                console.error('Error:', error);
                showNotification('❌ Error de conexión', 'error');
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }
        });
    }

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

    // listener para añadir tag con Enter
    const tagInput = document.getElementById("tagInput");
    if (tagInput) {
        tagInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                addTag();
            }
        });
    }


});

/* ====== GESTIÓN DE TAGS ====== */
const selectedTags = new Set();

function addTag() {
    const input = document.getElementById("tagInput");
    const tag = input.value.trim().toLowerCase();

    if (!tag) return;

    if (selectedTags.size >= 3) {
        alert("Máximo 3 tags permitidos");
        return;
    }

    if (selectedTags.has(tag)) {
        alert("Este tag ya está añadido");
        return;
    }

    selectedTags.add(tag);
    renderTags();
    input.value = "";
}

function removeTag(tag) {
    selectedTags.delete(tag);
    renderTags();
}

function renderTags() {
    const container = document.getElementById("tagsContainer");
    container.innerHTML = "";
    selectedTags.forEach(tag => {
        const card = document.createElement("div");
        card.className = "badge bg-primary me-1 tag-card";
        card.innerHTML = `
            <span>${tag}</span>
            <span class="remove-tag" onclick="removeTag('${tag}')" style="cursor: pointer; margin-left: 6px; font-weight: bold;">✕</span>
        `;
        container.appendChild(card);
    });
    
    // Actualizar el input oculto con los tags
    document.getElementById('tagsInput').value = Array.from(selectedTags).join(',');
}

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