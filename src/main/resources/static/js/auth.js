// Añadir el token a todas las peticiones fetch
const originalFetch = window.fetch;
window.fetch = async function(url, options = {}) {
    const token = localStorage.getItem("accessToken");
    if (token) {
        options.headers = {
            ...options.headers,
            "Authorization": `Bearer ${token}`
        };
    }
    let response = await originalFetch(url, options);

    // Si el token expiró intentar renovarlo
    if (response.status === 401) {
        const refreshToken = localStorage.getItem("refreshToken");
        if (refreshToken) {
            const refreshRes = await originalFetch(
                `/auth/refresh?refreshToken=${refreshToken}`, 
                { method: "POST" }
            );
            if (refreshRes.ok) {
                const data = await refreshRes.json();
                localStorage.setItem("accessToken", data.accessToken);
                localStorage.setItem("refreshToken", data.refreshToken);
                // reintentar la petición original con el nuevo token
                options.headers["Authorization"] = `Bearer ${data.accessToken}`;
                response = await originalFetch(url, options);
            } else {
                // refresh token expirado, redirigir al login
                localStorage.clear();
                window.location.href = "/auth/login";
            }
        }
    }
    return response;
};

// Cargar nombre del usuario en el dashboard
const userStr = localStorage.getItem("user");
if (userStr) {
    const user = JSON.parse(userStr);
    const el = document.getElementById("userName");
    if (el) el.textContent = user.name + " " + user.lastname;
}