const profileCircle = document.getElementById("profileCircle");
const profileDropdown = document.getElementById("profileDropdown");
const createPostButton = document.getElementById("createPostButton");

profileCircle.addEventListener("click", function () {
    profileDropdown.classList.toggle("active");
});

document.addEventListener("click", function (event) {
    if (!event.target.closest(".profile-container")) {
        profileDropdown.classList.remove("active");
    }
});

createPostButton.addEventListener("click", function () {
    window.location.href = "post-write.html";
});

const logoutButton = document.getElementById("logoutButton");

logoutButton.addEventListener("click", function (event) {
    event.preventDefault();
    localStorage.clear();
    window.location.href = "login.html";
});

let currentPage = 0;
const pageSize = 10;
let hasNext = true;
let isLoading = false;

const postsList = document.getElementById("postsList");

document.addEventListener("DOMContentLoaded", function () {
    postsList.innerHTML = "";
    loadPosts();
});

async function loadPosts() {
    if (!hasNext || isLoading) {
        return;
    }

    isLoading = true;

    try {
        const response = await fetch(
            `http://localhost:8080/api/v1/posts?page=${currentPage}&size=${pageSize}`
        );

        const result = await response.json();

        if (!response.ok) {
            alert(result.message);
            return;
        }

        const pageData = result.data;
        renderPosts(pageData.posts);

        hasNext = pageData.hasNext;
        currentPage += 1;

    } catch (error) {
        console.error("Posts fetch error:", error);
        alert("Failed to load posts.");
    } finally {
        isLoading = false;
    }
}

function renderPosts(posts) {
    posts.forEach(post => {
        const postItem = document.createElement("div");
        postItem.className = "post-item";

        postItem.innerHTML = `
            <div class="post-header">
                <div class="post-title" data-post-id="${post.id}">
                    ${post.title}
                </div>
            </div>

            <div class="post-author">
                <div class="author-circle">
                    ${post.profileImageUrl
                        ? `<img src="${post.profileImageUrl}" alt="profile">`
                        : post.nickname.charAt(0).toUpperCase()}
                </div>
                <div class="author-info">
                    ${post.nickname} • ${formatDate(post.createdAt)}
                </div>
            </div>

            <div class="post-stats">
                <div class="stat">👍 <span>${post.likeCount}</span></div>
                <div class="stat">👁️ <span>${post.viewCount}</span></div>
                <div class="stat">💬 <span>${post.commentCount}</span></div>
            </div>
        `;

        postItem.querySelector(".post-title").addEventListener("click", function () {
            window.location.href = `post-detail.html?postId=${post.id}`;
        });

        postsList.appendChild(postItem);
    });
}

window.addEventListener("scroll", function () {
    const scrollTop = window.scrollY;
    const windowHeight = window.innerHeight;
    const documentHeight = document.documentElement.scrollHeight;

    if (scrollTop + windowHeight >= documentHeight - 100) {
        loadPosts();
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