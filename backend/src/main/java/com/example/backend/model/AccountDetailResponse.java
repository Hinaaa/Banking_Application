package com.example.backend.model;

public record AccountDetailResponse(
    String message,
    String messageType,
    Long userId,
    Long accountId,
    Boolean hasAccount, //to response with true if account exists else false
    AccountDetail accountDetail
) {
    }
