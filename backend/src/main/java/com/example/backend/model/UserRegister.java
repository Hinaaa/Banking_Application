package com.example.backend.model;

public record UserRegister(
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String password,
        int yearOfBirth,
        String streetAddress,
        String city,
        String postalCode,
        String country
) {

}
