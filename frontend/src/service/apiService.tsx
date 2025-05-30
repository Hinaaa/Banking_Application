import axios, {} from "axios";

export async function registerUser(registrationDetails: {
    firstName: string;
    lastName: string;
    phoneNumber: string;
    email: string;
    password: string;
    passwordConfirmation: string;
    yearOfBirth: number;
    streetAddress: string;
    city: string;
    postalCode: string;
    country: string;
}) {
    try {
        const response = await axios.post("/api/user/register", registrationDetails); ////axios.post() sending HTTP POST request. Vite’s proxy forwarding "/api" to backend in JSON
        return response.data as string; ////axios return on success 200-209. response.data` contains whatever your backend
    } catch (err) {
        if (axios.isAxiosError(err)) { //err is any type, first checking if it’s an AxiosError
            if (err.response?.data && typeof err.response.data === "object" && "message" in err.response.data) {
                const backendMessage = (err.response.data as { message: string }).message;
                throw new Error(backendMessage); //throw backend message. if backend return error message, err.response is the HTTP response object from the server
            }
            // else if backend returned a plain string
            if (typeof err.response?.data === "string") {
                throw new Error(err.response.data);
            }
            throw new Error("Error: Could not connect to backend");
        }
        throw new Error("Unknown error occurred");
    }
}
//login axios
export async function loginUser(email: string, password: string) {
    try {
        const response = await axios.post("/api/user/login", { email, password });
        return response.data;
    } catch (err) {
        if (axios.isAxiosError(err)) {
           //GlobalExceptionHandler sends { errorMessage, statusCode }
            const backendError = (err.response?.data)?.errorMessage; //read err.response.data.errorMessage
            throw new Error(backendError || "Error: Could not connect to backend");
        }
        throw new Error("Unknown error occurred");
    }
}
//apiService
export async function registerAccountDetails(accountData: {
    userId: number;
    accountHolderName: string;
    iban: string;
    bic: string;
    cardNumber: string;
    cardHolder: string;
    expiryDate: string;
    cvv: string;
    pin: string;
}) {
    try {
        const response = await axios.post(
            "/api/account/accountregister",    // ← exact match
            accountData
        );
        return response.data as { message: string };
    } catch (err) {
        if (axios.isAxiosError(err)) {
            // … your existing error handling …
        }
        throw new Error("Unknown error occurred.");
    }
}