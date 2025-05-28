
import './App.css'
import {Route, Routes} from "react-router-dom";
import Login from "./page/Login.tsx";
import Register from "./page/Register.tsx";
import RegisterDetail from "./page/RegisterDetail.tsx";
import Home from "./page/Home.tsx";

function App() {

  return (
    <>
      <h1>Hello</h1>
        <Routes>
         <Route path ={"/"} element={<Home/>}/>
         <Route path={"/login"} element={<Login/>}/>
         <Route path={"/register"} element={<Register/>}/>
         <Route path={"/registerdetail"} element={<RegisterDetail/>}/>
        </Routes>
    </>
  )
}

export default App
