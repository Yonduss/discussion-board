const loginForm = document.getElementById("login-form");
const signupButton = document.getElementById("signup-button");

loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    console.log("Login clicked");
    console.table({ email, password });

    try {
        const response = await fetch("http://localhost:8080/api/v1/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const result = await response.json();

        if (!response.ok) {
            alert(result.message);
            return;
        }

        console.log("Login success:", result);

        localStorage.setItem("loginUserId", result.data.id);
        localStorage.setItem("loginUserEmail", result.data.email);
        localStorage.setItem("loginUserNickname", result.data.nickname);
        localStorage.setItem("loginUserProfileImageUrl", result.data.profileImageUrl);

        window.location.href = "posts.html";

    } catch (error) {
        console.error("Login error:", error);
        alert("Server connection failed.");
    }
});

signupButton.addEventListener("click", function () {
    window.location.href = "signup.html";
});