import {useState} from "react";
import { useNavigate } from "react-router-dom";

export default function AddMoneyPayment() {
    const [amount, setAmount] = useState(0)
    const navigate = useNavigate()
    //Radio Button
    const [selectTransactionType, setSelectTransactionType] = useState("")
    //IBAN Details
    const [accountHolderName, setAccountHolderName] = useState("")
    const [iban, setIban] = useState("")
    const [bic, setBic] = useState("")

    //Card Details:
    const [cardNumber, setCardNumber] = useState("")
    const [cardHolder, setCardHolder] = useState("")
    const [expiryDate, setExpiryDate] = useState("")
    const [cvv, setCvv] = useState("")

    //Message to display to user
    const [responseMessage, setResponseMessage] = useState<{ message: string, type: "Success" | "error" | "info" } | null>(null);

    //Handle adding account details
    const handleAddMoney = () => {
        if (!selectTransactionType) {
            setResponseMessage({ message: "Please select the transaction type", type: "error" })
            return;
        }
        if (amount <= 0) {
            setResponseMessage({ message: "Please enter a valid amount", type: "error" });
            return;
        }
        if (selectTransactionType === "bankTransfer") {
            if (!accountHolderName || !iban || !bic || !cardNumber || !cardHolder || !expiryDate || !cvv) {
                setResponseMessage({message: "Please fill all required fields", type: "error"})
                return;
            }
        }
        if (selectTransactionType === "cardTransfer") {
            if (!cardNumber || !cardHolder || !expiryDate || !cvv) {
                setResponseMessage({ message: "Please fill all required card fields", type: "error" });
                return;
            }
        }
            //when all validation passed
            setResponseMessage({ message: "Transaction initiated successfully", type: "Success" })
            navigate("/addmoney")
    }
        return (
            <div className="add-amount">
                <h2>Add Money to your account</h2>

                <div className="amount-form">
                    <label htmlFor="amount-label">Add Amount</label>
                    <input
                        type="number"
                        placeholder="Amount to add"
                        value={amount}
                        onChange={(e) => setAmount(Number(e.target.value))}
                    />
                </div>

                <div>
                    <label>
                        <input
                            type="radio"
                            name="transactionType"
                            value="bankTransfer"
                            checked={selectTransactionType === "bankTransfer"}
                            onChange={(e) => setSelectTransactionType(e.target.value)}
                        />
                        Add via Bank Transfer
                    </label>
                    <label>
                        <input
                            type="radio"
                            name="cardTransfer"
                            value="cardTransfer"
                            checked={selectTransactionType === "cardTransfer"}
                            onChange={(e) => setSelectTransactionType(e.target.value)}
                        />
                        Add via Card Details
                    </label>
                </div>

                {selectTransactionType === "bankTransfer" && (
                    <div className="add-money-transaction-by-iban">
                        <h3>IBAN Details of Account from Which You want to Transfer Money to Your Accoun</h3>
                        <input
                            type="text"
                            placeholder="Account Holder Name *"
                            value={accountHolderName}
                            onChange={(e) => setAccountHolderName(e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder="IBAN *"
                            value={iban}
                            onChange={(e) => setIban(e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder="BIC *"
                            value={bic}
                            onChange={(e) => setBic(e.target.value)}
                        />
                    </div>
                )}
                {selectTransactionType === "cardTransfer" && (
                    <div className="add-money-transaction-by-card">
                        <h3>Card Details from Which You Want to Transfer Money</h3>
                        <input
                            type="text"
                            placeholder="Card Number *"
                            value={cardNumber}
                            onChange={(e) => setCardNumber(e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder="Card Holder *"
                            value={cardHolder}
                            onChange={(e) => setCardHolder(e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder="Expiry Date (MM/YY) *"
                            value={expiryDate}
                            onChange={(e) => setExpiryDate(e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder="CVV *"
                            value={cvv}
                            onChange={(e) => setCvv(e.target.value)}
                        />
                    </div>
                )}
                <button onClick={handleAddMoney}>Add Money</button>

                {responseMessage && (
                    <div style={{ color: responseMessage.type === "error" ? "red" : "green" }}>
                        {responseMessage.message}
                    </div>
                )}
            </div>
        );
    }