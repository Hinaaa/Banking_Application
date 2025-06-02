package com.example.backend.model;
//for structure response
public record Response(
      String message,
      String messageType,
      Long userId,
      Boolean isRegistered,//for validating if user not registered login is not allowed and one should login first
      Boolean hasAccount //to response with true if account exists else false
) {
}
