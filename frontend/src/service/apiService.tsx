import axios from "axios";
import type {AccountDetailResponse} from "../types/AccountType.ts";
import type {AccountBalanceResponse, Transactionrequest} from "../types/TransactionType.ts";
import type {DashboardResponse} from "../types/DashboardType.ts";

//register a new user, sends input detail to backend
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
        const response = await axios.post("/api/user/register", registrationDetails); // axios.post() sending HTTP POST request. Vite’s proxy forwarding "/api" to backend in JSON
        return response.data as string; // backend returns a string on success 200-209. response.data` contains whatever your backend
    } catch (err) {
        if (axios.isAxiosError(err)) { //err is any type, first checking if it’s an AxiosError
            if (err.response?.data && typeof err.response.data === "object" && "message" in err.response.data) {
                const backendMessage = (err.response.data as { message: string }).message;
                throw new Error(backendMessage); //throw backend message. if backend return error message, err.response is the HTTP response object from the server
            }
            // else if backend returned a plain string
            if (typeof err.response?.data === "string") {
                throw new Error(err.response.data); // Throw backend message
            }
            throw new Error("Error: Could not connect to backend"); //otherwise generic
        }
        throw new Error("Unknown error occurred"); //Throw unknown error if not axios error if not got response from backend
    }
}
//login axios
export async function loginUser(email: string, password: string) {
    try {
        const response = await axios.post("/api/user/login", { email, password });
        return response.data;
    } catch (err) {
        if (axios.isAxiosError(err)) {
            const backendError = (err.response?.data)?.errorMessage; //read err.response.data.errorMessage
            throw new Error(backendError || "Error: Could not connect to backend");
        }
        throw new Error("Unknown error occurred");
    }
}
//Register account detail
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
            "/api/account/registeraccount",
            accountData
        );
        return response.data as { message: string };
    } catch (err) {
        if (axios.isAxiosError(err)) {

            if (err.response?.data && typeof err.response.data === "object" && "message" in err.response.data) { // if backend returned error object with message
                const backendMessage = (err.response.data as { message: string }).message;
                throw new Error(backendMessage);
            }
            if (typeof err.response?.data === "string") {
                throw new Error(err.response.data); //message from backend
            }
            // Generic backend connection error if no message
            throw new Error("Error: Could not connect to backend"); //when not from backend
        }
        throw new Error("Unknown error occurred");// Unknown error when not from axios
    }
}

//view account details -- GET
export async function getAccountDetails(user_id: number) {
    try {
        const response = await axios.get("/api/account/viewaccountdetails", {
            params: {user_id}, //controller expect user_id so this name should be same as in controller
        })
        return response.data as AccountDetailResponse
    } catch(err) {
        if (axios.isAxiosError(err)) {
            if (
                err.response?.data &&
                typeof err.response.data === "object" &&
                "message" in err.response.data
            ) {
                const backendMessage = (err.response.data as { message: string }).message;
                throw new Error(backendMessage);
            }
            if (typeof err.response?.data === "string") {
                throw new Error(err.response.data);
            }
            throw new Error("Error: Could not connect to backend");
        }
        throw new Error("Unknown error occurred");
    }
}
//Dashboard
export async function fetchDashboard(userId: number): Promise<DashboardResponse> {
    try {
        const response = await axios.get("/api/account/dashboard", {
            params: { userId },
        });
        return response.data as DashboardResponse;
    } catch (err) {
        if (axios.isAxiosError(err)) {
            if (
                err.response?.data &&
                typeof err.response.data === "object" &&
                "message" in err.response.data
            ) {
                const backendMessage = (err.response.data as { message: string }).message;
                throw new Error(backendMessage);
            }
            if (typeof err.response?.data === "string") {
                throw new Error(err.response.data);
            }
            throw new Error("Error: Could not connect to backend");
        }
        throw new Error("Unknown error occurred");
    }
}
//Transaction
export async function Transaction(transactionData: Transactionrequest) { //TransactionDtoType defined in type and passed as parameter here as type is also export
        try {
            const response = await axios.post("/api/account/addMoney", transactionData);
            return response.data as {
                transactionId: number;
                message?: string;
                transactionStatus: string;
                transactionType: string;
                updatedBalance: number;
                transactionDate: string;
                transactionData: Transactionrequest;
            };
        // const response = await axios.post("/api/account/addMoney",//adding data related to an account: user>>account>>Transaction. Account is direct Fk. Transaction nested under account not user directly
        //     transactionData) //Second parameter to axios.post. it stores input values sent from frontend. defined in main function parameter above
        // return response.data as { id: number; message?: string }; //.data = Builtin property. actual response body from backend e.g data: status:200 etc
    } catch (err) {
        if(axios.isAxiosError(err)) {

            if (err.response?.data && typeof err.response.data === "object" && "message" in err.response.data) { // if backend returned error object with message
                const backendMessage = (err.response.data as { message: string }).message; //Backend returned an object with a message(like { message: "Invalid IBAN" }).
                throw new Error(backendMessage); //show backend message
            }
            if (typeof err.response?.data === "string") {
                throw new Error(err.response.data); //message from backend error message
            }
            // Generic backend connection error if no message
            throw new Error("Error: Could not connect to backend"); //when not from backend
        }
        throw new Error("Unknown error occurred");// Unknown error when not from axios
    }
}
//transfer money
export async function TransactionMoney(transactionData: Transactionrequest) { //TransactionDtoType defined in type and passed as parameter here as type is also export
    try {
        const response = await axios.post("/api/account/transfermoney", transactionData);
        return response.data as {
            transactionId: number;
            message?: string;
            transactionStatus: string;
            transactionType: string;
            updatedBalance: number;
            transactionDate: string;
            transactionData: Transactionrequest;
        };
    } catch (err) {
        if(axios.isAxiosError(err)) {

            if (err.response?.data && typeof err.response.data === "object" && "message" in err.response.data) { // if backend returned error object with message
                const backendMessage = (err.response.data as { message: string }).message; //Backend returned an object with a message(like { message: "Invalid IBAN" }).
                throw new Error(backendMessage); //show backend message
            }
            if (typeof err.response?.data === "string") {
                throw new Error(err.response.data); //message from backend error message
            }
            // Generic backend connection error if no message
            throw new Error("Error: Could not connect to backend"); //when not from backend
        }
        throw new Error("Unknown error occurred");// Unknown error when not from axios
    }
}

export async function fetchCurrentBalance(userId: number): Promise<AccountBalanceResponse> {
    const resp = await axios.get("/api/account/addMoney", { params: { userId } });
    return resp.data;
}