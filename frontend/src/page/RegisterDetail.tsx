import { useState } from "react";
import {useLocation, useNavigate} from "react-router-dom";
import { registerUser } from "../service/apiService";
import axios from "axios";
import "../css/RegisterDetail.css";

export default function RegisterDetail() {

    const navigate = useNavigate();
    const location = useLocation();

    const registrationBasic =location.state as {
        email: string
        password: string
    }
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [phone, setPhone] = useState("");
    const [street, setStreet] = useState("");
    const [houseNumber, setHouseNumber] = useState("");
    const [postalCode, setPostalCode] = useState("");
    const [city, setCity] = useState("");
    const [country, setCountry] = useState("");
    const [yearOfBirth, setYearOfBirth] = useState("");
    const [error, setError] = useState("");

    const handleBack = () => {
        navigate(-1); // this goes back one page in history
    };
    const handleRegister = async () => {
        if (!firstName || !lastName || !phone || !street || !houseNumber || !postalCode || !city || !country || !yearOfBirth) {
            setError("Please fill in all mandatory fields");
            return;
        }
        //connecting to axios for backend data
       const payload = {
           email: registrationBasic.email, password: registrationBasic.password,
           passwordConfirmation: registrationBasic.password,
           firstName, lastName, phoneNumber: phone,
           streetAddress: `${street} ${houseNumber}`, //street +  houseNumber as backend expects
           postalCode, city, country, yearOfBirth: Number(yearOfBirth)
       }

        try {
            await registerUser(payload) //when successful login
            setError(""); //clear previous message
            navigate("/login") // redirect after successful registration

        }
        catch (err) {
            if(axios.isAxiosError(err)) {setError(err.message || "registration failed")}
        }
    };
    return (
        <div className="auth-wrapper">
            <div className="auth-form">
                <h2>Please enter registration details</h2>

                <div className="detail-block">
                    <input
                        type="text"
                        placeholder="First Name *"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Last Name *"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Phone Number *"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Street *"
                        value={street}
                        onChange={(e) => setStreet(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="House Number *"
                        value={houseNumber}
                        onChange={(e) => setHouseNumber(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Postal Code *"
                        value={postalCode}
                        onChange={(e) => setPostalCode(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="City *"
                        value={city}
                        onChange={(e) => setCity(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Country *"
                        value={country}
                        onChange={(e) => setCountry(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Year of Birth *"
                        value={yearOfBirth}
                        onChange={(e) => setYearOfBirth(e.target.value)}
                    />
                </div>
                {error && <div className="error-message">{error}</div>}
                <button onClick={handleRegister}>Register and Continue</button>
            </div>
            <div><button onClick={handleBack} className="back-button">← Back</button></div>
        </div>
    );
}