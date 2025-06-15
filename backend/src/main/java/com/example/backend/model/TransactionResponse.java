package com.example.backend.model;

import com.example.backend.enums.TransactionDirection;

import java.util.Date;

public record TransactionResponse(
        String message,
        String transactionStatus,
        String transactionType,
        Double updatedBalance,
        Date transactionDate,
        TransactionDto transactionData, //later for payment view use
        Long transactionId,
        TransactionDirection transactionDirection
) {
}
