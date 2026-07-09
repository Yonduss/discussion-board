import api, { requireLogin } from "./api.js";
import { setupProfileDropdown, setupLogout } from "./common.js";

const params = new URLSearchParams(window.location.search);
const postId = params.get("postId");

const postTitleInput = document.getElementById("postTitle");
const postContentInput = document.getElementById("postContent");
const imageInputs = document.getElementById("imageInputs");
const addImageButton = document.getElementById("addImageButton");
const postForm = document.getElementById("postForm");

if (!postId) {
    alert("Post id is missing.");
    window.location.href = "posts.html";
    throw new Error("Post id is missing.");
}

requireLogin();

setupProfileDropdown();
setupLogout();

loadPost();

async function loadPost() {
    try {
        const result = await api.get(
            `/api/v1/posts/${postId}`
        );

        const post = result.data;

        postTitleInput.value = post.title;
        postTitleInput.readOnly = true;

        postContentInput.value = post.content;

        renderImageInputs(post.postImageUrls || []);
    } catch (error) {
        console.error("Load post error:", error);
        alert(error.message || "Failed to load post.");
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
        await api.patch(
            `/api/v1/posts/${postId}`,
            {
                content,
                postImageUrls
            }
        );

        alert("Post updated successfully.");
        window.location.href = `post-detail.html?postId=${postId}`;
    } catch (error) {
        console.error(error);
        alert(error.message || "Failed to update post.");
    }
});