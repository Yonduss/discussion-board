import api from "./api.js";

const loginForm = document.getElementById("login-form");
const signupButton = document.getElementById("signup-button");

loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
        const result = await api.post(
            "/api/v1/auth/login",
            {
                email,
                password
            },
            {
                auth: false
            }
        );

        console.log("Login success:", result);

        localStorage.setItem("loginUserEmail", result.data.email);
        localStorage.setItem("accessToken", result.data.accessToken);

        window.location.href = "posts.html";

    } catch (error) {
        console.error("Login error:", error);
        alert(error.message || "Server connection failed.");
    }
});

signupButton.addEventListener("click", function () {
    window.location.href = "signup.html";
});