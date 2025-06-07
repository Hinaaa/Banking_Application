package com.example.backend.service;

import com.example.backend.enums.TransactionStatus;
import com.example.backend.model.*;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.TransactionRepo;
import com.example.backend.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final UserRepo userRepo;
    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;

    // Constructor injection
    public TransactionService(TransactionRepo transactionRepo, AccountRepo accountRepo, UserRepo userRepo) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    public TransactionResponse addTransaction(TransactionDto transactionDto) {
        //fetch account and user entity
        Account account = accountRepo.findById(transactionDto.accountId())//In transactionDto account id has been searched and again that account Id complete Account entity tfrom Account table has been fetched?
                //This handles throw Response now: .orElseThrow(() -> new RuntimeException("Account not Found"));
                .orElse(null);
        if (account == null) {
            return new TransactionResponse("Account not found",
                    TransactionStatus.FAILED.name(), null,null, null, null, null);
        } //enum as string

        //fetch user from repo
        User user = userRepo.getById(transactionDto.userId());
        Transaction transaction = new Transaction();
        transaction.setUser(user); //User already set but still have to set it here with account in account table
        transaction.setAccount(account); //Assigns full Account entity to the transaction to link it to the account in the DB via FK
        transaction.setAmount(transactionDto.amount()); //Transaction column set here because it is a transaction service
        transaction.setDescription(transactionDto.description());
        transaction.setTransactionType(transactionDto.transactionType());
        transaction.setTransactionDate(transactionDto.transactionDate());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionFromToAccountDetails(transactionDto.transactionFromToAccountDetails());

        //Update Account Balance
        account.setAccountBalance(account.getAccountBalance() + transactionDto.amount());
        accountRepo.save(account);//balance updated in table Account
        transactionRepo.save(transaction); //saving transaction
        return new TransactionResponse("Transaction Successful", TransactionStatus.SUCCESS.name(),
                transactionDto.transactionType(),
                account.getAccountBalance(),
                transactionDto.transactionDate(),
                transactionDto,
                transaction.getId());
    }
}
