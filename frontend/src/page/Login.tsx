import {useState} from "react";
import {useNavigate} from "react-router-dom";
import * as React from 'react';
import {loginUser} from "../service/apiService.tsx";

export default function Login() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [responseMessage, setResponseMessage] = useState<{ message: string, type: "Success" | "error" | "info" } | null>(null); //just initilizing and defing values here
    const navigate = useNavigate();
    const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()

        if (!email) {
            setResponseMessage({ message: "Please enter Email", type: "error" });
            return;
        }
        if (!password) {
            setResponseMessage({message: "Please enter Password", type: "error"});
            return;
        }
        setResponseMessage(null); // Clearing previous messages before sending the request

        try {
            const data = await loginUser(email, password);
            setResponseMessage({message: data.message, type: "Success"}); //response received from backend
            const userId = data.userId
            localStorage.setItem("currentUserId", String(userId))

            //  if(data.profileCompleted) {
            navigate("/registeraccount")//("/dashboard") //my url to navigates once i develop page
           // }
           // else {
             //   navigate()
            //}
        } catch (error: unknown) {
            if (error instanceof Error) {
                setResponseMessage({ message: error.message, type: "error" });
            } else {
                setResponseMessage({ message: "An unexpected error occurred", type: "error" });
            }
        }
    };

    return (
        <>
            <div className="auth-form">
                <h2>Login</h2>
                <form onSubmit={handleLogin}>
                    <label htmlFor="email">Email:</label>
                    <input type={"email"} placeholder={"email *"} value={email}
                           onChange={(e) => setEmail(e.target.value)}
                    />
                    <label htmlFor="password">Password:</label>
                    <input type={"password"} placeholder={"password *"} value={password}
                           onChange={(e) => setPassword(e.target.value)}
                    />
                    <button type="submit">Login</button>
                    {responseMessage && (
                        <div className={`message ${responseMessage.type.toLowerCase()}`}>
                            {responseMessage.message}
                        </div>
                    )}
                </form>
            </div>
        </>
    )
}
