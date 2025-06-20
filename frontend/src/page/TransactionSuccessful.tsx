import { useLocation, useNavigate } from "react-router-dom";
import "../css/TransactionSuccessful.css";

export default function TransactionSuccessful() {
    const navigate = useNavigate();
    const location = useLocation();
    const transactionId =
        location.state?.transactionId || localStorage.getItem("lastTransactionId");
    return (
        <main className="success-page" role="main" aria-live="polite">
            <section className="success-card" aria-label="Transaction success message">
                <span className="success-icon" aria-hidden="true"></span>
                <h1>Transaction Successful!</h1>
                <div className="transaction-id-row" aria-label="Transaction ID">
                    <p>Your transaction ID is:</p>
                    <p
                        style={{
                            fontWeight: "bold",
                            fontSize: "1.2rem",
                            marginLeft: "0.5rem",
                            marginTop: 0,
                        }}
                        aria-atomic="true"
                    >
                        {transactionId ?? "Not available"}
                    </p>
                </div>
                <button onClick={() => navigate("/dashboard")}>Go to Dashboard</button>
            </section>
        </main>
    );
}
