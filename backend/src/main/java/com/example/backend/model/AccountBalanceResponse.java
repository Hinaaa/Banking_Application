package com.example.backend.model;

public record AccountBalanceResponse(
        Long userId,
        Long accountId,
        double accountBalance
) {
}
