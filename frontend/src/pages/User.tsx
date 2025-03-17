import { useEffect, useState } from "react";
import { getCsrfToken, validateSession } from "../lib/utils";
import { SERVER_URL } from "../lib/variables";
import styled from "styled-components";

const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`;

const ButtonRow = styled.div`
    display: flex;
    flex-direction: row;
`;

const A = styled.a`
    color: inherit;
`;

const User = () => {
    const [username, setUsername] = useState<string | null>(null);

    useEffect(() => {
        (async () => {
            if (!(await validateSession())) {
                window.location.href = "/login";
            }
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
        <Wrapper>
            <h1>Welcome {username ?? ""}!</h1>
            <ButtonRow>
                <A href="/change-password">
                    <button>Change Password</button>
                </A>
                <button onClick={logOut}>Logout</button>
            </ButtonRow>
        </Wrapper>
    );
};

export default User;
