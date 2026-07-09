import api, { requireLogin, formatDate } from "./api.js";
import { setupProfileDropdown, setupLogout } from "./common.js";

const params = new URLSearchParams(window.location.search);
const postId = params.get("postId");

const editPostButton = document.getElementById("editPostButton");
const deletePostButton = document.getElementById("deletePostButton");
const likeButton = document.getElementById("likeButton");

const commentInput = document.getElementById("commentInput");
const addCommentButton = document.getElementById("addCommentButton");
const commentList = document.getElementById("commentList");

if (!postId) {
    alert("Post id is missing.");
    window.location.href = "posts.html";
    throw new Error("Post id is missing.");
}

requireLogin();

setupProfileDropdown();
setupLogout();

loadPostDetail();
loadComments();

async function loadPostDetail() {
    try {
        const result = await api.get(
            `/api/v1/posts/${postId}`
        );

        const post = result.data;

        document.getElementById("postTitle").textContent = post.title;
        document.getElementById("postContent").textContent = post.content;
        document.getElementById("authorName").textContent = post.nickname;
        document.getElementById("postTime").textContent = formatDate(post.createdAt);
        document.getElementById("likeCount").textContent = post.likeCount;
        document.getElementById("viewCount").textContent = `Views: ${post.viewCount}`;
        document.getElementById("commentCount").textContent = `Comments: ${post.commentCount}`;

        renderPostImages(post.postImageUrls || []);
    } catch (error) {
        console.error("Post detail fetch error:", error);
        alert(error.message || "Failed to load post.");
        window.location.href = "posts.html";
    }
}

async function loadComments() {
    try {
        const result = await api.get(
            `/api/v1/posts/${postId}/comments`
        );

        renderComments(result.data.comments);
    } catch (error) {
        console.error("Comments fetch error:", error);
        alert(error.message || "Failed to load comments.");
    }
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

editPostButton.addEventListener("click", function () {
    window.location.href = `post-edit.html?postId=${postId}`;
});

deletePostButton.addEventListener("click", async function () {
    if (!confirm("Are you sure you want to delete this post?")) {
        return;
    }

    try {
        await api.delete(
            `/api/v1/posts/${postId}`
        );

        alert("Post deleted successfully.");
        window.location.href = "posts.html";
    } catch (error) {
        console.error("Delete post error:", error);
        alert(error.message || "Failed to delete post.");
    }
});

likeButton.addEventListener("click", async function () {
    try {
        await api.post(
            `/api/v1/posts/${postId}/likes`
        );

        await loadPostDetail();
    } catch (error) {
        console.error("Like error:", error);
        alert(error.message || "Failed to like post.");
    }

});

addCommentButton.addEventListener("click", async function () {
    const content = commentInput.value.trim();

    if (!content) {
        alert("Please enter a comment.");
        return;
    }

    try {
        await api.post(
            `/api/v1/posts/${postId}/comments`,
            {
                content
            }
        );

        commentInput.value = "";

        await loadComments();
        await loadPostDetail();
    } catch (error) {
        console.error("Adding comment error:", error);
        alert(error.message || "Failed to add a comment.");
    }
});

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
    try {
        await api.patch(
            `/api/v1/posts/${postId}/comments/${commentId}`,
            {
                content
            }
        );

        await loadComments();
    } catch (error) {
        console.error("Update comment error:", error);
        alert(error.message || "Failed to update comment.");
    }
}

async function deleteComment(commentId) {
    try {
        await api.delete(
            `/api/v1/posts/${postId}/comments/${commentId}`
        );

        await loadComments();
        await loadPostDetail();
    } catch (error) {
        console.error("Delete comment error:", error);
        alert(error.message || "Failed to delete comment.");
    }
}

const backButton = document.getElementById("backButton");

backButton.addEventListener("click", function (event) {
    event.preventDefault();
    history.back();
});