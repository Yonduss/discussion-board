const topProfileCircle = document.getElementById("profileCircle");
const profileDropdown = document.getElementById("profileDropdown");
const logoutButton = document.getElementById("logoutButton");

const profileImageContainer = document.getElementById("profileImageContainer");
const profileUrlBox = document.getElementById("profileUrlBox");
const profileImageUrlInput = document.getElementById("profileImageUrl");
const editProfileCircle = document.getElementById("editProfileCircle");

const emailInput = document.getElementById("email");
const nicknameInput = document.getElementById("nickname");
const userEditForm = document.getElementById("userEditForm");
const deleteAccountBtn = document.getElementById("deleteAccountBtn");

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

profileImageContainer.addEventListener("click", function () {
    profileUrlBox.classList.toggle("show");
    profileImageUrlInput.focus();
});

profileImageUrlInput.addEventListener("keydown", function (event) {
    if (event.key !== "Enter") {
        return;
    }

    event.preventDefault();
    renderProfileImage(profileImageUrlInput.value.trim());
    profileUrlBox.classList.remove("show");
});

loadUser();

async function loadUser() {
    const response = await fetch(`http://localhost:8080/api/v1/users/${loginUserId}`);
    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        return;
    }

    const user = result.data;

    emailInput.value = user.email;
    nicknameInput.value = user.nickname;
    profileImageUrlInput.value = user.profileImageUrl || "";

    renderProfileImage(user.profileImageUrl);
}

function renderProfileImage(imageUrl) {
    if (!imageUrl) {
        editProfileCircle.classList.add("empty");
        editProfileCircle.innerHTML = "";
        return;
    }

    editProfileCircle.classList.remove("empty");
    editProfileCircle.innerHTML = `<img src="${imageUrl}" alt="Profile">`;
}

userEditForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const nickname = nicknameInput.value.trim();
    const profileImageUrl = profileImageUrlInput.value.trim();

    if (!nickname) {
        alert("Nickname is required.");
        return;
    }

    const response = await fetch(`http://localhost:8080/api/v1/users/${loginUserId}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            nickname,
            profileImageUrl: profileImageUrl || null
        })
    });

    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        return;
    }

    localStorage.setItem("loginUserNickname", result.data.nickname);
    localStorage.setItem("loginUserProfileImageUrl", result.data.profileImageUrl || "");

    alert("Profile updated successfully.");
    window.location.href = "posts.html";
});

deleteAccountBtn.addEventListener("click", async function () {
    if (!confirm("Are you sure you want to delete your account?")) {
        return;
    }

    const response = await fetch(`http://localhost:8080/api/v1/users/${loginUserId}`, {
        method: "DELETE"
    });

    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        return;
    }

    localStorage.clear();
    alert("Account deleted successfully.");
    window.location.href = "login.html";
});