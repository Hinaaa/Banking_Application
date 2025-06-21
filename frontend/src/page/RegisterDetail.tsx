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
    const [focusedField, setFocusedField] = useState<string | null>(null);

    const handleBack = () => {
        navigate(-1); // this goes back one page in history
    };
    const handleRegister = async () => {
        if (!firstName || !lastName || !phone || !street || !houseNumber || !postalCode || !city || !country || !yearOfBirth) {
            setError("Please fill in all mandatory fields");
            return;
        }
        //field Validations
        // First/Last Name: alphabetic + spaces, 2–50 chars
        const nameRegex = /^[A-Za-z\s]{2,50}$/;
        if (!nameRegex.test(firstName)) {
            setError("First Name must be 2–50 letters");
            return;
        }
        if (!nameRegex.test(lastName)) {
            setError("Last Name must be 2–50 letters");
            return;
        }

        // Phone: digits only, 7–15 digits
        if (!/^\+?\d{7,13}$/.test(phone)) {
            setError("Phone Number must be 7–13 digits and may start with +");
            return;
        }

        // Street: allow letters, numbers, spaces, 2–100 chars
        if (street.length < 2 || street.length > 100) {
            setError("Street must be 2–100 characters");
            return;
        }

        // House Number: alphanumeric, 1–10 chars
        if (!/^[A-Za-z0-9/-]{1,10}$/.test(houseNumber)) {
            setError("House Number must be 1–10 alphanumeric characters");
            return;
        }
        // Postal Code: digits or alphanumeric depending on locale; here allow 3–10 alphanumeric
        if (!/^[A-Za-z0-9]{3,10}$/.test(postalCode)) {
            setError("Postal Code must be 3–10 alphanumeric characters");
            return;
        }

        // City: letters and spaces, 2–50 chars
        if (!/^[A-Za-z\s]{2,50}$/.test(city)) {
            setError("City must be 2–50 letters");
            return;
        }

        // Country: letters and spaces, 2–50 chars
        if (!/^[A-Za-z\s]{2,50}$/.test(country)) {
            setError("Country must be 2–50 letters");
            return;
        }

        // Year of Birth
        const currentYear = new Date().getFullYear();
        const yob = parseInt(yearOfBirth.trim(), 10);

        if (!/^\d{4}$/.test(yearOfBirth) || yob < 1900 || yob >= currentYear) {
            setError(`Year of Birth must be a 4-digit year between 1900 and ${currentYear - 1}`);
            return;
        }
        //Connecting to axios for backend data
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
            <div><div><button onClick={handleBack} className="back-button">← Back</button></div>
            <div className="auth-form">
                <h2>Registration Details</h2>

                <div className="detail-block">
                    <label htmlFor="firstName">First Name *</label>
                    <input
                        id="firstName"
                        type="text"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                    />

                    <label htmlFor="lastName">Last Name *</label>
                    <input
                        id="lastName"
                        type="text"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                    />

                    <label htmlFor="phone">Phone Number *</label>
                    <input
                        id="phone"
                        type="text"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                    />

                    <label htmlFor="street">Street *</label>
                    <input
                        id="street"
                        type="text"
                        value={street}
                        onChange={(e) => setStreet(e.target.value)}
                    />

                    <label htmlFor="houseNumber">House Number *</label>
                    <input
                        id="houseNumber"
                        type="text"
                        value={houseNumber}
                        onChange={(e) => setHouseNumber(e.target.value)}
                    />

                    <label htmlFor="postalCode">Postal Code *</label>
                    <input
                        id="postalCode"
                        type="text"
                        value={postalCode}
                        onChange={(e) => setPostalCode(e.target.value)}
                    />

                    <label htmlFor="city">City *</label>
                    <input
                        id="city"
                        type="text"
                        value={city}
                        onChange={(e) => setCity(e.target.value)}
                    />

                    <label htmlFor="country">Country *</label>
                    <input
                        id="country"
                        type="text"
                        value={country}
                        onChange={(e) => setCountry(e.target.value)}
                    />

                    <label htmlFor="yearOfBirth">Year of Birth *</label>
                    <input
                        id="yearOfBirth"
                        type="text"
                        value={yearOfBirth}
                        onChange={(e) => setYearOfBirth(e.target.value)}
                        onFocus={() => setFocusedField("yearOfBirth")}
                        onBlur={() => setFocusedField(null)}
                    />
                    {focusedField === "yearOfBirth" && (
                        <div className="focus-message">
                            Enter a 4-digit year between 1900 and {new Date().getFullYear() - 1}
                        </div>
                    )}
            </div>
                {error && <div className="error-message">{error}</div>}
                <button onClick={handleRegister}>Register and Continue</button>
            </div>
        </div>
        </div>
    );
}