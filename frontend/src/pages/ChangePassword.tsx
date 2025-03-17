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
    originalPassword: string;
    newPassword: string;
    newPasswordConfirmation: string;
}

const ChangePassword = () => {
    useEffect(() => {
        (async () => {
            if (!(await validateSession())) {
                window.location.href = "/";
            }
        })();
    }, []);

    const [errorMessage, setErrorMessage] = useState<string>("");
    const { register, handleSubmit } = useForm<Inputs>();

    const onSubmit: SubmitHandler<Inputs> = async (data) => {
        try {
            const csrfToken = getCsrfToken();
            const response = await fetch(`${SERVER_URL}/auth/change-password`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-XSRF-TOKEN": csrfToken ?? "",
                },
                credentials: "include",
                body: JSON.stringify({
                    originalPassword: data.originalPassword,
                    newPassword: data.newPassword,
                    newPasswordConfirmation: data.newPasswordConfirmation,
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
            <Title>Change Password</Title>
            <span>
                <a href="/">Go back</a>
            </span>
            <ErrorMessage>{errorMessage}</ErrorMessage>
            <Form onSubmit={handleSubmit(onSubmit)}>
                <InputContainer>
                    <label htmlFor="username">Original Password</label>
                    <input
                        {...register("originalPassword", { required: true })}
                        type="password"
                    />
                </InputContainer>
                <InputContainer>
                    <label htmlFor="password">New Password</label>
                    <input
                        {...register("newPassword", { required: true })}
                        type="password"
                    />
                </InputContainer>
                <InputContainer>
                    <label htmlFor="confirmationPassword">
                        Confirm New Password
                    </label>
                    <input
                        {...register("newPasswordConfirmation", {
                            required: true,
                        })}
                        type="password"
                    />
                </InputContainer>
                <LoginSubmit type="submit" />
            </Form>
        </Wrapper>
    );
};

export default ChangePassword;
