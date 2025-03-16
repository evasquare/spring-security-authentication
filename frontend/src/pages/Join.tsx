import { useEffect, useState } from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import { getCsrfToken, validateSession } from "../lib/utils";
import { SERVER_URL } from "../lib/variables";
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
    flex-direction: column;
    justify-content: space-between;
    width: 100%;
    margin-bottom: 5px;
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
    confirmationPassword: string;
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
                    confirmPassword: data.confirmationPassword,
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
        <Wrapper>
            <Title>Join</Title>
            <span>
                Do you want to <a href="/">login</a>?
            </span>
            <ErrorMessage>{errorMessage}</ErrorMessage>
            <Form onSubmit={handleSubmit(onSubmit)}>
                <InputContainer>
                    <label htmlFor="username">Username</label>
                    <input {...register("username", { required: true })} />
                </InputContainer>
                <InputContainer>
                    <label htmlFor="password">Password</label>
                    <input
                        {...register("password", { required: true })}
                        type="password"
                    />
                </InputContainer>
                <InputContainer>
                    <label htmlFor="confirmationPassword">
                        Confirm Password
                    </label>
                    <input
                        {...register("confirmationPassword", {
                            required: true,
                        })}
                        type="confirmationPassword"
                    />
                </InputContainer>
                <LoginSubmit type="submit" />
            </Form>
        </Wrapper>
    );
};

export default Join;
