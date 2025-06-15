package com.example.backend.model;

import com.example.backend.enums.TransactionDirection;
import com.example.backend.enums.TransactionStatus;

import java.util.Date;

public record TransactionDto(
        Long userId,
        Long accountId,
        Long id,
        double accountBalance, //this should be sent from account table later - to view current account
        Double amount,
        String description, //example amount added in account or amount transferred from account
        String transactionType, //added or transferred
        String transactionFromToAccountDetails, //transferred from acc/card: acc card detail or transferred to
        Date transactionDate,
        TransactionStatus status,
        TransactionDirection transactionDirection
) {
}
