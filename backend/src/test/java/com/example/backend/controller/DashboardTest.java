package com.example.backend.controller;

import com.example.backend.enums.TransactionDirection;
import com.example.backend.enums.TransactionStatus;
import com.example.backend.model.AccountDetail;
import com.example.backend.model.Response;
import com.example.backend.model.TransactionDto;
import com.example.backend.model.UserRegister;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DashboardTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private Long accountId;

    @BeforeEach
    void setUp() throws Exception {
        // 1- Register user
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
        MvcResult userResult = mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered"))
                .andExpect(jsonPath("$.isRegistered").value(true))
                .andReturn();
        String userRespStr = userResult.getResponse().getContentAsString();
        Response userResponse = objectMapper.readValue(userRespStr, Response.class);
        userId = userResponse.userId();

        // 2-Register user account for that user
        AccountDetail accountDetail = new AccountDetail(
                userId,
                "Jana Jorden",
                "DE 4450 0105 1754 0732 4931",
                "DEUTDEFF",
                "4111111111111111",
                "Jana Jorden",
                "12/27",
                "321",
                "1234"
        );
        String accountJson = objectMapper.writeValueAsString(accountDetail);
        MvcResult accResult = mockMvc.perform(post("/api/account/registeraccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("account registered"))
                .andExpect(jsonPath("$.hasAccount").value(true))
                .andReturn();
        String accRespStr = accResult.getResponse().getContentAsString();
        Response accResponse = objectMapper.readValue(accRespStr, Response.class);
        accountId = accResponse.userId();
        //Transaction Data
        // Add Money - 1000
        TransactionDto transactionDtoAdd1 = new TransactionDto( //dto for add money transaction
                userId,
                accountId,
                null,   // transactionId is null for new transaction
                0.00,
                1000.00, //amount to add
                "Salary credit",
                "added",
                "Employer Payroll",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.CREDIT
        );
        //transfer money dto 1
        TransactionDto transactionDtoTransfer1 = new TransactionDto(
                userId,
                accountId,
                null,
                1000.00,
                50.00,
                "Transfer amount",
                "DEBIT",
                "Account Holder: John paul, IBAN: DE 7867 8768 3779 3424, BIC: BIX89798",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.DEBIT
        );
        String json1 = objectMapper.writeValueAsString(transactionDtoAdd1);
        String json2 = objectMapper.writeValueAsString(transactionDtoTransfer1);

        //Transaction1: add first transaction 0+1000 = 10000
        mockMvc.perform(post("/api/account/addMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.updatedBalance").value(1000.00))
                .andExpect(jsonPath("$.transactionStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionDirection").value("CREDIT"));

        //Transaction2: first amount transfer - Transfer amount < balance (50 < 1000) => balance = 1000-50 = 950
        mockMvc.perform(post("/api/account/transfermoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("transactionId").exists())
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.transactionType").value("DEBIT"))
                .andExpect(jsonPath("$.updatedBalance").value(950.00)) //balance should be 1000-50
                .andExpect(jsonPath("$.transactionDirection").value(TransactionDirection.DEBIT.toString()))
                .andExpect(jsonPath("$.transactionDate").exists());
    }
    @Test
    @DirtiesContext
    void viewDashboard_whenUserHasCreditedAndDebitedTransactions_shouldReturnAccountInfoAndTransactions() throws Exception {
        //view On Dashboard
        MvcResult dashboardResult = mockMvc.perform(get("/api/account/dashboard")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String content = dashboardResult.getResponse().getContentAsString();
        System.out.println("DASHBOARD JSON: " + content);

        // Now assertions, using observed field names and correct balance:
        mockMvc.perform(get("/api/account/dashboard")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId.intValue()))

                // Account Details
                .andExpect(jsonPath("$.accountDetailDashboardInfo.accountId").value(accountId.intValue()))
                .andExpect(jsonPath("$.accountDetailDashboardInfo.accountBalance").value(950.00))
                .andExpect(jsonPath("$.accountDetailDashboardInfo.accountHolderName").value("Jana Jorden"))
                .andExpect(jsonPath("$.accountDetailDashboardInfo.iban").value("DE 4450 0105 1754 0732 4931"))
                .andExpect(jsonPath("$.accountDetailDashboardInfo.bic").value("DEUTDEFF"))
                .andExpect(jsonPath("$.transactionDashboard.length()").value(2))

                // First transaction: add money
                .andExpect(jsonPath("$.transactionDashboard[0].id").exists())
                .andExpect(jsonPath("$.transactionDashboard[0].userId").value(userId.intValue()))
                .andExpect(jsonPath("$.transactionDashboard[0].accountId").value(accountId.intValue()))
                .andExpect(jsonPath("$.transactionDashboard[0].amount").value(1000.00))
                .andExpect(jsonPath("$.transactionDashboard[0].description").value("Salary credit"))
                .andExpect(jsonPath("$.transactionDashboard[0].transactionType").value("added"))
                .andExpect(jsonPath("$.transactionDashboard[0].transactionFromToAccountDetails").value("Employer Payroll"))
                .andExpect(jsonPath("$.transactionDashboard[0].transactionDate").exists())
                .andExpect(jsonPath("$.transactionDashboard[0].status").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.transactionDashboard[0].transactionDirection").value(TransactionDirection.CREDIT.toString()))

                //Second Transaction - transfer
                .andExpect(jsonPath("$.transactionDashboard[1].id").exists())
                .andExpect(jsonPath("$.transactionDashboard[0].userId").value(userId.intValue()))
                .andExpect(jsonPath("$.transactionDashboard[0].accountId").value(accountId.intValue()))
                .andExpect(jsonPath("$.transactionDashboard[1].accountBalance").value(950.00)) //balance will be calculated after final transaction
                .andExpect(jsonPath("$.transactionDashboard[1].amount").value(50.00))
                .andExpect(jsonPath("$.transactionDashboard[1].transactionType").value("DEBIT"))
                .andExpect(jsonPath("$.transactionDashboard[0].transactionFromToAccountDetails").value("Employer Payroll"))
                .andExpect(jsonPath("$.transactionDashboard[1].transactionDirection").value(TransactionDirection.DEBIT.toString()))
                .andExpect(jsonPath("$.transactionDashboard[1].status").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.transactionDashboard[1].transactionDate").exists());
    }
}