
import './App.css'
import {Route, Routes} from "react-router-dom";
import Login from "./page/Login.tsx";
import Register from "./page/Register.tsx";
import RegisterDetail from "./page/RegisterDetail.tsx";
import Home from "./page/Home.tsx";
import RegisterAccount from "./page/RegisterAccount.tsx";
import Dashboard from "./page/Dashboard.tsx";
import ViewAccountDetails from "./page/ViewAccountDetails.tsx";
import AddMoneyPayment from "./page/AddMoneyPayment.tsx";
import TransferMoneyPayment from "./page/TransferMoneyPayment.tsx";
import TransactionSuccessful from "./page/TransactionSuccessful.tsx";
import ViewTransaction from "./page/ViewTransaction.tsx";
import AboutUs from "./page/AboutUs.tsx";

function App() {

  return (
    <>
        <div className="app-container">
        <Routes>
         <Route path ={"/"} element={<Home/>}/>
         <Route path={"/login"} element={<Login/>}/>
         <Route path={"/register"} element={<Register/>}/>
          <Route path={"/registerdetail"} element={<RegisterDetail/>}/>
          <Route path="/registeraccount" element={<RegisterAccount />}/>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/viewaccountdetails" element={<ViewAccountDetails />} />
          <Route path="/addmoney" element={<AddMoneyPayment/>}/>
          <Route path="/transfermoney" element={<TransferMoneyPayment/>}/>
          <Route path="/transactionsuccessful" element={<TransactionSuccessful/>}/>
          <Route path="/viewtransaction/:id" element={<ViewTransaction />} />
          <Route path="/about" element={<AboutUs />} />
        </Routes>
        </div>
    </>
  )
}

export default App
