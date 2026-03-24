document.addEventListener("DOMContentLoaded", function(){

    const form = document.querySelector("#postModal form");

    if(!form) return;

    form.addEventListener("submit", async function(e){

        e.preventDefault();

        const postId = document.getElementById("modalPostId").value;
        console.log("postId al enviar:", postId);
        const content = document.getElementById("commentContent").value;
        console.log("content al enviar:", content);

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

    return div;
}