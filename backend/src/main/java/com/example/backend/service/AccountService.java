package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
                .orElse(null);
        //user not exists means not registered
        if (user == null) {
            return new Response("User not found", "Error", null, false, false); //
        }
        //if user already have account
        if (accountRepo.existsByUser(user)) { //existsByUser JPT function is any Account record where the foreign key to the User matches
            return new Response("User already exists", "Info", user.getId(),true, true);
        }

        //create link account, when user exists and account not exist so register account
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
        return new Response("account registered", "Success", user.getId(), true, true
        ); //passes the user’s ID into your Response record
    }

    //View account details
    public AccountDetailResponse viewAccountDetails(Long userId) {
        //if user exists - save side check this
        Optional<Account> optionalAccount = accountRepo.findByUserId(userId);

        // If account present
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get(); // Directly retrieve account. Spring Data translated to SELECT * FROM accounts WHERE user_id = :userId
            AccountDetail accountDetail = new AccountDetail( //Map Account entity to AccountDetail Dto
                    account.getUser().getId(),
                    account.getAccountHolderName(),
                    account.getIban(),
                    account.getBic(),
                    account.getCardNumber(),
                    account.getCardHolder(),
                    account.getExpiryDate(),
                    account.getCvv(),
                    account.getPin()
            );
            return new AccountDetailResponse("account details fetched successfully", "success", userId, account.getId(), true, accountDetail);
        } else
            return new AccountDetailResponse(
                    "No account found for user",
                    "error",
                    userId,
                    null,
                    false, // hasAccount = false
                    null); // no AccountDetail to return
    }
    public AccountBalanceResponse viewAccountBalance(Long userId) {
        Optional<Account> optionalAccount = accountRepo.findByUserId(userId);
        if(optionalAccount.isPresent()) {
            Account userAccount = optionalAccount.get();
            return new AccountBalanceResponse(
                    userId,
                    userAccount.getId(),
                    userAccount.getAccountBalance()
            );
        } else {
           return new AccountBalanceResponse(userId,null,0);
        }
    }
}

