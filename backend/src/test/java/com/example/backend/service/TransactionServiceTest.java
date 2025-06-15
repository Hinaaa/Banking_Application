package com.example.backend.service;

import com.example.backend.enums.TransactionDirection;
import com.example.backend.enums.TransactionStatus;
import com.example.backend.model.*;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.TransactionRepo;
import com.example.backend.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    @Mock
    private TransactionRepo mockTransactionRepo;
    @Mock
    private AccountRepo mockAccountRepo;
    @Mock
    private UserRepo mockUserRepo;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Account account;
    private TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        account = new Account();
        account.setId(1L);
        account.setAccountBalance(1000.0);
        account.setUser(user);

        transactionDto = new TransactionDto(
                user.getId(),
                account.getId(),
                null,
                1000.00,
                200.00,
                "Test deposit",
                "bankTransfer",
                "Account Holder: Tester, IBAN: DE977898992789, BIC: BIC6872",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.CREDIT
        );
        transactionDto = new TransactionDto(
                user.getId(),
                account.getId(),
                null,
                1000.00,
                200.00,
                "Test deposit",
                "bankTransfer",
                "Account Holder: Tester, IBAN: DE977898992789, BIC: BIC6872",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.DEBIT
        );
    }
    //Transaction - add money
    @Test
    void addMoneyTransaction_shouldReturnSuccessResponse_whenAccountExists() {
        when(mockAccountRepo.findById(account.getId())).thenReturn(Optional.of(account)); //mocked, return account when searched by Id
        when(mockUserRepo.getById(user.getId())).thenReturn(user);//mocked, return user when searched by Id
        TransactionResponse response = transactionService.addTransaction(transactionDto); //call service
        assertNotNull(response); //response not null
        assertEquals("Transaction Successful",response.message());
        assertEquals(TransactionStatus.SUCCESS.name(),response.transactionStatus());
        assertEquals(1200.00,response.updatedBalance());
        assertEquals(TransactionDirection.CREDIT,response.transactionDirection());

        verify(mockAccountRepo).save(account); //verify mock
     //   verify(mockTransactionRepo).save(any(Transaction.class));
    }
    @Test
    void addMoneyTransaction_shouldReturnFailedResponse_whenAccountNotFound() {
        when(mockAccountRepo.findById(account.getId())).thenReturn(Optional.empty()); //return account empty
        TransactionResponse response = transactionService.addTransaction(transactionDto); //call service
        assertNotNull(response);
        assertEquals("Account not found",response.message());
        assertEquals(TransactionStatus.FAILED.name(),response.transactionStatus());
        verify(mockAccountRepo, never()).save(any());
        verify(mockTransactionRepo, never()).save(any()); //verify nothings save
    }
    //transfer money
    @Test
    void transferTransaction_shouldReturnSuccessResponse_whenAccountExists_andSufficientBalance() {
        when(mockAccountRepo.findById(account.getId())).thenReturn(Optional.of(account)); //return account
        when(mockUserRepo.getById(user.getId())).thenReturn(user); //return user
        TransactionResponse response = transactionService.transferTransaction(transactionDto); //call service
        assertNotNull(response); //response not null
        assertEquals("Transaction Successful",response.message()); //transaction successful
        assertEquals(TransactionStatus.SUCCESS.name(),response.transactionStatus()); //status success
        assertEquals(800.00,response.updatedBalance());
        assertEquals(TransactionDirection.DEBIT,response.transactionDirection());
        verify(mockAccountRepo).save(account);
        verify(mockTransactionRepo).save(any(Transaction.class));
    }
    @Test
    void transferTransaction_shouldReturnFailedResponse_whenAccountNotFound_andSufficientBalance() {
        when(mockAccountRepo.findById(account.getId())).thenReturn(Optional.empty());//account empty
        TransactionResponse response = transactionService.transferTransaction(transactionDto); //call service
        assertNotNull(response); //response not null
        assertEquals("Account not found",response.message());
        assertEquals(TransactionStatus.FAILED.name(),response.transactionStatus());
        verify(mockAccountRepo, never()).save(any());
        verify(mockTransactionRepo, never()).save(any(Transaction.class));
    }
    @Test
    void transferTransaction_shouldReturnFailedResponse_whenInsufficientBalance() {
        account.setAccountBalance(100.00);
        transactionDto = new TransactionDto(
                user.getId(), account.getId(), null,  100.00, 200.00, "Test deposit with insufficient balance",
                "bankTransfer", "Account Holder: Tester, IBAN: DE977898992789, BIC: BIC6872",
                new Date(), TransactionStatus.FAILED,
                TransactionDirection.DEBIT
        );
        when(mockAccountRepo.findById(account.getId())).thenReturn(Optional.of(account));
        when(mockUserRepo.getById(user.getId())).thenReturn(user);

        TransactionResponse response = transactionService.transferTransaction(transactionDto);
        assertNotNull(response);
        assertEquals("Transaction Failed: Account Balance low to transfer requested amount",response.message());
        assertEquals(TransactionStatus.FAILED.name(),response.transactionStatus());
        assertEquals(TransactionDirection.DEBIT,response.transactionDirection());
    }
}