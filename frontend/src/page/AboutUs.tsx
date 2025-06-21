import { useNavigate } from "react-router-dom";
import "../css/AboutUs.css";

export default function AboutUs() {
    const navigate = useNavigate();

    const handleBack = () => {
        navigate("/");
    };

    return (
        <main className="about-section">
            <div className="about-content">
                <button className="back-button" onClick={handleBack}>← Back to Home</button>
                <h2>About Us</h2>
                <p>
                    MyBank is dedicated to providing modern, user-friendly, and secure financial solutions.
                    The aim is to empower individuals with digital tools to easily manage their finances,
                    and make fast, reliable transactions anytime, anywhere.
                </p>
            </div>
        </main>
    );
}
