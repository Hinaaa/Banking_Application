import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {fetchDashboard} from "../service/apiService.tsx";
import type {AccountInfo, DashboardResponse} from "../types/DashboardType.ts";
import "../css/Dashboard.css";

export default function Dashboard() {
    const [loading, setLoading] = useState<boolean>(false);
    const [dashboardData, setDashboardData] = useState<DashboardResponse | null>(null)
    const [error, setError] = useState<string | null>(null)
    const navigate = useNavigate()

    const stored = localStorage.getItem("currentUserId");
    const userId = stored ? Number(stored) : null;

    const handleLogout = () => {
        localStorage.removeItem("currentUserId");
        navigate("/");
    };

    useEffect(() => {
        if (!userId) {
            alert("User Id missing");
            return;
        }
        setLoading(true);
        fetchDashboard(userId)
            .then((data) => {
                setDashboardData(data);
            })
            .catch((err: unknown) => {
                if (err instanceof Error) {
                    setError(err.message);
                } else {
                    setError("Could not connect to database");
                }
            })
            .finally(() => {
                setLoading(false);
            });
    }, [userId]);

    const handleViewAccountDetails = () => {
        if(!stored) {
            alert("User Id exists")
            return
        }
        const numericId = Number(stored);
        navigate(`/viewaccountdetails?user_id=${numericId}`)
    }

    const handleAddMoneyPayment = () => {
        navigate("/addmoney")
    }

    const handleTransferMoneyPayment = () => {
        navigate("/transfermoney")
    }

    if(loading) {
        return <div>loading</div>
    }
    if(error) {
        return <div style={{ color: "red" }}>{error}</div>
    }
    if(!dashboardData) {
        return null
    }

    const { accountDetailDashboardInfo, transactionDashboard } = dashboardData;
    const accountInfo = accountDetailDashboardInfo as AccountInfo

    return (
        <>
            <div>
                <div className="dashboard-header">
                    <h1>Welcome to your Dashboard</h1>
                    <h2><button onClick={handleLogout} className="logout-button">Logout</button></h2>
                </div>

                <div className="dashboard-top">
                    <div className="account-detail">
                        <p><strong>Name: {accountInfo.accountHolderName}</strong></p>
                        <p><strong>IBAN: {accountInfo.iban}</strong></p>
                        <p><strong>BIC: {accountInfo.bic}</strong></p>
                        <p><strong>Total Account Balance: {accountInfo.accountBalance}</strong></p>
                    </div>

                    <div className="handle-buttons">
                        <button onClick={handleViewAccountDetails}>View Account Details</button>
                        <button onClick={handleAddMoneyPayment}>Add Money</button>
                        <button onClick={handleTransferMoneyPayment}>Transfer Money</button>
                    </div>
                </div>
            </div>

            <h3>Transactions</h3>
            {Array.isArray(transactionDashboard) && transactionDashboard.length === 0 ? (
                <p>No transactions to show.</p>
            ) : (
                Array.isArray(transactionDashboard) ? (
                    <div className="table-wrapper">
                        <table className="transaction-table" aria-label="Transaction details">
                            <thead>
                            <tr>
                                <th>Transaction Id</th>
                                <th>Transaction</th>
                                <th>Amount</th>
                                <th>Type</th>
                                <th>Description</th>
                                <th>Date</th>
                                <th>Status</th>
                                <th>To Account</th>
                            </tr>
                            </thead>
                            <tbody>
                            {transactionDashboard
                                .slice()
                                .sort((a, b) => new Date(b.transactionDate).getTime() - new Date(a.transactionDate).getTime())
                                .map((transactionDetail) => {
                                    const dir = (transactionDetail.transactionDirection || "").toLowerCase().trim();
                                    const rowClass = dir === "credit" ? "credit" : dir === "debit" ? "debit" : "";
                                    const amount = Math.abs(Number(transactionDetail.amount)).toFixed(2);
                                    return (
                                        <tr
                                            key={transactionDetail.id}
                                            className={`${rowClass} cursor-pointer`}
                                            onClick={() =>
                                                navigate(`/viewtransaction/${transactionDetail.id}`, {
                                                    state: { transaction: transactionDetail },
                                                })
                                            }
                                        >
                                            <td data-label="Transaction Id">{transactionDetail.id}</td>
                                            <td data-label="Transaction">{transactionDetail.transactionDirection}</td>
                                            <td data-label="Amount">{amount}</td>
                                            <td data-label="Type">{transactionDetail.transactionType}</td>
                                            <td data-label="Description">{transactionDetail.description}</td>
                                            <td data-label="Date">{new Date(transactionDetail.transactionDate).toLocaleString()}</td>
                                            <td data-label="Status">{transactionDetail.status}</td>
                                            <td data-label="To Account">{transactionDetail.transactionFromToAccountDetails}</td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                ) : (
                    <p>Transactions data is not available.</p>
                )
            )}
        </>
    );
}
