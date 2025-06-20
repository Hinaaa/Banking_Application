package com.example.backend.controller;

import com.example.backend.enums.TransactionDirection;
import com.example.backend.enums.TransactionStatus;
import com.example.backend.model.*;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.TransactionRepo;
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

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepo userRepo;

    private Long userId;
    private Long accountId;

    @BeforeEach
    void setUp() throws Exception {
        // Register user with realistic name
        UserRegister userRegister = new UserRegister(
                "Jana",
                "Jorden",
                "+491234567890",
                "jana.jorden@example.com",
                "StrongPass@2025",
                1988,
                "Maple Street 25",
                "Munich",
                "80331",
                "Germany"
        );
        String userJson = objectMapper.writeValueAsString(userRegister);

        MvcResult result = mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered"))
                .andExpect(jsonPath("$.isRegistered").value(true))
                .andReturn();

        String respStr = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(respStr, Response.class);
        userId = response.userId();

        // Create account for the user
        AccountDetail accountDetail = new AccountDetail(
                userId,
                "Jana Jorden",
                "DE44500105175407324931",
                "DEUTDEFF",
                "4111111111111111",
                "Jana Jorden",
                "12/27",
                "321",
                "1234"
        );
        String accountJson = objectMapper.writeValueAsString(accountDetail);
        //register account
        MvcResult accResult = mockMvc.perform(post("/api/account/registeraccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("account registered"))
                .andExpect(jsonPath("$.hasAccount").value(true))
                .andReturn();

        String accResp = accResult.getResponse().getContentAsString();
        Response accResponse = objectMapper.readValue(accResp, Response.class);
        accountId = accResponse.userId();  // typically accountId might be fetched differently, but here we use userId for simplicity
    }

    @Test
    @DirtiesContext
    void addMoney_whenCalledWithValidData_shouldReturnSuccess() throws Exception {
        double currentBalance = 1000.00;
        TransactionDto transactionDto = new TransactionDto(
                userId,
                accountId,
                null,                   // transactionId is null for new transaction
                currentBalance,
                500.00,
                "Salary credit",
                "added",
                "Employer Payroll",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.CREDIT
        );

        String json = objectMapper.writeValueAsString(transactionDto);

        mockMvc.perform(post("/api/account/addMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.transactionType").value("added"))
                .andExpect(jsonPath("$.updatedBalance").exists())
                .andExpect(jsonPath("$.transactionDate").exists())
                .andExpect(jsonPath("$.transactionStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionDirection").value("CREDIT"));
    }
}