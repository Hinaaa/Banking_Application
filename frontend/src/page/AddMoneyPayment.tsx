    import {useEffect, useState} from "react";
    import { useNavigate } from "react-router-dom";
    import {fetchCurrentBalance, getAccountDetails, Transaction} from "../service/apiService.tsx";
    import "../CSS/Transaction.css";

    export default function AddMoneyPayment() {
        const [amount, setAmount] = useState(0)
        const [reference, setReference] = useState("")
        //Radio Button
        const [selectTransactionType, setSelectTransactionType] = useState<"bankTransfer" | "cardTransfer" | "">("");
        //IBAN Details
        const [accountHolderName, setAccountHolderName] = useState("")
        const [iban, setIban] = useState("")
        const [bic, setBic] = useState("")

        //Card Details:
        const [cardNumber, setCardNumber] = useState("")
        const [cardHolder, setCardHolder] = useState("")
        const [expiryDate, setExpiryDate] = useState("")
        const [cvv, setCvv] = useState("")
        const navigate = useNavigate() //for navigation

        //Message to display to user
        const [responseMessage, setResponseMessage] = useState<{
            message: string,
            type: "success" | "error" | "info"
        } | null>(null);

        const [loading, setLoading] = useState(false) //for loading while account fetching
        const [accountId, setAccountId] = useState(0)
        const [userId, setUserId] = useState(0)
        const [balance, setBalance] = useState(0);

// ① On mount (or when userId/accountId known), pull the balance:
        useEffect(() => {
            const uid = Number(localStorage.getItem("currentUserId"));
            if (!uid) return;
            fetchCurrentBalance(uid)
                .then(b => setBalance(b.accountBalance))
                .catch(() => setBalance(0));
        }, []);
        // fetch the user’s primary accountId
        useEffect(() => {
            const storeUserId = localStorage.getItem("currentUserId") //get userId from localStorage means stored when login
            if (!storeUserId) {
                setResponseMessage({message: "User not Found", type: "error"})
                return
            }

            const userId = Number(storeUserId) //login user id stored, converting to number as required by api
            setUserId(userId)
            setLoading(true) //loading

            getAccountDetails(userId) //get account detail as per user id(getAccountDetails imported from service), account must exist, that handled in dashboard
                .then((accountDetails) => {
                    setAccountId(accountDetails.accountId)
                   // setBalance(accountDetails.accountBalance);
                    // localStorage.setItem("currentBalance", accountDetails.accountBalance.toString());
                })
                .catch((err: unknown) => {
                    if (err instanceof Error) {
                        setResponseMessage({message: err.message, type: "error"});
                    } else {
                        setResponseMessage({message: "account not found", type: "error"})
                    }
                })
                .finally(() => {
                    setLoading(false)
                })
        }, []);

        //storing from front end, building to get a field: transactionFromToAccountDetails because set in backend to store in table
        const detailsOtherAccOrCard =
            selectTransactionType === "bankTransfer"
                ? `Account Holder: ${accountHolderName}, IBAN: ${iban}, BIC: ${bic}`
                : `Card Number: ${cardNumber}, Card Holder: ${cardHolder}, Expiry: ${expiryDate}`;

        //Handle adding account details
        const handleAddMoney = async () => {
            if (!selectTransactionType) {
                setResponseMessage({message: "Please select the transaction type", type: "error"})
                return;
            }
            if (amount <= 0) {
                setResponseMessage({message: "Please enter a valid amount", type: "error"});
                return;
            }

            if (selectTransactionType === "bankTransfer") {
                if (!accountHolderName || !iban || !bic) {
                    setResponseMessage({message: "Please fill all required fields", type: "error"})
                    return;
                }
            }
            if (selectTransactionType === "cardTransfer") {
                if (!cardNumber || !cardHolder || !expiryDate || !cvv) {
                    setResponseMessage({message: "Please fill all required card fields", type: "error"});
                    return;
                }
            }
            //building payload: to send back to backed
            const payload = {
                userId,
                accountId,
                amount,
                description: reference,
                transactionType: selectTransactionType,
                transactionFromToAccountDetails: detailsOtherAccOrCard,
            }
            // const newTx = await Transaction(payload);
            // setBalance(newTx.updatedBalance);

            //axios
            try {
                const newTransaction = await Transaction(payload); // Call API after building payload
                setBalance(newTransaction.updatedBalance);
                const key = `currentBalance_${accountId}`;
                localStorage.setItem(key, newTransaction.updatedBalance.toString());
                //when all validation passed
                setResponseMessage({
                    message: "Transaction successful. Amount has been added to your Account",
                    type: "success"
                });

                //send id to next page to show
                if (newTransaction && newTransaction.transactionId !== undefined && newTransaction.transactionId !== null) {
                    localStorage.setItem("lastTransactionId", newTransaction.transactionId.toString());
                    navigate("/transactionsuccessful", {
                        state: { transactionId: newTransaction.transactionId },
                    });
                } else {
                    setResponseMessage({
                        message: "Transaction succeeded but no ID was returned",
                        type: "info",
                    });
                }
            } catch (error: unknown) {
                if (error instanceof Error && error.message) {
                    setResponseMessage({message: error.message, type: "error"}); // Display actual error message
                } else {
                    setResponseMessage({message: "Transaction failed. Please try again.", type: "error"}); // Fallback message
                }
            }
        }
        return (
            <div className="add-amount">
                <div className="balance-box">
                    <label htmlFor="current-balance ">Current Balance:</label>
                    <output id="current-balance">
                        {balance !== null
                            ? balance.toLocaleString(undefined, { style: 'currency', currency: 'EUR' })
                            : '—'}
                    </output>
                </div>
                <h2>Add Money to your Account</h2>

                <div className="amount-form">
                    <label htmlFor="amount-label">Amount </label>
                    <input
                        type="number"
                        placeholder="Amount to add"
                        value={amount}
                        onChange={(e) => setAmount(Number(e.target.value))}
                    />

                    <div className="radio-group"> {/* Added class for styling */}
                        <label htmlFor="bank-transfer-radio">
                            <input
                                id="bank-transfer-radio" // **Added id for accessibility**
                                type="radio"
                                name="transactionType"
                                value="bankTransfer"
                                checked={selectTransactionType === "bankTransfer"}
                                onChange={(e) => setSelectTransactionType(e.target.value as "bankTransfer")}
                            />
                            <span>Add via Bank Transfer</span>
                        </label>
                        <label htmlFor="card-transfer-radio">
                            <input
                                id="card-transfer-radio" // **Added id for accessibility**
                                type="radio"
                                name="transactionType"
                                value="cardTransfer"
                                checked={selectTransactionType === "cardTransfer"}
                                onChange={(e) => setSelectTransactionType(e.target.value as "cardTransfer")}
                            />
                            <span>Add via Card Details</span>
                        </label>
                    </div>

                    {selectTransactionType === "bankTransfer" && (
                        <div className="add-money-transaction-by-iban">
                            <h3>IBAN Details of Account from Which You want to Transfer Money to Your Account</h3>
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
                </div>
                <div className="reference-comments">
                    <label htmlFor="reference-comments-label">Reference </label>
                    <input
                        type="text"
                        placeholder="Reference comments"
                        value={reference}
                        onChange={(e) => setReference(String(e.target.value))}
                    />
                </div>
                {loading && <p>Loading account details...</p>}
                <button onClick={handleAddMoney}>Add Money</button>

                {responseMessage && (
                    <div style={{color: responseMessage.type === "error" ? "red" : "green"}}>
                        {responseMessage.message}
                    </div>
                )}
            </div>
        )
    }