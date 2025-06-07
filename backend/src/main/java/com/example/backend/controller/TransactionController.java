package com.example.backend.controller;

import com.example.backend.model.TransactionDto;
import com.example.backend.repo.TransactionRepo;
import com.example.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> addTransaction(@RequestBody TransactionDto transactionDto) {
        transactionService.addTransaction(transactionDto);
        return ResponseEntity.ok("Money added successfully");
    }

    //Transfer Amount
    @PostMapping("/transfer")
    public ResponseEntity<String> transferTransaction(@RequestBody TransactionDto transactionDto) {
        //transactionService.transferTransaction(transactionDto);
        return ResponseEntity.ok("Money transferred successfully");
    }
}
