import { useEffect, useState } from "react";
import { getCsrfToken } from "./lib/csrf";
import { SERVER_URL } from "./lib/variables";

const User = () => {
    const [username, setUsername] = useState<string | null>(null);

    useEffect(() => {
        (async () => {
            setUsername(await getUsername());
        })();
    }, []);

    const getUsername = async () => {
        const response = await fetch(`${SERVER_URL}/auth/get-username`, {
            method: "GET",
            credentials: "include",
        });
        return await response.text();
    };

    const logOut = async () => {
        try {
            const csrfToken = getCsrfToken();
            await fetch(`${SERVER_URL}/auth/logout`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-XSRF-TOKEN": csrfToken ?? "",
                },
                credentials: "include",
            });

            window.location.href = "/";
        } catch (error) {
            if (error instanceof Error) {
                console.log(error.message);
            }
        }
    };

    return (
        <>
            <h1>Welcome {username ?? ""}</h1>
            <button onClick={getUsername}>Get Username</button>
            <button onClick={logOut}>Logout</button>
        </>
    );
};

export default User;
