function isTokenExpired(token) {
    if (!token) return true;

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.exp * 1000 < Date.now();
    } catch (e) {
        return true;
    }
}

// Intentar renovar el token si ha expirado
async function checkAndRefreshToken(requireAuth = false) {
    const token = localStorage.getItem("accessToken");

    if (isTokenExpired(token)) {
        const refreshToken = localStorage.getItem("refreshToken");

        if (!refreshToken) {

            if(requireAuth) {
                localStorage.clear();
                document.cookie = "accessToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
                window.location.href = "/auth/login";
            }
            
            return;
        }

        try {
            const res = await originalFetch(`/auth/refresh?refreshToken=${refreshToken}`, {
                method: "POST"
            });

            if (res.ok) {
                const data = await res.json();
                localStorage.setItem("accessToken", data.accessToken);
                localStorage.setItem("refreshToken", data.refreshToken);
                localStorage.setItem("user", JSON.stringify(data.user));
                
                document.cookie = `accessToken=${data.accessToken}; path=/; SameSite=Strict`;
                window.location.reload();
            } else {
                localStorage.clear();
                document.cookie = "accessToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
                window.location.href = "/auth/login";
            }
        } catch (e) {
            localStorage.clear();
            document.cookie = "accessToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
            window.location.href = "/auth/login";
        }
    }
}

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

                document.cookie = `accessToken=${data.accessToken}; path=/; SameSite=Strict`;
                // reintentar la petición original con el nuevo token
                options.headers["Authorization"] = `Bearer ${data.accessToken}`;
                response = await originalFetch(url, options);
            } else {
                // refresh token expirado, redirigir al login
                localStorage.clear();
                document.cookie = "accessToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
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
    const userName = document.getElementById("userName");
    if (userName) userName.textContent = user.name + " " + user.lastname;
}