const topProfileCircle = document.getElementById("profileCircle");
const profileDropdown = document.getElementById("profileDropdown");
const logoutButton = document.getElementById("logoutButton");

const passwordEditForm = document.getElementById("passwordEditForm");
const currentPasswordInput = document.getElementById("currentPassword");
const newPasswordInput = document.getElementById("newPassword");
const confirmPasswordInput = document.getElementById("confirmPassword");

const loginUserId = localStorage.getItem("loginUserId");

if (!loginUserId) {
    alert("Please login first.");
    window.location.href = "login.html";
}

topProfileCircle.addEventListener("click", function () {
    profileDropdown.classList.toggle("active");
});

document.addEventListener("click", function (event) {
    if (!event.target.closest(".profile-container")) {
        profileDropdown.classList.remove("active");
    }
});

logoutButton.addEventListener("click", function (event) {
    event.preventDefault();
    localStorage.clear();
    window.location.href = "login.html";
});

passwordEditForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const currentPassword = currentPasswordInput.value.trim();
    const newPassword = newPasswordInput.value.trim();
    const newPasswordConfirm = confirmPasswordInput.value.trim();

    if (!currentPassword || !newPassword || !newPasswordConfirm) {
        alert("All password fields are required.");
        return;
    }

    if (newPassword !== newPasswordConfirm) {
        alert("New passwords do not match.");
        return;
    }

    const response = await fetch(`http://localhost:8080/api/v1/users/${loginUserId}/password`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            currentPassword,
            newPassword,
            newPasswordConfirm
        })
    });

    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        return;
    }

    alert("Password changed successfully.");
    window.location.href = "posts.html";
});