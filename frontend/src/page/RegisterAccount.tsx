import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerAccountDetails } from "../service/apiService";
import "../css/RegisterAccount.css";

export default function RegisterAccount() {
    const navigate = useNavigate();

    // IBAN Details
    const [accountHolderName, setAccountHolderName] = useState("");
    const [iban, setIban] = useState("");
    const [bic, setBic] = useState("");

    // Card Details
    const [cardNumber, setCardNumber] = useState("");
    const [cardHolder, setCardHolder] = useState("");
    const [expiryDate, setExpiryDate] = useState("");
    const [cvv, setCvv] = useState("");

    // PIN
    const [pin, setPin] = useState("");

    //loading
    const [loading, setLoading] = useState(false);

    // Response and focus
    const [responseMessage, setResponseMessage] = useState<{ message: string; type: "Success" | "error" | "info" } | null>(null);
    const [focusedField, setFocusedField] = useState<string | null>(null);

    // Validation hints for focus
    const validationHints: Record<string, string> = {
        accountHolderName: "Required: Name of account holder.",
        iban: "Required: 15–34 uppercase letters and digits (spaces allowed).",
        bic: "Optional: Bank Identifier Code (usually 8 or 11 characters).",
        cardNumber: "Required: Card number is 16 digits (spaces allowed).",
        cardHolder: "Required: Name on the card.",
        expiryDate: "Required: Format MM/YY, e.g. 09/27.",
        cvv: "Required: Exactly 3 digits.",
        pin: "Please enter the registration PIN provided to you."
    };

    const handleBack = () => {
        navigate(-1);
    };

    const handleAccountRegister = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
       //loading
        if (loading) return; // Prevent multiple submits while loading
        setLoading(true);
        setResponseMessage(null);

        // Basic required check
        if (!accountHolderName || !iban || !cardNumber || !cardHolder || !expiryDate || !cvv || !pin) {
            setResponseMessage({ message: "Please fill all required fields", type: "error" });
            setLoading(false);
            return;
        }
        setResponseMessage(null);

        // Get userId
        const userId = Number(localStorage.getItem("currentUserId"));
        if (!userId) {
            setResponseMessage({ message: "User not logged in", type: "error" });
            setLoading(false);
            return;
        }

        // Validate IBAN (remove spaces)
        const ibanNoSpaces = iban.replace(/\s+/g, "");
        if (!/^[A-Z0-9]{15,34}$/.test(ibanNoSpaces)) {
            setResponseMessage({
                message: "Invalid IBAN format. It should contain 15 to 34 alphanumeric characters (spaces allowed).",
                type: "error",
            });
            return;
        }

        // Validate card number
        const cardNumberNoSpaces = cardNumber.replace(/\s+/g, "");
        if (!/^\d{16}$/.test(cardNumberNoSpaces)) {
            setResponseMessage({
                message: "Invalid card number format. It should contain exactly 16 digits (spaces allowed).",
                type: "error",
            });
            setLoading(false);
            return;
        }

        // Validate expiry date
        if (!/^\d{2}\/\d{2}$/.test(expiryDate)) {
            setResponseMessage({ message: "Expiry date must be in MM/YY format", type: "error" });
            setLoading(false);
            return;
        }

        // Validate CVV
        if (!/^\d{3}$/.test(cvv)) {
            setResponseMessage({ message: "Invalid CVV. It must be exactly 3 digits.", type: "error" });
            setLoading(false);
            return;
        }

        // All validations passed; call API
        try {
            const data = await registerAccountDetails({
                userId,
                accountHolderName,
                iban,
                bic,
                cardNumber,
                cardHolder,
                expiryDate,
                cvv,
                pin,
            });
            setResponseMessage({ message: data.message, type: "Success" });
            navigate("/dashboard");
        }catch (error: unknown) {
            if (error instanceof Error) {
                if (
                    error.message.includes("Network Error") ||
                    error.message.includes("Failed to fetch")
                ) {
                    setResponseMessage({ message: "Cannot connect to backend. Please try again later.", type: "error" });
                } else {
                    setResponseMessage({ message: error.message, type: "error" });
                }
            } else {
                setResponseMessage({ message: "An unexpected error occurred", type: "error" });
            }
        } finally {
            setLoading(false);
        }
    };

    // A helper to render hint under an input if focused
    const renderHint = (fieldName: string) => {
        if (focusedField === fieldName && validationHints[fieldName]) {
            return <div className="input-hint">{validationHints[fieldName]}</div>;
        }
        return null;
    };

    return (
        <div className="auth-wrapper">
            <div>
                <div>
                    <button onClick={handleBack} className="back-button">
                        ← Back
                    </button>
                </div>
                {loading && <div className="loading-message">Please wait...</div>}
                <div className="auth-form">
                    <h1>Welcome to account registration</h1>
                    <form onSubmit={handleAccountRegister}>
                        <div className="iban-block">
                            <h3>IBAN Details</h3>

                            <label htmlFor="accountHolderName">Account Holder Name:</label>
                            <input
                                id="accountHolderName"
                                type="text"
                                placeholder="Account Holder Name *"
                                value={accountHolderName}
                                onChange={(e) => setAccountHolderName(e.target.value)}
                                onFocus={() => setFocusedField("accountHolderName")}
                                onBlur={() => setFocusedField(null)}
                            />
                            {renderHint("accountHolderName")}

                            <label htmlFor="iban">IBAN:</label>
                            <input
                                id="iban"
                                type="text"
                                placeholder="IBAN *"
                                value={iban}
                                onChange={(e) => setIban(e.target.value.toUpperCase())}
                                onFocus={() => setFocusedField("iban")}
                                onBlur={() => setFocusedField(null)}
                            />
                            {renderHint("iban")}

                            <label htmlFor="bic">BIC:</label>
                            <input
                                id="bic"
                                type="text"
                                placeholder="BIC (Bank Identifier Code)"
                                value={bic}
                                onChange={(e) => setBic(e.target.value)}
                                onFocus={() => setFocusedField("bic")}
                                onBlur={() => setFocusedField(null)}
                            />
                            {renderHint("bic")}
                        </div>

                        <div className="card-block">
                            <h3>Card Details</h3>

                            <label htmlFor="cardNumber">Card Number:</label>
                            <input
                                id="cardNumber"
                                type="text"
                                placeholder="Card Number *"
                                value={cardNumber}
                                onChange={(e) => setCardNumber(e.target.value)}
                                onFocus={() => setFocusedField("cardNumber")}
                                onBlur={() => setFocusedField(null)}
                            />
                            {renderHint("cardNumber")}

                            <label htmlFor="cardHolder">Card Holder Name:</label>
                            <input
                                id="cardHolder"
                                type="text"
                                placeholder="Card Holder Name *"
                                value={cardHolder}
                                onChange={(e) => setCardHolder(e.target.value)}
                                onFocus={() => setFocusedField("cardHolder")}
                                onBlur={() => setFocusedField(null)}
                            />
                            {renderHint("cardHolder")}

                            <label htmlFor="expiryDate">Expiry Date (MM/YY):</label>
                            <input
                                id="expiryDate"
                                type="text"
                                placeholder="MM/YY *"
                                value={expiryDate}
                                onChange={(e) => setExpiryDate(e.target.value)}
                                onFocus={() => setFocusedField("expiryDate")}
                                onBlur={() => setFocusedField(null)}
                            />
                            {renderHint("expiryDate")}

                            <label htmlFor="cvv">CVV:</label>
                            <input
                                id="cvv"
                                type="password"
                                placeholder="CVV *"
                                value={cvv}
                                onChange={(e) => setCvv(e.target.value)}
                                onFocus={() => setFocusedField("cvv")}
                                onBlur={() => setFocusedField(null)}
                            />
                            {renderHint("cvv")}
                        </div>

                        <div className="pin-block">
                            <label htmlFor="pin">Registration PIN:</label>
                            <input
                                id="pin"
                                type="text"
                                placeholder="Registration PIN *"
                                value={pin}
                                onChange={(e) => setPin(e.target.value)}
                                onFocus={() => setFocusedField("pin")}
                                onBlur={() => setFocusedField(null)}
                            />
                            {renderHint("pin")}
                        </div>

                        <button type="submit" disabled={loading}>
                            {loading ? "Registering..." : "Register Account and Proceed"}
                        </button>

                        {responseMessage && (
                            <div className={`message ${responseMessage.type.toLowerCase()}`}>
                                {responseMessage.message}
                            </div>
                        )}
                    </form>
                </div>
            </div>
        </div>
    );
}
