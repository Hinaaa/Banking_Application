package com.example.backend.service;

import com.example.backend.enums.TransactionStatus;
import com.example.backend.model.*;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.TransactionRepo;
import com.example.backend.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardTest {
    @Mock
    private UserRepo mockUserRepo;
    @Mock
    private TransactionRepo mockTransactionRepo;
    @Mock
    private AccountRepo mockAccountRepo;
    @Mock
    private DashboardService dashboardService;

    private User user;
    private Account account;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        mockUserRepo = mock(UserRepo.class);
        mockTransactionRepo = mock(TransactionRepo.class);
        mockAccountRepo = mock(AccountRepo.class);
        dashboardService = new DashboardService(mockUserRepo, mockTransactionRepo, mockAccountRepo);

        user = new User(1L,"Jana","Jorden","jana.jorden@example.com","test123",
                "1234567890",1990,"123 abc Str","Berlin","13086",
                "Germany"
        );

        account = new Account("Jana Jorden", "DE89370400440532013000", "COBADEFFXXX", "1234567890123456",
                "Jana Jorden", "12/25", "123", "4567");
        account.setId(1L);
        account.setAccountBalance(1500.0);
        account.setUser(user);

        transaction = new Transaction();
        transaction.setId(100L);
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setAmount(500.0);
        transaction.setDescription("Test Transfer");
        transaction.setTransactionType("Credit");
        transaction.setTransactionFromToAccountDetails("From A to B");
        transaction.setTransactionDate(new Date()); // using java.util.Date
        transaction.setStatus(TransactionStatus.SUCCESS); // using enum type
    }

    @Test
    void viewTransactionDashboard_shouldReturnData_whenUserAndAccountExist() {

        when(mockUserRepo.findById(user.getId())).thenReturn(Optional.of(user)); //Mock transactionRepo, it's returning a list with one transaction for user
        when(mockTransactionRepo.findAllByUser_Id(user.getId())).thenReturn(List.of(transaction)); //Simulate transactionRepo
        when(mockAccountRepo.findByUserId(user.getId())).thenReturn(Optional.of(account)); //Simulate accountRepo

        DashboardResponse dashboardData = dashboardService.viewTransactionDashboard(user.getId()); //Call the actual service

        assertNotNull(dashboardData); //data fetched, response not null
        assertEquals(user.getId(), dashboardData.user_id());
        AccountDetailDashboardInfo accountInfo = dashboardData.accountDetailDashboardInfo();
        assertNotNull(accountInfo);
        assertEquals(account.getAccountBalance(), accountInfo.accountBalance());
        assertEquals(account.getIban(), accountInfo.iban());
        assertEquals(account.getBic(), accountInfo.bic());

        List<TransactionDto> transactionList = dashboardData.transactionDashboard();
        assertEquals(1, transactionList.size());

        TransactionDto transactionResponse = transactionList.getFirst();
        assertEquals(transaction.getAmount(), transactionResponse.amount());
        assertEquals(transaction.getTransactionType(), transactionResponse.transactionType());
        assertEquals(transaction.getDescription(), transactionResponse.description());
    }
    @Test
    void viewTransactionDashboard_shouldThrowException_whenUserNotFound() {
        when(mockUserRepo.findById(user.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dashboardService.viewTransactionDashboard(user.getId()));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void viewTransactionDashboard_shouldThrowException_whenAccountNotFound() {
        when(mockUserRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(mockTransactionRepo.findAllByUser_Id(user.getId())).thenReturn(List.of(transaction));
        when(mockAccountRepo.findByUserId(user.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dashboardService.viewTransactionDashboard(user.getId()));
        assertEquals("Account not found", exception.getMessage());
    }
}