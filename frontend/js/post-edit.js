const profileCircle = document.getElementById("profileCircle");
const profileDropdown = document.getElementById("profileDropdown");
const logoutButton = document.getElementById("logoutButton");

const loginUserId = localStorage.getItem("loginUserId");

const params = new URLSearchParams(window.location.search);
const postId = params.get("postId");

const postTitleInput = document.getElementById("postTitle");
const postContentInput = document.getElementById("postContent");
const imageInputs = document.getElementById("imageInputs");
const addImageButton = document.getElementById("addImageButton");
const postForm = document.getElementById("postForm");

if (!loginUserId) {
    alert("Please login first.");
    window.location.href = "login.html";
}

if (!postId) {
    alert("Post id is missing.");
    window.location.href = "posts.html";
}

profileCircle.addEventListener("click", function () {
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

loadPost();

async function loadPost() {
    try {
        const response = await fetch(`http://localhost:8080/api/v1/posts/${postId}`);
        const result = await response.json();

        if (!response.ok) {
            alert(result.message);
            window.location.href = "posts.html";
            return;
        }

        const post = result.data;

        postTitleInput.value = post.title;
        postTitleInput.readOnly = true;

        postContentInput.value = post.content;

        renderImageInputs(post.postImageUrls || []);

    } catch (error) {
        console.error(error);
        alert("Failed to load post.");
    }
}

function renderImageInputs(imageUrls) {
    imageInputs.innerHTML = "";

    if (imageUrls.length === 0) {
        addImageInput("");
        return;
    }

    imageUrls.forEach(url => {
        addImageInput(url);
    });
}

function addImageInput(value) {
    const row = document.createElement("div");
    row.className = "image-input-row";

    const input = document.createElement("input");
    input.type = "url";
    input.name = "images[]";
    input.placeholder = "Image URL";
    input.value = value;

    row.appendChild(input);
    imageInputs.appendChild(row);
}

addImageButton.addEventListener("click", function () {
    addImageInput("");
});

postForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const content = postContentInput.value.trim();

    const postImageUrls = Array.from(document.querySelectorAll('input[name="images[]"]'))
        .map(input => input.value.trim())
        .filter(url => url.length > 0);

    if (!content) {
        alert("Content is required.");
        return;
    }

    try {
        const response = await fetch(
            `http://localhost:8080/api/v1/posts/${loginUserId}/${postId}`,
            {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    content,
                    postImageUrls
                })
            }
        );

        const result = await response.json();

        if (!response.ok) {
            alert(result.message);
            return;
        }

        alert("Post updated successfully.");
        window.location.href = `post-detail.html?postId=${postId}`;

    } catch (error) {
        console.error(error);
        alert("Failed to update post.");
    }
});