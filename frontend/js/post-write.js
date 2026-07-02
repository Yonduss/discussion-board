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

const logoutButton = document.getElementById("logoutButton");

logoutButton.addEventListener("click", function (event) {
    event.preventDefault();
    localStorage.clear();
    window.location.href = "login.html";
});

const loginUserId = localStorage.getItem("loginUserId");

if (!loginUserId) {
    alert("Please login first.");
    window.location.href = "login.html";
}

document.getElementById("addImageButton").addEventListener("click", function () {
    const imageInputs = document.getElementById("imageInputs");

    const row = document.createElement("div");
    row.className = "image-input-row";

    const input = document.createElement("input");
    input.type = "url";
    input.name = "images[]";
    input.placeholder = "Image URL";

    row.appendChild(input);
    imageInputs.appendChild(row);
});

document.getElementById("postForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const title = document.getElementById("postTitle").value.trim();
    const content = document.getElementById("postContent").value.trim();

    const postImageUrls = Array.from(document.querySelectorAll('input[name="images[]"]'))
        .map(input => input.value.trim())
        .filter(url => url.length > 0);

    if (!title || !content) {
        alert("Title and content are required.");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/v1/posts/${loginUserId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                title,
                content,
                postImageUrls
            })
        });

        const result = await response.json();

        if (!response.ok) {
            alert(result.message);
            return;
        }

        alert("Post created successfully.");
        window.location.href = `post-detail.html?postId=${result.data.id}`;

    } catch (error) {
        console.error(error);
        alert("Failed to create post.");
    }
});