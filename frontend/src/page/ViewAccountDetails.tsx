import {useEffect, useState} from "react"
import type {AccountDetail} from "../types/AccountType.ts"
import { getAccountDetails } from "../service/apiService.tsx"
import { useSearchParams } from "react-router-dom";
import "../css/ViewAccountDetail.css";
import { useNavigate } from "react-router-dom";

export default function ViewAccountDetails() {
    const [account, setAccount] = useState<AccountDetail | null>(null)
    const [error, setError] = useState("");
    const [searchParams] = useSearchParams();
    const idParam = searchParams.get("user_id"); //should be same as in backend cont4roller
    const numericId = idParam ? Number(idParam) : NaN;
    const navigate = useNavigate();
    const handleBack = () => {
        navigate(-1); // this goes back one page in history
    };
    useEffect(() => {// Runs only once when the component opens
        if (!numericId || isNaN(numericId)) {
            setError("Invalid or missing userId in URL");
            return;
        }async function fetchAccount() {  //async function to call the backend
            try {
                //const response = await getAccountDetails(props.user_id);  //calls api
                const response = await getAccountDetails(numericId)
                if (response.hasAccount && response.accountDetail) {
                    setAccount(response.accountDetail) //account info stored
                } else {
                    setError("No account found")
                }
            } catch {
                setError("Failed to connect with backend.");// show this if backend fails
            }
        }
        fetchAccount() //call function)
    }, [numericId])
    if (error) {
        return <p>{error}</p>;
    }
    if(!account) {
       return <p>Loading..</p>
    }
    return (

        <div className="auth-wrapper">
            <div>
                <button onClick={handleBack} className="back-button">← Back</button>
            <div className="auth-form">
                <h2 className="form-heading">View Account Details</h2>
                <div className="accountDetail">
                    <h3>Account Details</h3>
                    <p>Account Holder: {account.accountHolderName}</p>
                    <p>IBAN: {account.iban}</p>
                    <p>BIC: {account.bic}</p>
                </div>

                <div className="carddetail">
                    <h3>Card Details</h3>
                    <p>Card Number: {account.cardNumber}</p>
                    <p>Card Holder: {account.cardHolder}</p>
                    <p>Expiry Date: {account.expiryDate}</p>
                    <p>CVV: {account.cvv}</p>
                    <p>PIN: {account.pin}</p>
                </div>
            </div>
        </div>
        </div>
    );
    }