package com.example.backend.model;

public record AccountBalanceDto(
        Long userId,
        Long accountId,
        double accountBalance
) {
}
