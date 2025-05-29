package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepo accountRepo; //inject account repo
    private final UserRepo userRepo; //inject user repo

    public AccountService(AccountRepo accountRepo, UserRepo userRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    public Response registerAccount(AccountDetail accountDetail) {
        User user = userRepo.findById(accountDetail.userId())
                           .orElseThrow(() -> new IllegalArgumentException("User not found"));

    //create link account
        Account account = new Account();
        account.setUser(user);
        account.setAccountHolderName(accountDetail.accountHolderName());
        account.setIban(accountDetail.iban());
        account.setBic(accountDetail.bic());
        account.setCardNumber(accountDetail.cardNumber());
        account.setCardHolder(accountDetail.cardHolder());
        account.setExpiryDate(accountDetail.expiryDate());
        account.setCvv(accountDetail.cvv());
        account.setPin(accountDetail.pin());
        accountRepo.save(account);
        return new Response("account registered", "Success", user.getId()); //passes the user’s ID into your Response record
    }
}
