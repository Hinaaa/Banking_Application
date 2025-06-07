export type AccountDetail = {
    userId: number;
    accountHolderName: string;
    iban: string;
    bic: string;
    cardNumber: string;
    cardHolder: string;
    expiryDate: string;
    cvv: string;
    pin: string;
};

export type AccountDetailResponse = {
    message: string;
    messageType: "success" | "fail" | "error" | "info";
    userId: number;
    accountId:number
    hasAccount: boolean;
    accountDetail: AccountDetail | null;
};