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
                        />
                        <label htmlFor="iban">IBAN:</label>
                        <input
                            type="text"
                            placeholder="IBAN *"
                            value={iban}
                            onChange={(e) => setIban(e.target.value)}
                        />
                        <label htmlFor="bic">BIC:</label>
                        <input
                            type="text"
                            placeholder="BIC (Bank Identifier Code)"
                            value={bic}
                            onChange={(e) => setBic(e.target.value)}
                        />
                    </div>

                    <div className="card-block">
                        <h3>Card Details</h3>
                        <label htmlFor="cardNumber">Card Number:</label>
                        <input
                            type="text"
                            placeholder="Card Number *"
                            value={cardNumber}
                            onChange={(e) => setCardNumber(e.target.value)}
                        />

                        <label htmlFor="cardHolder">Card Holder Name:</label>
                        <input
                            type="text"
                            placeholder="Card Holder Name *"
                            value={cardHolder}
                            onChange={(e) => setCardHolder(e.target.value)}
                        />

                        <label htmlFor="expiryDate">Expiry Date (MM/YY):</label>
                        <input
                            type="text"
                            placeholder="MM/YY *"
                            value={expiryDate}
                            onChange={(e) => setExpiryDate(e.target.value)}
                        />

                        <label htmlFor="cvv">CVV:</label>
                        <input
                            type="password"
                            placeholder="CVV *"
                            value={cvv}
                            onChange={(e) => setCvv(e.target.value)}
                        />
                    </div>

                    <div className="pin-block">
                        <label htmlFor="pin">Registration PIN:</label>
                        <input
                            type="text"
                            placeholder="Registration PIN *"
                            value={pin}
                            onChange={(e) => setPin(e.target.value)}
                        />
                    </div>
                    <button type="submit">Register Account and Proceed</button>
                    {responseMessage && (
                        <div className={`message ${responseMessage.type.toLowerCase()}`}>
                            {responseMessage.message}
                        </div>
                    )}
                </form>
            </div>
        </div>
    );
}