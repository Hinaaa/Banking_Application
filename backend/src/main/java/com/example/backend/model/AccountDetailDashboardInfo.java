package com.example.backend.model;

public record AccountDetailDashboardInfo(
        Long accountId,
        double accountBalance,
        String accountHolderName,
        String iban,
        String bic
) {
}
