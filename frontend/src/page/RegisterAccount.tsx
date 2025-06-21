import {useState} from "react";
import {useNavigate} from "react-router-dom";
import { registerAccountDetails } from "../service/apiService";
import "../css/RegisterAccount.css";

export default function RegisterAccount() {

    const navigate = useNavigate();
    //IBAN Details
    const [accountHolderName, setAccountHolderName] = useState("");
    const [iban, setIban] = useState("");
    const [bic, setBic] = useState("");

    //Card Details:
    const [cardNumber, setCardNumber] = useState("");
    const [cardHolder, setCardHolder] = useState("");
    const [expiryDate, setExpiryDate] = useState("");
    const [cvv, setCvv] = useState("");

    //Enter pin code for authentication
    const [pin,setPin] = useState(""); //this pin delivered to customer
    const [responseMessage, setResponseMessage] = useState<{ message: string, type: "Success" | "error" | "info" } | null>(null);
    const [focusedField, setFocusedField] = useState<string | null>(null);
    const [focusMessage, setFocusMessage] = useState<string | null>(null);
    const [focusMessagePin, setFocusMessagePin] = useState<string | null>(null);

    const validationHints: Record<string, string> = {
        accountHolderName: "Required: Name of account holder.",
        iban: "Required: 15–34 uppercase letters and digits.",
        bic: "Optional: Bank Identifier Code (usually 8 or 11 characters).",
        cardNumber: "Required: Card number is of 16 digits",
        cardHolder: "Required: Name on the card.",
        expiryDate: "Required. Format MM/YY, e.g. 09/27.",
        cvv: "Required. Exactly 3 digits."
    };
    const handleBack = () => {
        navigate(-1); // this goes back one page in history
    };
    //HandleAccountRegister
    const handleAccountRegister = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if(!accountHolderName || !iban || !bic || !cardNumber || !cardHolder || !expiryDate || !cvv || !pin) {
            setResponseMessage({ message: "Please fill all required fields", type: "error" });
            return;
        }
        setResponseMessage(null);
        //current user id read
        const userId = Number(localStorage.getItem("currentUserId"));
        if (!userId) {
            setResponseMessage({ message: "User not logged in", type: "error" });
            return;
        }
        //field validations
        const ibanNoSpaces = iban.replace(/\s+/g, "");  // Remove all spaces
        if (!/^[A-Z0-9]{15,34}$/.test(ibanNoSpaces)) {
            setResponseMessage({ message: "Invalid IBAN format. It should contain 15 to 34 alphanumeric characters (spaces allowed).", type: "error" });
            return;
        }

        const cardNumberNoSpaces = cardNumber.replace(/\s+/g, "");
        if (!/^\d{16}$/.test(cardNumberNoSpaces)) {
            setResponseMessage({ message: "Invalid card number format. It should contain exactly 16 digits (spaces allowed).", type: "error" });
            return;
        }

        if (!/^\d{2}\/\d{2}$/.test(expiryDate)) {
            setResponseMessage({ message: "Expiry date must be in MM/YY format", type: "error" });
            return;
        }

        if (!/^\d{3}$/.test(cvv)) {
            setResponseMessage({ message: "Invalid CVV. Only three digits allowed", type: "error" });
            return;
        }

        if (!/^\d{3}$/.test(cvv)) {
            setResponseMessage({ message: "Invalid CVV. It must be exactly 3 digits.", type: "error" });
            return;
        }
        //axios
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
                pin
            });
            setResponseMessage({ message: data.message, type: "Success" });
            navigate("/dashboard");
        } catch (error: unknown) {
            if (error instanceof Error) {
                setResponseMessage({ message: error.message, type: "error" });
            } else {
                setResponseMessage({ message: "An unexpected error occurred", type: "error" });
            }
        }
    };

    return (
        <div className="auth-wrapper">
            <div>
                <div><button onClick={handleBack} className="back-button">← Back</button></div>
            <div className="auth-form">
                <h1>Welcome to account registration</h1>
                <form onSubmit={handleAccountRegister}>
                    <div className="iban-block">
                        <h3>IBAN Details</h3>

                        <label htmlFor="accountHolder">Account Holder Name:</label>
                        <input
                            type="text"
                            placeholder="Account Holder Name *"
                            value={accountHolderName}
                            onChange={(e) => setAccountHolderName(e.target.value)}
                            onFocus={() => setFocusedField("accountHolderName")}
                            onBlur={() => setFocusedField(null)}
                        />

                        <label htmlFor="iban">IBAN:</label>
                        <input
                            type="text"
                            placeholder="IBAN *"
                            value={iban}
                            onChange={(e) => setIban(e.target.value.toUpperCase())} // optional: auto uppercase
                            onFocus={() => setFocusedField("iban")}
                            onBlur={() => setFocusedField(null)}
                        />

                        <label htmlFor="bic">BIC:</label>
                        <input
                            type="text"
                            placeholder="BIC (Bank Identifier Code)"
                            value={bic}
                            onChange={(e) => setBic(e.target.value)}
                            onFocus={() => setFocusedField("bic")}
                            onBlur={() => setFocusedField(null)}
                        />
                    </div>

                    {focusedField && (
                        <div className="input-hint">
                            {validationHints[focusedField]}
                        </div>
                    )}


                    <div className="card-block">
                        <h3>Card Details</h3>
                        <label htmlFor="cardNumber">Card Number:</label>
                        <input
                            type="text"
                            placeholder="Card Number *"
                            value={cardNumber}
                            onChange={(e) => setCardNumber(e.target.value)}
                            onFocus={() => setFocusedField("cardNumber")}
                            onBlur={() => setFocusedField(null)}
                        />
                        {focusedField && (
                            <div className="input-hint">
                                {validationHints[focusedField]}
                            </div>
                        )}

                        <label htmlFor="cardHolder">Card Holder Name:</label>
                        <input
                            type="text"
                            placeholder="Card Holder Name *"
                            value={cardHolder}
                            onChange={(e) => setCardHolder(e.target.value)}
                            onFocus={() => setFocusMessage("Card Number should be 16 digits. Spaces allowed.")}
                            onBlur={() => setFocusMessage(null)}
                        />

                        <label htmlFor="expiryDate">Expiry Date (MM/YY):</label>
                        <input
                            type="text"
                            placeholder="MM/YY *"
                            value={expiryDate}
                            onChange={(e) => setExpiryDate(e.target.value)}
                            onFocus={() => setFocusMessage("Expiry Date format: MM/YY. Example: 08/25")}
                            onBlur={() => setFocusMessage(null)}
                        />

                        <label htmlFor="cvv">CVV:</label>
                        <input
                            type="password"
                            placeholder="CVV *"
                            value={cvv}
                            onChange={(e) => setCvv(e.target.value)}
                            onFocus={() => setFocusMessage("PIN should contain 3 digits.")}
                            onBlur={() => setFocusMessage(null)}
                        />

                        {focusMessage && <div className="focus-message">{focusMessage}</div>}
                    </div>

                    <div className="pin-block">
                        <label htmlFor="pin">Registration PIN:</label>
                        <input
                            type="text"
                            placeholder="Registration PIN *"
                            value={pin}
                            onChange={(e) => setPin(e.target.value)}
                            onFocus={() => {
                                setFocusMessagePin("Please enter PIN ro registered, provided by government.");
                                setFocusedField(null);
                            }}
                            onBlur={() => setFocusMessagePin(null)}
                        />

                    </div>
                   {/* For Register PIN */}
                    {focusMessagePin && (
                        <div className="pin-input-hint">
                            {focusMessagePin}
                        </div>
                    )}
                    <button type="submit">Register Account and Proceed</button>
                    {responseMessage && (
                        <div className={`message ${responseMessage.type}`}>
                            {responseMessage.message}
                        </div>
                    )}
                </form>
            </div>
        </div>
        </div>
    );
}