import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import * as React from 'react';
import {loginUser} from "../service/apiService.tsx";

export default function Login() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [responseMessage, setResponseMessage] = useState<{message: string; type: "success" | "error" | "info"; } | null>(null);    const navigate = useNavigate();
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
            localStorage.setItem("currentUserId", String(data.userId)); //store the returned userId in localStorage so others can read it
            //user registered and have account
            if (data.hasAccount === true){ //registered
                console.log(data.hasAccount)
                navigate("/dashboard") //user registered and have account go to dashboard
                return; //stopped as navigated to dashboard
            }
            //user registered but dont have account
              if (data.hasAccount === false){
                  console.log(data.hasAccount)
                  navigate("/registeraccount") //user registered and has no account goto register account page
                  return; //stopped as moved to register account
              }
             //error
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
                    <div><p>Don't have an account? <Link to="/register">Register User</Link></p></div>
                </form>
            </div>
        </>
    )
}
