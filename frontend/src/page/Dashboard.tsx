import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {fetchDashboard} from "../service/apiService.tsx";
import type {AccountInfo, DashboardResponse} from "../types/DashboardType.ts";
import "../CSS/Dashboard.css";

export default function Dashboard() {
    const [loading, setLoading] = useState<boolean>(false);//for user
    const [dashboardData, setDashboardData] = useState<DashboardResponse | null>(null)
    const [error, setError] = useState<string | null>(null)
    const navigate = useNavigate()
    //user
    const stored = localStorage.getItem("currentUserId");
    const userId = stored ? Number(stored) : null;
//    fetchDashboard data
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

    //handled buttons actions and navigation
    const handleViewAccountDetails = () => {
        if(!stored) {
            alert("User Id exists")
            return
        }
        const numericId = Number(stored);
        navigate(`/viewaccountdetails?user_id=${numericId}`) //Include the user_id query param
    }
    const handleAddMoneyPayment = () => {
        navigate("/addmoney")
    }
    //send Money
    const handleTransferMoneyPayment = () => {
        navigate("/transfermoney")
    }
    //handle loading
    if(loading) {
        return <div>loading</div>
    }
    if(error) {
        return <div style={{ color: "red" }}>{error}</div>
    }
    if(!dashboardData) { //if no data returned
        return null
    }

    const { accountDetailDashboardInfo, transactionDashboard } = dashboardData; //setting from type
    const accountInfo = accountDetailDashboardInfo as AccountInfo

    return (
        <>
            {/*Account Detail*/}
                <h1>Welcome to your Dashboard</h1>

                {/* Top row: account details left, buttons right */}
                <div className="dashboard-top">
                    <div className="account-detail">
                        <p><strong>Name: {accountInfo.accountHolderName}</strong></p>
                        <p><strong>IBAN: {accountInfo.iban}</strong></p>
                        <p><strong>BIC: {accountInfo.bic}</strong></p>
                        <p><strong>Total Account Balance: {accountInfo.accountBalance}</strong></p>
                    </div>
                    {/*button actions and navigations from dashboard*/}
                    <div className="handle-buttons">
                        <button onClick={handleViewAccountDetails}>View Account Details</button>
                        <button onClick={handleAddMoneyPayment}>Add Money</button>
                        <button onClick={handleTransferMoneyPayment}>Transfer Money</button>
                    </div>
                </div>

            {/*Transactions*/}
            <h3>Transactions </h3>
            {Array.isArray(transactionDashboard) && transactionDashboard.length === 0 ? (
                <p>No transactions to show.</p>
            ) : (
                <table className="transaction-table">
                    <thead>
                    <tr>
                        <th>Transaction Id</th>
                        <th>Transaction</th>
                        <th>Transaction amount</th>
                        <th>Transaction Type</th>
                        <th>Transaction Description</th>
                        <th>Transaction Date</th>
                        <th>Transaction Status</th>
                        <th>Transaction to Account</th>
                    </tr>
                    </thead>
                    <tbody>
                    {Array.isArray(transactionDashboard) &&
                        // Sort by transactionDate descending (newest first)
                        transactionDashboard
                            .slice() // create a shallow copy to avoid mutating state
                            .sort((a, b) => new Date(b.transactionDate).getTime() - new Date(a.transactionDate).getTime())
                            .map((transactionDetail) => {
                                const rawDir = transactionDetail.transactionDirection || "";
                                const dir = rawDir.toString().trim().toLowerCase();
                                let rowClass = "";
                                if (dir === "credit") {
                                    rowClass = "credit";
                                } else if (dir === "debit") {
                                    rowClass = "debit";
                                }
                                const amount = Math.abs(Number(transactionDetail.amount)).toFixed(2);

                                return (
                                    <tr key={transactionDetail.id} className={rowClass || undefined}>
                                        <td>{transactionDetail.id}</td>
                                        <td>{transactionDetail.transactionDirection}</td>
                                        <td>{amount}</td>
                                        <td>{transactionDetail.transactionType}</td>
                                        <td>{transactionDetail.description}</td>
                                        <td>{new Date(transactionDetail.transactionDate).toLocaleString()}</td>
                                        <td>{transactionDetail.status}</td>
                                        <td>{transactionDetail.transactionFromToAccountDetails}</td>
                                    </tr>
                                );
                            })}
                    </tbody>
                </table>
            )}
        </>
    );
}