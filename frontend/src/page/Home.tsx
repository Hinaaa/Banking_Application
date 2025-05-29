import {useNavigate} from "react-router-dom";

export default function Home() {

const navigate = useNavigate()
    const handleLogin = () => {
        navigate("/login")
    }
    const handleRegister = () => {
        navigate("/register")
    }

    return (
        <>
            <>Welcome to Home Page</>
            <button onClick={handleLogin}>Login</button>
            <button onClick={handleRegister}>Register</button>
        </>
    )
}

//const [email, setEmail] = useState("") //enter input field
//onChange={(e) => setEmail(e.target.value)}