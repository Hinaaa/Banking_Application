// What frontend SENDS TO backend: user inputs + IDs only
export type Transactionrequest = {
    userId: number;
    accountId: number //for which used logged in
    amount: number //Enter from frontend by user
    description: string//enter by user at front end
    transactionType:  "bankTransfer" | "cardTransfer" //frontend radio button Selection
    transactionFromToAccountDetails: string //entered by user from frontend
}
// What backend returns, In this case some fields along with some of its separately set fields from database Exactly matches backend TransactionDto record:
export type TransactionDto = { //TransactionDto passed in response
    userId: number;
    accountId: number //for which used logged in
    accountBalance: number //coming from database
    amount: number //Enter from frontend by user
    description: string//enter by user at front end
    transactionType: string //frontend radio button Selection
    transactionFromToAccountDetails: string //entered by user from frontend
    transactionDate: Date //this is generated at backend already the time of entry
    status: "SUCCESS" | "FAILED" | "PENDING" | "Error";//this defined in enum at backend
}

//Exactly matches backend TransactionResponse
export type TransactionResponse = { //is it defined for backend>
    message: string;
    transactionStatus: "SUCCESS" | "FAILED" | "ERROR" // status needs explicit typing because it's a controlled set. So It need to defined at frontend end as well but not message because it's free text.
    transactionType: "success" | "fail" | "error" | "info"; //Also a controlled set of values uses by frontend to display or logic
    updatedBalance: number; //Defined separately as well other than below dto to define key summary fields like total amount, date, status etc separately(A good Practise)
    transactionDate: Date;
    transactionData: Transactionrequest //TransactionDto should be same at backend
};

//user id sent from frontend and account if fetched from backend as per sent user