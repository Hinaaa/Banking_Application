import {useNavigate} from "react-router-dom";

export default function Dashboard() {
    const navigate = useNavigate()
    const handleViewAccountDetails = () => {
        const stored = localStorage.getItem("currentUserId")
        if(!stored) {
            alert("User Id exists")
            return
        }
        const numericId = Number(stored);
        navigate(`/viewaccountdetails?user_id=${numericId}`) //Include the user_id query param
    }
    //a payment block here
    //payment add money logic
    const handleAddMoneyPayment = () => {
        navigate("/addmoney")
    }
    //send Moeny
    const handleSendMoneyPayment = () => {
        navigate("/sendmoney")
    }
    return (
        <>
            <h1>Welcome to your Dashboard</h1>
            <button onClick={handleViewAccountDetails}>View Account Details</button>
            <button onClick={handleAddMoneyPayment}>Add Money</button>
            <button onClick={handleSendMoneyPayment}>Send Money</button>
        </>
    )
}