package com.example.backend.controller;

import com.example.backend.model.Account;
import com.example.backend.model.AccountDetail;
import com.example.backend.model.Response;
import com.example.backend.model.UserRegister;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AccountRepo accountRepo;

    private Long userId;
    @BeforeEach
    void setUp() throws Exception {
        UserRegister userRegister = new UserRegister(
                "Tester",
                "Integration",
                "+491234567890",
                "test.user@example.com",
                "Password@123",
                1993,
                "Test Street 1",
                "Berlin",
                "1323",
                "Germany"
        );
        String userJson = objectMapper.writeValueAsString(userRegister);
        //register user
        MvcResult result = mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered"))
                .andExpect(jsonPath("$.messageType").value("Success"))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.isRegistered").value(true))
                .andExpect(jsonPath("$.hasAccount").value(false))
                .andReturn();
        //extract return user Id
        String respStr = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(respStr, Response.class);
        userId = response.userId();
    }
    @Test
    @DirtiesContext
    void registerAccount_whenUserExists_andCalledWithValidData_shouldReturnsSuccess() throws Exception {
        AccountDetail accountDetail = new AccountDetail(
                userId,                    // userId
                "Holder Name",                   // accountHolderName
                "DE89370400440532013000",        // iban
                "COBADEFFXXX",                   // bic
                "1234567812345678",              // cardNumber
                "Holder Name",                   // cardHolder
                "12/25",                         // expiryDate
                "123",                           // cvv
                "0000"                           // pin
        );
        String json = objectMapper.writeValueAsString(accountDetail);
        mockMvc.perform(post("/api/account/registeraccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                // THEN: expect success response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("account registered"))
                .andExpect(jsonPath("$.messageType").value("Success"))
                .andExpect(jsonPath("$.userId").value(userId.intValue()))
                .andExpect(jsonPath("$.isRegistered").value(true))
                .andExpect(jsonPath("$.hasAccount").value(true));
    }
    @Test
    @DirtiesContext
    void registerAccount_whenUserNotExists_andCalled_shouldReturnsError() throws Exception {
        Long nonExistentUserId = userId + 999999;
        AccountDetail accountDetail = new AccountDetail(
                nonExistentUserId,
                "NoUser Holder",
                "DE 7834 3842 8934 3232",
                "BIX27380323",
                "8765432187654321",
                "NoUser Holder",
                "01/26",
                "321",
                "1111"
        );
        String json = objectMapper.writeValueAsString(accountDetail);
        mockMvc.perform(post("/api/account/registeraccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User not found")) //error response
                .andExpect(jsonPath("$.messageType").value("Error"))
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.isRegistered").value(false))
                .andExpect(jsonPath("$.hasAccount").value(false));
    }
    @Test
    @DirtiesContext
    void registerAccount_whenAccountAlreadyExists_andCalled_shouldReturnsError() throws Exception {
       //pre saving account via repository
        Account existing = new Account();
        existing.setUser(userRepo.findById(userId).orElseThrow());
        existing.setAccountHolderName("Tester Exists");
        existing.setIban("DE 2222 4243 4324 3345 3435");
        existing.setBic("EXAMPDEFFXXX");
        existing.setCardNumber("DE 2222 4243 4324 3345 3435");
        existing.setCardHolder("Tester Exists");
        existing.setExpiryDate("11/25");
        existing.setCvv("999");
        existing.setPin("2222");
        accountRepo.save(existing);

        //AccountDetail matching existing user
        AccountDetail accountDetail = new AccountDetail(
                userId,
                "Tester Exists",
                "DE 2222 4243 4324 3345 3435",
                "EXAMPDEFFXXX",
                "DE 2222 4243 4324 3345 3435",
                "Tester Exists",
                "11/25",
                "999",
                "2222"
        );
        String json = objectMapper.writeValueAsString(accountDetail);

        mockMvc.perform(post("/api/account/registeraccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User already exists"))
                .andExpect(jsonPath("$.messageType").value("Info"))
                .andExpect(jsonPath("$.userId").value(userId.intValue()))
                .andExpect(jsonPath("$.isRegistered").value(true))
                .andExpect(jsonPath("$.hasAccount").value(true));
    }
    @Test
    @DirtiesContext
    void viewAccountDetails_whenAccountAlreadyExists_andCalledWithValidData_shouldReturnsSuccess() throws Exception {
        //set account exists
        Account account = new Account();
        account.setUser(userRepo.findById(userId).orElseThrow());
        account.setAccountHolderName("Tester View");
        account.setIban("DE 3333 3424 3332 3413 9831");
        account.setBic("VIEWDEFFXXX");
        account.setCardNumber("4444333322221111");
        account.setCardHolder("Tester View");
        account.setExpiryDate("10/26");
        account.setCvv("555");
        account.setPin("3333");
        Account savedAcc = accountRepo.save(account);
        Long accountId = savedAcc.getId();

        //Get
        mockMvc.perform(get("/api/account/viewaccountdetails")
                        .param("user_id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("account details fetched successfully"))
                .andExpect(jsonPath("$.messageType").value("success"))
                .andExpect(jsonPath("$.userId").value(userId.intValue()))
                .andExpect(jsonPath("$.accountId").value(accountId.intValue()))
                .andExpect(jsonPath("$.hasAccount").value(true))
                .andExpect(jsonPath("$.accountDetail.userId").value(userId.intValue()))
                .andExpect(jsonPath("$.accountDetail.accountHolderName").value("Tester View"))
                .andExpect(jsonPath("$.accountDetail.iban").value("DE 3333 3424 3332 3413 9831"))
                .andExpect(jsonPath("$.accountDetail.bic").value("VIEWDEFFXXX"))
                .andExpect(jsonPath("$.accountDetail.cardNumber").value("4444333322221111"))
                .andExpect(jsonPath("$.accountDetail.cardHolder").value("Tester View"))
                .andExpect(jsonPath("$.accountDetail.expiryDate").value("10/26"))
                .andExpect(jsonPath("$.accountDetail.cvv").value("555"))
                .andExpect(jsonPath("$.accountDetail.pin").value("3333"));
    }
    @Test
    @DirtiesContext
    void viewAccountDetails_whenNoAccountExists_andCalledWith_shouldReturnsError() throws Exception {
        //get without creating account
        mockMvc.perform(get("/api/account/viewaccountdetails")
                        .param("user_id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No account found for user")) //"No account found" response
                .andExpect(jsonPath("$.messageType").value("error"))
                .andExpect(jsonPath("$.userId").value(userId.intValue()))
                .andExpect(jsonPath("$.accountId").doesNotExist())
                .andExpect(jsonPath("$.hasAccount").value(false))
                .andExpect(jsonPath("$.accountDetail").doesNotExist());
    }
    @Test
    @DirtiesContext
    void viewAccountDetails_whenNoAUserExists_andCalledWith_shouldReturnsError() throws Exception {
        //get without creating account
        Long nonExistentUserId = userId + 972500;

        //Get
        mockMvc.perform(get("/api/account/viewaccountdetails")
                        .param("user_id", nonExistentUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No account found for user"))
                .andExpect(jsonPath("$.messageType").value("error"))
                .andExpect(jsonPath("$.userId").value(nonExistentUserId.intValue()))
                .andExpect(jsonPath("$.accountId").doesNotExist())
                .andExpect(jsonPath("$.hasAccount").value(false))
                .andExpect(jsonPath("$.accountDetail").doesNotExist());
    }
}