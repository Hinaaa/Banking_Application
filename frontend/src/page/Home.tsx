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

                <nav className="nav-bar" aria-label="Main navigation">
                    <ul className="nav-links">
                        <li>
                            <Link to="/about" aria-label="About Us">
                                About Us
                            </Link>
                        </li>
                        <li>
                            <button onClick={handleLogin} className="btn small" aria-label="Login">
                                Login
                            </button>
                        </li>
                        <li>
                            <button onClick={handleRegister} className="btn small" aria-label="Sign Up">
                                Sign Up
                            </button>
                        </li>
                    </ul>
                </nav>
            </header>

            <main>
                <section className="hero-section">
                    <div className="hero-left">
                        <h2>Welcome to MyBank</h2>
                        <h4>The Bank of Your Choice</h4>
                        <p>
                            Secure online banking. Manage your accounts, instant bank transfers,
                            add and track your spending anytime.
                        </p>
                        <ul className="features-list">
                            <li>✓ 24/7 Account Access</li>
                            <li>✓ Real-Time Transfers</li>
                            <li>✓ Secure Transactions</li>
                        </ul>
                        <button onClick={handleRegister} className="btn large" aria-label="Open an Account">
                            Open an Account
                        </button>
                    </div>

                    <div className="hero-right">
                        <img
                            src={heroImage}
                            alt="Illustration of secure online banking"
                            loading="lazy"
                        />
                    </div>
                </section>
            </main>
        </>
    );
}
