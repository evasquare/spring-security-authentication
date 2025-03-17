import { useForm, SubmitHandler } from "react-hook-form";
import { getCsrfToken, validateSession } from "../lib/utils";
import { SERVER_URL } from "../lib/variables";
import { useEffect, useState } from "react";
import styled from "styled-components";

const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`;

const Title = styled.h1`
    color: black;
`;

const Form = styled.form`
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 280px;

    margin-bottom: 30px;
`;

const InputContainer = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
    margin-bottom: 5px;
`;

const Input = styled.input`
    width: 67%;
`;

const LoginSubmit = styled.input`
    margin-top: 8px;
`;

const ErrorMessage = styled.span`
    color: tomato;
    margin-bottom: 12px;
`;

interface Inputs {
    username: string;
    password: string;
}

const Login = () => {
    useEffect(() => {
        (async () => {
            if (await validateSession()) {
                window.location.href = "/";
            }
        })();
    }, []);

    const [errorMessage, setErrorMessage] = useState<string>("");
    const { register, handleSubmit } = useForm<Inputs>();

    const onSubmit: SubmitHandler<Inputs> = async (data) => {
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
            window.location.href = "/";
        } catch (error: unknown) {
            const serverNotRespondingMessages = [
                "NetworkError when attempting to fetch resource.",
                "Failed to fetch",
                "Load failed",
            ];
            if (error instanceof Error) {
                if (serverNotRespondingMessages.includes(error.message)) {
                    setErrorMessage("Server is not responding...");
                } else {
                    setErrorMessage(error.message);
                }
            }
        }
    };

    return (
        <Wrapper>
            <Title>Login</Title>
            <ErrorMessage>{errorMessage}</ErrorMessage>
            <Form onSubmit={handleSubmit(onSubmit)}>
                <InputContainer>
                    <label htmlFor="username">Username</label>
                    <Input {...register("username", { required: true })} />
                </InputContainer>
                <InputContainer>
                    <label htmlFor="password">Password</label>
                    <Input
                        {...register("password", { required: true })}
                        type="password"
                    />
                </InputContainer>
                <LoginSubmit type="submit" />
            </Form>
            <span>
                Don't have an account? You can <a href="/join">make one here</a>
                !
            </span>
        </Wrapper>
    );
};

export default Login;
