const params = new URLSearchParams(window.location.search);
const postId = params.get("postId");
const loginUserId = localStorage.getItem("loginUserId");

const profileCircle = document.getElementById("profileCircle");
const profileDropdown = document.getElementById("profileDropdown");
const logoutButton = document.getElementById("logoutButton");

const editPostButton = document.getElementById("editPostButton");
const deletePostButton = document.getElementById("deletePostButton");
const likeButton = document.getElementById("likeButton");

const commentInput = document.getElementById("commentInput");
const addCommentButton = document.getElementById("addCommentButton");
const commentList = document.getElementById("commentList");

if (!postId) {
    alert("Post id is missing.");
    window.location.href = "posts.html";
}

if (!loginUserId) {
    alert("Please login first.");
    window.location.href = "login.html";
}

loadPostDetail();
loadComments();

async function loadPostDetail() {
    const response = await fetch(`http://localhost:8080/api/v1/posts/${postId}`);
    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        window.location.href = "posts.html";
        return;
    }

    const post = result.data;

    document.getElementById("postTitle").textContent = post.title;
    document.getElementById("postContent").textContent = post.content;
    document.getElementById("authorName").textContent = post.nickname;
    document.getElementById("postTime").textContent = formatDate(post.createdAt);
    document.getElementById("likeCount").textContent = post.likeCount;
    document.getElementById("viewCount").textContent = `Views: ${post.viewCount}`;
    document.getElementById("commentCount").textContent = `Comments: ${post.commentCount}`;

    renderPostImages(post.postImageUrls || []);
}

async function loadComments() {
    const response = await fetch(`http://localhost:8080/api/v1/posts/${postId}/comments`);
    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        return;
    }

    renderComments(result.data.comments);
}

function renderComments(comments) {
    commentList.innerHTML = "";

    comments.forEach(comment => {
        const commentItem = document.createElement("div");
        commentItem.className = "comment-item";

        commentItem.innerHTML = `
            <div class="comment-header">
                <div class="comment-author-circle">👤</div>
                <div class="comment-author-info">
                    <div class="comment-author-name">${comment.nickname}</div>
                    <div class="comment-time">${formatDate(comment.createdAt)}</div>
                </div>
                <div class="comment-actions">
                    <button class="edit-comment-button" data-comment-id="${comment.id}">Edit</button>
                    <button class="delete-comment-button" data-comment-id="${comment.id}">Delete</button>
                </div>
            </div>
            <div class="comment-body">${comment.content}</div>
        `;

        commentList.appendChild(commentItem);
    });
}

function renderPostImages(imageUrls) {
    const postImages = document.getElementById("postImages");
    postImages.innerHTML = "";

    imageUrls.forEach(url => {
        const img = document.createElement("img");
        img.src = url;
        img.alt = "Post image";
        img.className = "post-image";
        postImages.appendChild(img);
    });
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

editPostButton.addEventListener("click", function () {
    window.location.href = `post-edit.html?postId=${postId}`;
});

deletePostButton.addEventListener("click", async function () {
    if (!confirm("Are you sure you want to delete this post?")) {
        return;
    }

    const response = await fetch(
        `http://localhost:8080/api/v1/posts/${loginUserId}/${postId}`,
        { method: "DELETE" }
    );

    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        return;
    }

    alert("Post deleted successfully.");
    window.location.href = "posts.html";
});

likeButton.addEventListener("click", async function () {
    const response = await fetch(
        `http://localhost:8080/api/v1/posts/${loginUserId}/${postId}/likes`,
        { method: "POST" }
    );

    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        return;
    }

    await loadPostDetail();
});

addCommentButton.addEventListener("click", async function () {
    const content = commentInput.value.trim();

    if (!content) {
        alert("Please enter a comment.");
        return;
    }

    try {
        const response = await fetch(
            `http://localhost:8080/api/v1/posts/${postId}/comments/${loginUserId}`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    content: content
                })
            }
        );

        const result = await response.json();

        if (!response.ok) {
            alert(result.message);
            return;
        }

        commentInput.value = "";

        await loadComments();
        await loadPostDetail();
    } catch (error) {
        console.error(error);
        alert("Failed to add a comment.");
    }
});

function formatDate(dateString) {
    const date = new Date(dateString);

    return date.toLocaleString("ko-KR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    });
}

commentList.addEventListener("click", async function (event) {
    const editButton = event.target.closest(".edit-comment-button");
    const deleteButton = event.target.closest(".delete-comment-button");

    if (editButton) {
        const commentId = editButton.dataset.commentId;

        const currentContent = editButton
            .closest(".comment-item")
            .querySelector(".comment-body")
            .textContent
            .trim();

        const newContent = prompt("Edit comment:", currentContent);

        if (!newContent || !newContent.trim()) {
            return;
        }

        await updateComment(commentId, newContent.trim());
    }

    if (deleteButton) {
        const commentId = deleteButton.dataset.commentId;

        if (!confirm("Delete this comment?")) return;

        await deleteComment(commentId);
    }
});

async function updateComment(commentId, content) {
    const response = await fetch(
        `http://localhost:8080/api/v1/posts/${postId}/comments/${loginUserId}/${commentId}`,
        {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ content })
        }
    );

    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        return;
    }

    await loadComments();
}

async function deleteComment(commentId) {
    const response = await fetch(
        `http://localhost:8080/api/v1/posts/${postId}/comments/${loginUserId}/${commentId}`,
        {
            method: "DELETE"
        }
    );

    const result = await response.json();

    if (!response.ok) {
        alert(result.message);
        return;
    }

    await loadComments();
    await loadPostDetail();
}

const backButton = document.getElementById("backButton");

backButton.addEventListener("click", function (event) {
    event.preventDefault();
    history.back();
});