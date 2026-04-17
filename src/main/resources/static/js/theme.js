(function() {
    const prefsStr = localStorage.getItem("prefs");
    console.log("Preferencias recibidas:", prefsStr);
    if (!prefsStr) return;
    
    try {
        const prefs = JSON.parse(prefsStr);
        
        // aplicar tema de color
        if (prefs.menuMainColor !== undefined) {
            const themes = ['', 'azul', 'verde', 'morado'];
            const theme = themes[prefs.menuMainColor] || '';
            document.documentElement.setAttribute('data-theme', theme);
        }
        
        // aplicar tamaño de letra
        if (prefs.textSizeLevel !== undefined) {
            const sizes = ['small', 'normal', 'large'];
            document.documentElement.setAttribute('data-font', 
                sizes[prefs.textSizeLevel] || 'normal');
        }
    } catch (e) {}
})();

document.addEventListener("DOMContentLoaded", function () {
    // userId=1 hasta tener usuarios
    const userId = window.currentUserId || 1;
    fetch(`/api/preferences?userId=${userId}`)
        .then(res => res.json())
        .then(pref => {
            if (pref && pref.menuMainColor !== undefined) {
                switch(pref.menuMainColor) {
                    case 1: document.documentElement.setAttribute('data-theme','azul'); break;
                    case 2: document.documentElement.setAttribute('data-theme','verde'); break;
                    case 3: document.documentElement.setAttribute('data-theme','morado'); break;
                    default: document.documentElement.setAttribute('data-theme','marron'); break;
                }
            }
            if (pref && pref.textSizeLevel !== null && pref.textSizeLevel !== undefined) {
                let sizeToken = 'normal';
                if (pref.textSizeLevel === 0) sizeToken = 'small';
                else if (pref.textSizeLevel === 1) sizeToken = 'normal';
                else if (pref.textSizeLevel === 2) sizeToken = 'large';
                document.documentElement.setAttribute('data-font', sizeToken);
            }
        })
        .catch(() => { });
});

function changeTheme(theme, userId = window.currentUserId || 1) {
    document.documentElement.setAttribute("data-theme", theme);
    let code = 0;
    if (theme === 'azul') code = 1;
    else if (theme === 'verde') code = 2;
    else if (theme === 'morado') code = 3;

    // actualizar localStorage
    const prefs = JSON.parse(localStorage.getItem("prefs") || "{}");
    prefs.menuMainColor = code;
    localStorage.setItem("prefs", JSON.stringify(prefs));

    const body = { menuMainColor: code };
    fetch(`/api/preferences?userId=${userId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    }).catch(() => {});
}

function changeFontSize(size, userId = window.currentUserId || 1) {
    let level = 1;
    let sizeToken = 'normal';
    if (size === 'small') { level = 0; sizeToken = 'small'; }
    else if (size === 'normal') { level = 1; sizeToken = 'normal'; }
    else if (size === 'large') { level = 2; sizeToken = 'large'; }
    document.documentElement.setAttribute("data-font", sizeToken);

    // actualizar localStorage
    const prefs = JSON.parse(localStorage.getItem("prefs") || "{}");
    prefs.textSizeLevel = level;
    localStorage.setItem("prefs", JSON.stringify(prefs));

    const body = { textSizeLevel: Number(level) };
    fetch(`/api/preferences?userId=${userId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    }).catch(() => {});
}