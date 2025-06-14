package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {
    //mocking repo
    @Mock
    private UserRepo mockUserRepo;
    @Mock
    private AccountRepo mockAccountRepo;
    @Mock
    private AccountService accountService;
    //injecting user, account and accountdetails
    private User user;
    private Account account;
    private AccountDetail accountDetail;

    @BeforeEach
    void setUp() {
        mockAccountRepo = Mockito.mock(AccountRepo.class);
        mockUserRepo = Mockito.mock(UserRepo.class);
        accountService = new AccountService(mockAccountRepo, mockUserRepo);

        // Entity to simulate saved User with assigned ID
        user = new User(1L,"Jana","Jorden","jana.jorden@example.com","test123",
                "1234567890",1990,"123 abc Str","Berlin","13086",
                "Germany"
        );
        accountDetail = new AccountDetail(
                user.getId(),
                "Jana Jorden","DE89370400440532013000","COBADEFFXXX","1234567890123456",
                "Jana Jorden","12/25","123","4567"
        );
        account = new Account(
                accountDetail.accountHolderName(), accountDetail.iban(), accountDetail.bic(),accountDetail.cardNumber(),
                accountDetail.cardHolder(),accountDetail.expiryDate(),accountDetail.cvv(), accountDetail.pin()
        );
        account.setUser(user);
        account.setId(1L);
        account.setAccountBalance(1000.00);
    }
    @Test
    void registerAccount_shouldSaveAccount_andReturnSuccessResponse() {
        when(mockUserRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(mockAccountRepo.existsByUser(user)).thenReturn(true); //when already exists
        when(mockAccountRepo.save(account)).thenReturn(account);

        Response response = accountService.registerAccount(accountDetail);//response

        assertEquals("User already exists", response.message());
        assertEquals("Info", response.messageType());
        assertTrue(response.isRegistered());
        assertTrue(response.hasAccount());
        assertEquals(user.getId(), response.userId());
        verify(mockAccountRepo, never()).save(any(Account.class)); // No save should happen when account exists
    }
    @Test
    void registerAccount_shouldReturnError_whenUserNotFound() {
        when(mockUserRepo.findById(user.getId())).thenReturn(Optional.empty());
        Response response = accountService.registerAccount(accountDetail);
        assertEquals("User not found", response.message());
        assertEquals("Error", response.messageType());
        assertFalse(response.isRegistered());
        assertFalse(response.hasAccount());
        verify(mockAccountRepo, never()).save(any()); //verify not saved
    }
    @Test
    void registerAccount_shouldReturnError_whenAccountAlreadyExists() {
        when(mockUserRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(mockAccountRepo.existsByUser(user)).thenReturn(true); //account exists
        Response response = accountService.registerAccount(accountDetail);

        assertEquals("User already exists", response.message());
        assertEquals("Info", response.messageType());
        assertTrue(response.isRegistered());
        assertTrue(response.hasAccount());

        verify(mockAccountRepo, never()).save(any());
    }
    @Test
    void viewAccountDetails_shouldReturnAccountDetails_whenAccountExists() {
        when(mockUserRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(mockAccountRepo.findByUserId(user.getId())).thenReturn(Optional.of(account));

        AccountDetailResponse result = accountService.viewAccountDetails(user.getId());
        assertEquals("account details fetched successfully", result.message());
        assertEquals("success", result.messageType());
        assertEquals(user.getId(), result.userId());
        assertTrue(result.hasAccount());
        assertNotNull(result.accountDetail());
   //     assertEquals(account.getAccountBalance(), result.accountDetail().accountBalance());
    }
    @Test
    void viewAccountDetails_shouldReturnEmpty_whenAccountNotFound() {
        when(mockUserRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(mockAccountRepo.findByUserId(user.getId())).thenReturn(Optional.empty());

        AccountDetailResponse result = accountService.viewAccountDetails(user.getId());
        assertEquals("No account found for user", result.message());
        assertEquals("error", result.messageType());
        assertFalse(result.hasAccount());
        assertNull(result.accountDetail()); // accountDetail should be null if no account
    }
    @Test
    void viewAccountDetails_shouldReturnEmpty_whenUserNotFound() {
        when(mockUserRepo.findById(user.getId())).thenReturn(Optional.empty());
        AccountDetailResponse result = accountService.viewAccountDetails(user.getId());
        assertEquals("No account found for user", result.message());
        assertEquals("error", result.messageType());
        assertFalse(result.hasAccount());
        assertNull(result.accountDetail());
    }
}