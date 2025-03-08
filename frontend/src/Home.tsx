import { useForm, SubmitHandler } from "react-hook-form";
import { getCsrfToken } from "./lib/csrf";
import { SERVER_URL } from "./lib/variables";
import { useState } from "react";

interface Inputs {
    username: string;
    password: string;
}

const Home = () => {
    // todo: if user is already logged in, redirect to User page.

    const [errorMessage, setErrorMessage] = useState<string>("");
    const { register, handleSubmit } = useForm<Inputs>();

    const onSubmit: SubmitHandler<Inputs> = async (data) => {
        console.log(data);
        try {
            const csrfToken = getCsrfToken();
            const response = await fetch(`${SERVER_URL}/auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-XSRF-TOKEN": csrfToken ?? "",
                },
                credentials: "include",
                body: JSON.stringify({
                    username: data.username,
                    password: data.password,
                }),
            });
            if (!response.ok) {
                throw new Error(await response.text());
            }
            window.location.href = "/user";
        } catch (error: unknown) {
            if (error instanceof Error) {
                setErrorMessage(error.message);
            }
        }
    };

    return (
        <>
            <h1>Login</h1>
            {errorMessage}
            <form onSubmit={handleSubmit(onSubmit)}>
                <input {...register("username", { required: true })} />
                <input {...register("password", { required: true })} />

                <input type="submit" />
            </form>

            <a href="/join">Join?</a>
        </>
    );
};

export default Home;
