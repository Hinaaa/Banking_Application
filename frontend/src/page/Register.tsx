import {useState} from "react";
import {useNavigate} from "react-router-dom";
import * as React from 'react';
import {loginUser} from "../service/apiService.tsx";
import "../css/Register.css";

export default function Register() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [confirmNewPassword, setConfirmNewPassword] = useState("")
    const [error, setError] = useState("")
    const [responseMessage, setResponseMessage] = useState<{message: string; type: "success" | "error" | "info"; } | null>(null);
    const navigate = useNavigate();
    const handleBack = () => {
        navigate(-1); // this goes back one page in history
    };
    const handleRegistration = async (e: React.FormEvent<HTMLFormElement>) => {// async so it can await the backend check
        e.preventDefault()
        if (!email) {
            setError("Pleas enter Email");
            return;
        }
        if (!password) {
            setError("Please enter Password");
            return;
        }
        if (!confirmNewPassword) {
            setError("Please enter Confirm Password");
            return;
        }
        if (password !== confirmNewPassword) {
            setError("Passwords do not match");
            return;
        }
        setError("");
        try {
            const data = await loginUser(email, password); // loginUser fails if user not registered

            // if login is successful (i.e., user exists), then we stop and show message
            if (data.isRegistered === true) {
                setResponseMessage({ message: "Email Id already in Use", type: "error" });
            }
        } catch (err: unknown) {
            if (err instanceof Error) {
                console.error("Login error:", err.message);
            } else {
                console.error("Unknown error:", err);
            }
            //assuming user is not registered
            navigate("/registerdetail", {
                state: {
                    email,
                    password,
                },
            });
        }
    };

    return (
        <div className="auth-wrapper">
            <div className="auth-form">
                <h2>Register User</h2>
                <form onSubmit={handleRegistration}>
                    <label htmlFor="email">Email:</label>
                    <input
                        type="email"
                        placeholder="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <label htmlFor="password">Set Password:</label>
                    <input
                        type="password"
                        placeholder="new password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <label htmlFor="password">Confirm Password:</label>
                    <input
                        type="password"
                        placeholder="re-type password"
                        value={confirmNewPassword}
                        onChange={(e) => setConfirmNewPassword(e.target.value)}
                    />
                    <button type="submit">Continue</button>

                    {error && <div className="error-message">{error}</div>}

                    {responseMessage && (
                        <div className={`message ${responseMessage.type.toLowerCase()}`}>
                            {responseMessage.message}
                        </div>
                    )}
                </form>
            </div>
            <div><button onClick={handleBack} className="back-button">← Back</button></div>
        </div>
    );
}
