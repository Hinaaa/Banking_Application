export type AccountDetail = {
    user_id: number;
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
    user_id: number;
    hasAccount: boolean;
    accountDetail: AccountDetail | null;
};