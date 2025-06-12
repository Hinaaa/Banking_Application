export type TransactionData = { // TransactionDto matches backend record fields
    userId:number
    accountId:number
    id: number
    accountBalance:number //this should be sent from account table later - to view current account
    amount:number
    description: string //example amount added in account or amount transferred from account
    transactionType:string //added or transferred
    transactionFromToAccountDetails:string //transferred from acc/card: acc card detail or transferred to
    transactionDate:Date
    status: "SUCCESS" | "FAILED" | "PENDING" | "Error"
}
export type AccountInfo = { //AccountDetailDashboardInfo from backend record
    accountId:number,
    accountBalance:number,
    accountHolderName:string,
    iban:string,
    bic:string
}
export type DashboardResponse = { //DashboardResponse matches backend DashboardResponse record:
    user_id:number
    accountDetailDashboardInfo: AccountInfo
    transactionDashboard: TransactionData
}