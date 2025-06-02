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
    return (
        <>
            <h1>Im dashboard</h1>
            <button onClick={handleViewAccountDetails}>View Account Details</button>
        </>
    )
}