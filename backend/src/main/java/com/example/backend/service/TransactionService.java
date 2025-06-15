package com.example.backend.service;

import com.example.backend.enums.TransactionDirection;
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
    private final AccountService accountService;
    // Constructor injection
    public TransactionService(TransactionRepo transactionRepo, AccountRepo accountRepo, UserRepo userRepo, AccountService accountService) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
        this.accountService = accountService;
    }

    public TransactionResponse addTransaction(TransactionDto transactionDto) {
        //fetch account and user entity
        Account account = accountRepo.findById(transactionDto.accountId())//In transactionDto account id has been searched and again that account Id complete Account entity tfrom Account table has been fetched?
                //This handles throw Response now: .orElseThrow(() -> new RuntimeException("Account not Found"));
                .orElse(null);
        if (account == null) {
            return new TransactionResponse("Account not found",
                    TransactionStatus.FAILED.name(), null,null, null, null, null,null);
        } //enum as string
        User user = userRepo.getById(transactionDto.userId());//fetch user from repo
        Transaction transaction = new Transaction();
        transaction.setUser(user); //User already set but still have to set it here with account in account table
        transaction.setAccount(account); //Assigns full Account entity to the transaction to link it to the account in the DB via FK
        transaction.setAmount(transactionDto.amount()); //Transaction column set here because it is a transaction service
        transaction.setDescription(transactionDto.description());
        transaction.setTransactionType(transactionDto.transactionType());
        transaction.setTransactionDate(transactionDto.transactionDate());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionDirection(TransactionDirection.CREDIT);
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
                transaction.getId(),
                TransactionDirection.CREDIT);
    }
    //reusing balance from account
    public AccountBalanceResponse getUserBalance(Long userId) {
        return accountService.viewAccountBalance(userId);
    }
    //Transfer amount
    public TransactionResponse transferTransaction(TransactionDto transactionDto) {
        Account account = accountRepo.findById(transactionDto.accountId())//In transactionDto account id has been searched and again that account Id complete Account entity tfrom Account table has been fetched?
                //This handles throw Response now: .orElseThrow(() -> new RuntimeException("Account not Found"));
                .orElse(null);
        if (account == null) {
            return new TransactionResponse("Account not found",
                    TransactionStatus.FAILED.name(), null, null, null, null, null,null);
        } //enum as string
        User user = userRepo.getById(transactionDto.userId());//fetch user from repo
        Transaction transaction = new Transaction();
        transaction.setUser(user); //User already set but still have to set it here with account in account table
        transaction.setAccount(account); //Assigns full Account entity to the transaction to link it to the account in the DB via FK
        transaction.setAmount(transactionDto.amount()); //Transaction column set here because it is a transaction service
        transaction.setDescription(transactionDto.description());
        transaction.setTransactionType(transactionDto.transactionType());
        transaction.setTransactionDate(transactionDto.transactionDate());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionFromToAccountDetails(transactionDto.transactionFromToAccountDetails());
        transaction.setTransactionDirection(TransactionDirection.DEBIT);

        //Update Account Balance
        if (account.getAccountBalance() >= transactionDto.amount()) {
            account.setAccountBalance(account.getAccountBalance() - transactionDto.amount());
            accountRepo.save(account);//balance updated in table Account
            transactionRepo.save(transaction); //saving transaction
            return new TransactionResponse("Transaction Successful",
                    TransactionStatus.SUCCESS.name(),
                    transactionDto.transactionType(),
                    account.getAccountBalance(),
                    transactionDto.transactionDate(),
                    transactionDto,
                    transaction.getId(),
                    TransactionDirection.DEBIT);
        }
        else
            return new TransactionResponse("Transaction Failed: Account Balance low to transfer requested amount",
                    TransactionStatus.FAILED.name(),
                    transactionDto.transactionType(),
                    account.getAccountBalance(),
                    transactionDto.transactionDate(),
                    transactionDto,
                    transaction.getId(),
                    TransactionDirection.DEBIT);
    }
}
