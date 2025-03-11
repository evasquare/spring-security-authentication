import { useEffect, useState } from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import { getCsrfToken, validateSession } from "../lib/utils";
import { SERVER_URL } from "../lib/variables";

interface Inputs {
    username: string;
    password: string;
}

const Join = () => {
    useEffect(() => {
        (async () => {
            if (await validateSession()) {
                window.location.href = "/user";
            }
        })();
    }, []);

    const [errorMessage, setErrorMessage] = useState<string>("");
    const { register, handleSubmit } = useForm<Inputs>();

    const onSubmit: SubmitHandler<Inputs> = async (data) => {
        try {
            const csrfToken = getCsrfToken();
            const response = await fetch(`${SERVER_URL}/auth/join`, {
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
            window.location.href = "/";
        } catch (error: unknown) {
            if (error instanceof Error) {
                setErrorMessage(error.message);
            }
        }
    };

    return (
        <>
            <h1>Join</h1>
            {errorMessage}
            <form onSubmit={handleSubmit(onSubmit)}>
                <input
                    id="username"
                    {...register("username", { required: true })}
                />
                <input {...register("password", { required: true })} />

                <input type="submit" />
            </form>

            <a href="/">Login?</a>
        </>
    );
};

export default Join;
