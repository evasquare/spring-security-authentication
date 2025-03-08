export const getCsrfToken = () => {
    const cookieValue = document.cookie
        .split("; ")
        .find((row) => row.startsWith("XSRF-TOKEN="))
        ?.split("=")[1];
    return cookieValue ? decodeURIComponent(cookieValue) : null;
};
