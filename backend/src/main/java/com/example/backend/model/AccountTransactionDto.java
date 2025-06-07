package com.example.backend.model;

public record AccountTransactionDto(
        Long userId, // this fk already in db
        Long payment_Id,
        Double totalAccountBalance
) {
}
