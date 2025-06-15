package com.example.backend.model;

import com.example.backend.enums.TransactionDirection;
import com.example.backend.enums.TransactionStatus;

import java.util.Date;

public record TransactionDto(
        Long id,
        Long fromAccountId,
        Long toAccountId,
        double amount,
        Double fee,
        String currency,
        String description,
        String reference,
        Date date,
        TransactionStatus status,
        TransactionDirection direction
) {
}
