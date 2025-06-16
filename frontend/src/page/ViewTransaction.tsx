import {useLocation, useNavigate} from "react-router-dom";
import "../CSS/ViewTransaction.css";

export default function ViewTransaction() {
    const { state } = useLocation();
    const navigate = useNavigate();
    const transaction = state?.transaction;

    if (!transaction) {
        return (
            <div className="no-data-message">
                <p>
                    No transaction data. Please reload from dashboard or implement fallback fetch.
                </p>
                <span className="back-button" onClick={() => navigate(-1)}>
          ← Back
        </span>
            </div>
        );
    }
    return (
        <div className="view-transaction-container">
      <span className="back-button" onClick={() => navigate(-1)}>
        ← Back to Dashboard
      </span>
            <h2 className="view-transaction-title">Transaction Details</h2>
            <div className="transaction-details">
                <div className="transaction-row">
                    <span className="transaction-label">Transaction ID: </span>
                    <span className="transaction-value">{transaction.id}</span>
                </div>
                <div className="transaction-row">
                    <span className="transaction-label">Amount: </span>
                    <span className="transaction-value">{transaction.amount}</span>
                </div>
                <div className="transaction-row">
                    <span className="transaction-label">Transaction Direction: </span>
                    <span className="transaction-value">{transaction.transactionDirection}</span>
                </div>
                <div className="transaction-row">
                    <span className="transaction-label">Transaction Type: </span>
                    <span className="transaction-value">{transaction.transactionType}</span>
                </div>
                <div className="transaction-row">
                    <span className="transaction-label">Transaction Description: </span>
                    <span className="transaction-value">{transaction.description}</span>
                </div>
                <div className="transaction-row">
                    <span className="transaction-label">Transaction Date: </span>
                    <span className="transaction-value">
            {new Date(transaction.transactionDate).toLocaleString()}
          </span>
                </div>
                <div className="transaction-row">
                    <span className="transaction-label">Status: </span>
                    <span className="transaction-value">{transaction.status}</span>
                </div>
                <div className="transaction-row">
                    <span className="transaction-label">Transferred to Account: </span>
                    <span className="transaction-value">
            {transaction.transactionFromToAccountDetails}
          </span>
                </div>
            </div>
        </div>
    );
}