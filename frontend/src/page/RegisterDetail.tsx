import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function RegisterDetail() {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [phone, setPhone] = useState("");
    const [street, setStreet] = useState("");
    const [houseNumber, setHouseNumber] = useState("");
    const [postalCode, setPostalCode] = useState("");
    const [city, setCity] = useState("");
    const [country, setCountry] = useState("");
    const [error, setError] = useState("");

    const navigate = useNavigate();

    const handleRegister = () => {
        if (!firstName || !lastName || !phone || !street || !houseNumber || !postalCode || !city || !country) {
            setError("Please fill in all mandatory fields");
            return;
        }

        // Optionally store data somewhere or send to backend here

        setError("");
        navigate("/"); // redirect after successful "register"
    };

    return (
        <div className="checkout-form">
            <h2>Please enter registration details</h2>

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

            {error && <div style={{ color: "red" }}>{error}</div>}

            <button onClick={handleRegister}>Register & Continue</button>
        </div>
    );
}