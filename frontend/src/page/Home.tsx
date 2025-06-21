import { useNavigate, Link } from "react-router-dom";
import heroImage from "../assets/images/HomeImage.png";
import "../css/Home.css";

export default function Home() {
    const navigate = useNavigate();

    const handleLogin = () => {
        navigate("/login");
    };

    const handleRegister = () => {
        navigate("/register");
    };

    return (
        <>
            <header className="header">
                <h1 className="logo">MyBank</h1>
                <nav className="nav-bar">
                    <ul className="nav-links">
                        <li><Link to="/about">About Us</Link></li>
                        <li><button onClick={handleLogin} className="btn small">Login</button></li>
                        <li><button onClick={handleRegister} className="btn small">Sign Up</button></li>
                    </ul>
                </nav>
            </header>
            <main>
                <section className="hero-section">
                    <div className="hero-left">
                        <h2>Welcome to MyBank</h2>
                        <h4>The Bank of Your Choice</h4>
                        <p>
                            Secure online banking. Manage your
                            accounts, Instant Bank Transfers,
                            Add and track your spending anytime.
                        </p>
                    </div>

                    <div className="hero-right">
                        <img src={heroImage} alt="Online banking services" />
                    </div>
                </section>
            </main>
        </>
    );
}
