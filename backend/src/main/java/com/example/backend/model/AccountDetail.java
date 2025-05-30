package com.example.backend.model;

public record AccountDetail(
            Long userId,
        String accountHolderName,
        String iban,
        String bic,
        String cardNumber,
        String cardHolder,
        String expiryDate,
        String cvv,
        String pin
) {
}
