package com.example.backend.model;
//for structure response
public record Response(
      String message,
      String messageType,
      Long userId
) {
}
