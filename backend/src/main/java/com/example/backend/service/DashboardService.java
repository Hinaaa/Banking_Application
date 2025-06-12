package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.TransactionRepo;
import com.example.backend.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final UserRepo userRepo;
    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;

    public DashboardService(UserRepo userRepo, TransactionRepo transactionRepo, AccountRepo accountRepo) {
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
    }
    public DashboardResponse viewTransactionDashboard(Long userId) {
        userRepo.findById(userId) //get user from repo
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Transaction> transactions = transactionRepo.findAllByUser_Id(userId); //method defined in repo, find all transaction for user

        //map transaction dto. same transaction dto used for dashboard transaction
        List<TransactionDto> transactionData = transactions.stream()
                .map(td -> new TransactionDto(
                        td.getUser().getId(),
                        td.getAccount().getId(),
                        td.getId(),
                        td.getAccount().getAccountBalance(),
                        td.getAmount(),
                        td.getDescription(),
                        td.getTransactionType(),
                        td.getTransactionFromToAccountDetails(),
                        td.getTransactionDate(),
                        td.getStatus()
                ))
                .collect(Collectors.toList());

        //Account details
        Optional<Account> optionalAccount = accountRepo.findByUserId(userId); //for account details for respective user
        Account account = optionalAccount.orElseThrow(() -> new RuntimeException("Account not found")); //This unwraps the container. if an Account is present, it returns it; if not, it throws the exception. After this line, account is the actual Account object
        //Map account
        AccountDetailDashboardInfo accountInfo = new AccountDetailDashboardInfo(
                account.getId(),
                account.getAccountBalance(),
                account.getAccountHolderName(),
                account.getIban(),
                account.getBic() //these not methods from dtos, the value is just passing into the record constructor but accessing it from an Account entity, which uses Lombok @Getter getBic not bic() of record
        //Fields can be reduced while mapping i.e. more fields can exist in Dto
        );
        return new DashboardResponse(userId,accountInfo,transactionData);
    }
}
