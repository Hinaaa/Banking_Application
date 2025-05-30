package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String accountHolderName;
    private String iban;
    private String bic;

    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String cvv;
    private String pin;

public Account() {} //constructor
public Account(String accountHolderName, String iban, String bic, String cardNumber,
               String cardHolder, String expiryDate, String cvv, String pin) {
    this.accountHolderName = accountHolderName;
    this.iban = iban;
    this.bic = bic;
    this.cardNumber = cardNumber;
    this.cardHolder = cardHolder;
    this.expiryDate = expiryDate;
    this.cvv = cvv;
    this.pin = pin;
}
}