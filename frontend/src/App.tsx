import "./App.css";

function App() {
    const getCsrfToken = () => {
        const cookieValue = document.cookie
            .split("; ")
            .find((row) => row.startsWith("XSRF-TOKEN="))
            ?.split("=")[1];
        return cookieValue ? decodeURIComponent(cookieValue) : null;
    };

    const login = async () => {
        const csrfToken = getCsrfToken();
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": csrfToken ?? "",
            },
            credentials: "include",
            body: JSON.stringify({
                username: "user12",
                password: "pass",
            }),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        console.log(await response.text());
    };

    const getUsername = async () => {
        const csrfToken = getCsrfToken();
        const response = await fetch(
            "http://localhost:8080/auth/get-username",
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "X-XSRF-TOKEN": csrfToken ?? "",
                },
                credentials: "include",
            }
        );
        console.log(await response.text());
    };

    const logOut = async () => {
        const csrfToken = getCsrfToken();
        const response = await fetch("http://localhost:8080/auth/logout", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": csrfToken ?? "",
            },
            credentials: "include",
        });
        console.log(await response.text());
    };
    return (
        <>
            <h1>Test Application</h1>
            <button onClick={login}>Login</button>
            <button onClick={getUsername}>Get Username</button>
            <button onClick={logOut}>Logout</button>
        </>
    );
}

export default App;
