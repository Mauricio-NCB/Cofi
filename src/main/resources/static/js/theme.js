document.addEventListener("DOMContentLoaded", function () {

    const savedTheme = localStorage.getItem("theme");
    const savedFontSize = localStorage.getItem("fontSize");

    if (savedTheme) {
        document.documentElement.setAttribute("data-theme", savedTheme);
    }

    if (savedFontSize) {
        document.documentElement.setAttribute("data-font", savedFontSize);
    }

});

function changeTheme(theme) {
    document.documentElement.setAttribute("data-theme", theme);
    localStorage.setItem("theme", theme);
}

function changeFontSize(size) {
    document.documentElement.setAttribute("data-font", size);
    localStorage.setItem("fontSize", size);
}