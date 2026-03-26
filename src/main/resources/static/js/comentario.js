document.addEventListener("DOMContentLoaded", function(){

    const form = document.getElementById("commentForm");

    if(!form) return;

    form.addEventListener("submit", async function(e){

        e.preventDefault();

        const postId = document.getElementById("modalPostId").value;
        const content = document.getElementById("commentContent").value;

        await fetch("/comunidad/comentario", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ postId: parseInt(postId), content: content, parentId: null })
        });

        form.reset();
        cargarComentarios(postId);
    });

});

async function cargarComentarios(postId){

    const response = await fetch("/comunidad/comentarios/" + postId);

    const comentarios = await response.json();

    const container = document.getElementById("commentsSection");

    container.innerHTML = "";

    comentarios.forEach(c => {

        const comment = renderComment(c);

        container.appendChild(comment);

    });

}

function formatearFecha(fecha){

    const d = new Date(fecha);

    return d.toLocaleString("es-ES", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });

}

function renderComment(comment, level = 0){

    const div = document.createElement("div");

    div.style.marginLeft = (level * 25) + "px";

    div.className = "border rounded p-2 mb-2";

    div.innerHTML = `
        <strong>${comment.authorName}</strong>
        <span class="text-muted small">
            ${formatearFecha(comment.dateSent)}
        </span>

        <p>${comment.content}</p>

        <div class="reactions-container mt-1 mb-2" id="reactions-comment-${comment.id}"></div>

        <button class="btn btn-sm btn-link responder-btn">
            Responder
        </button>

        <div class="reply-form d-none mt-2">
            <textarea class="form-control mb-2"
                      placeholder="Escribe una respuesta"></textarea>

            <button class="btn btn-primary btn-sm enviar-respuesta">
                Enviar
            </button>
        </div>
    `;
    
    const btnResponder = div.querySelector(".responder-btn");
    const replyForm = div.querySelector(".reply-form");

    btnResponder.addEventListener("click", () => {
        replyForm.classList.toggle("d-none");
    });

    const enviarBtn = div.querySelector(".enviar-respuesta");
    const textarea = div.querySelector("textarea");

    enviarBtn.addEventListener("click", async () => {

        const content = textarea.value.trim();
        if(!content) return;

        const postId = document.getElementById("modalPostId").value;

        await fetch("/comunidad/comentario", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ postId: parseInt(postId), content: content, parentId: comment.id })
        });

        textarea.value = "";

        cargarComentarios(postId);
    });

    if(comment.replies && comment.replies.length > 0){

        comment.replies.forEach(r => {  

            const reply = renderComment(r, level + 1);

            div.appendChild(reply);

        });

    }

    // Cargar reacciones del comentario - obtener el contenedor del div que se acaba de crear
    const reactionsContainer = div.querySelector(`#reactions-comment-${comment.id}`);
    loadCommentReactions(comment.id, reactionsContainer);

    return div;
}

async function loadCommentReactions(commentId, container = null){
    if (!container) {
        container = document.getElementById(`reactions-comment-${commentId}`);
    }
    if (!container) return;

    container.innerHTML = '';

    const allReactions = await fetch('/comunidad/reacciones/disponibles').then(r => r.json());
    const commentReactionsData = await fetch(`/comunidad/reacciones/comment/${commentId}`).then(r => r.json());

    // Crear un mapa de reacciones por ID para búsqueda rápida
    const reactionMap = {};
    commentReactionsData.forEach(reaction => {
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
            reactToComment(commentId, r.id);
        });

        container.appendChild(btn);
    });
}

function reactToComment(commentId, reactionId){
    fetch('/comunidad/react/comment', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({commentId, reactionId})
    }).then(res => res.json()).then(data => {
        if (data.status === 'error') {
            alert('Error: ' + data.message);
        } else {
            loadCommentReactions(commentId);
        }
    });
}