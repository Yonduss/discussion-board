import api, { requireLogin } from "./api.js";
import { setupProfileDropdown, setupLogout } from "./common.js";

const passwordEditForm = document.getElementById("passwordEditForm");
const currentPasswordInput = document.getElementById("currentPassword");
const newPasswordInput = document.getElementById("newPassword");
const confirmPasswordInput = document.getElementById("confirmPassword");

requireLogin();

setupProfileDropdown();
setupLogout();

passwordEditForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const currentPassword = currentPasswordInput.value.trim();
    const newPassword = newPasswordInput.value.trim();
    const newPasswordConfirm = confirmPasswordInput.value.trim();

    if (!currentPassword || !newPassword || !newPasswordConfirm) {
        alert("All fields are required to change your password.");
        return;
    }

    if (currentPassword === newPassword) {
        alert("New password is same as old password.");
        return;
    }

    if (newPassword !== newPasswordConfirm) {
        alert("New passwords do not match.");
        return;
    }

    await api.patch(
        `/api/v1/users/password`,
        {
            currentPassword,
            newPassword,
            newPasswordConfirm
        }
    );

    alert("Password changed successfully.");
    window.location.href = "posts.html";
});