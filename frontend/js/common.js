export function setupProfileDropdown() {
    const profileCircle = document.getElementById("profileCircle");
    const profileDropdown = document.getElementById("profileDropdown");

    profileCircle.addEventListener("click", function () {
        profileDropdown.classList.toggle("active");
    });

    document.addEventListener("click", function (event) {
        if (!event.target.closest(".profile-container")) {
            profileDropdown.classList.remove("active");
        }
    });
}

export function setupLogout() {
    const logoutButton = document.getElementById("logoutButton");

    logoutButton.addEventListener("click", function (event) {
        event.preventDefault();
        localStorage.clear();
        window.location.href = "login.html";
    });
}