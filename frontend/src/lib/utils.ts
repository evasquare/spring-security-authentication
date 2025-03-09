import { SERVER_URL } from "./variables";

export const getCsrfToken = () => {
    const cookieValue = document.cookie
        .split("; ")
        .find((row) => row.startsWith("XSRF-TOKEN="))
        ?.split("=")[1];
    return cookieValue ? decodeURIComponent(cookieValue) : null;
};

export const validateSession = async () => {
    try {
        const csrfToken = getCsrfToken();
        const response = await fetch(`${SERVER_URL}/auth/validation`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": csrfToken ?? "",
            },
            credentials: "include",
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        return Boolean(response.text());
    } catch (error: unknown) {
        if (error instanceof Error) {
            console.log(error.message);
        }
    }
};
