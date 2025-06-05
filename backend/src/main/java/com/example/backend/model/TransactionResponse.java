package com.example.backend.model;

import java.util.Date;

public record TransactionResponse(
        String message,
        String transactionStatus,
        String transactionType,
        Double updatedBalance,
        Date transactionDate,
        TransactionDto transactionData
) {
}
