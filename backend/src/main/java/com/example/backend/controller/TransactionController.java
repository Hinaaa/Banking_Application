package com.example.backend.controller;

import com.example.backend.model.AccountBalanceResponse;
import com.example.backend.model.TransactionDto;
import com.example.backend.model.TransactionResponse;
import com.example.backend.repo.TransactionRepo;
import com.example.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/account")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionRepo transactionRepo;

    public TransactionController(TransactionService transactionService, TransactionRepo transactionRepo) {
        this.transactionService = transactionService;
        this.transactionRepo = transactionRepo;
    }
    //Add Amount
    @PostMapping("/addMoney")
    public ResponseEntity<TransactionResponse> addTransaction(@RequestBody TransactionDto transactionDto) {//Receive the transaction data sent from frontend
        TransactionResponse response = transactionService.addTransaction(transactionDto); //Pass the TransactionDto object to the service layer method
        return ResponseEntity.ok(response); //return full response instead, frontend receives full transaction details
    }
    //transferMoney
    @PostMapping("/transfermoney")
    public ResponseEntity<TransactionResponse> transferTransaction(@RequestBody TransactionDto transactionDto) {
        TransactionResponse response = transactionService.transferTransaction(transactionDto);
        return ResponseEntity.ok(response);
    }
    //for balance view
    @GetMapping("/balance")
    public AccountBalanceResponse getBalance(@RequestParam long userId) {
        return transactionService.getUserBalance(userId);
    }
}
//current POST /addMoney endpoint only returns the balance after a transaction is done.
