package com.example.backend.controller;

import com.example.backend.enums.TransactionDirection;
import com.example.backend.enums.TransactionStatus;
import com.example.backend.model.*;
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
        double currentBalance = 0.00;
        TransactionDto transactionDto = new TransactionDto( //dto for add money transaction
                userId,
                accountId,
                null,   // transactionId is null for new transaction
                currentBalance,
                500.00, //amount to add
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
                .andExpect(jsonPath("$.updatedBalance").value(500.00))
                .andExpect(jsonPath("$.transactionDate").exists())
                .andExpect(jsonPath("$.transactionStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionDirection").value("CREDIT"));
    }
//multiple time add money
@Test
@DirtiesContext
void addMoney_whenCalledMultipleTimesWithValidData_shouldReturnSuccess() throws Exception {
    TransactionDto transactionDtoAdd1 = new TransactionDto( //dto for first add money transaction
            userId,
            accountId,
            null,   // transactionId is null for new transaction
            0.00,
            500.00, //amount to add
            "Salary credit",
            "added",
            "Employer Payroll",
            new Date(),
            TransactionStatus.SUCCESS,
            TransactionDirection.CREDIT
    );
    TransactionDto transactionDtoAdd2 = new TransactionDto( //dto for second add money transaction
            userId,
            accountId,
            null,
            500.00,
            300.00, //amount to add
            "Salary credit",
            "added",
            "Employer Payroll",
            new Date(),
            TransactionStatus.SUCCESS,
            TransactionDirection.CREDIT
    );
    String json1 = objectMapper.writeValueAsString(transactionDtoAdd1);
    String json2 = objectMapper.writeValueAsString(transactionDtoAdd2);
    //first transaction - add money
    mockMvc.perform(post("/api/account/addMoney")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Transaction Successful"))
            .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
            .andExpect(jsonPath("$.transactionType").value("added"))
            .andExpect(jsonPath("$.updatedBalance").value(500.00))
            .andExpect(jsonPath("$.transactionDate").exists())
            .andExpect(jsonPath("$.transactionStatus").value("SUCCESS"))
            .andExpect(jsonPath("$.transactionDirection").value("CREDIT"));

    //Second transaction - add money
    mockMvc.perform(post("/api/account/addMoney")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Transaction Successful"))
            .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
            .andExpect(jsonPath("$.transactionType").value("added"))
            .andExpect(jsonPath("$.updatedBalance").value(800.00)) //500 (from last add money transaction) + 300 (This transaction) = 800
            .andExpect(jsonPath("$.transactionDate").exists())
            .andExpect(jsonPath("$.transactionStatus").value("SUCCESS"))
            .andExpect(jsonPath("$.transactionDirection").value("CREDIT"));
}


    @Test
    @DirtiesContext
    void transferMoney_whenCalledWith_CurrentAccountBalanceGreaterThanEnteredAmount_shouldReturnSuccess() throws Exception {
        //first add amount
        TransactionDto transactionDtoAdd = new TransactionDto( //dto for add money transaction
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

        String json = objectMapper.writeValueAsString(transactionDtoAdd);

        mockMvc.perform(post("/api/account/addMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.updatedBalance").value(1000.00))
                .andExpect(jsonPath("$.transactionStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionDirection").value("CREDIT"));

        // Transfer amount < balance
        TransactionDto transactionDtoTransfer = new TransactionDto(
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

        String json2 = objectMapper.writeValueAsString(transactionDtoTransfer);

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
    void transferMoney_whenCalledWith_CurrentAccountBalanceLessThanEnteredAmount_shouldReturnError() throws Exception {
        //first add amount
        TransactionDto transactionDtoAdd = new TransactionDto( //dto for add money transaction
                userId,
                accountId,
                null,
                0.00,
                1000.00, //amount to add
                "Salary credit",
                "added",
                "Employer Payroll",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.CREDIT
        );

        String json = objectMapper.writeValueAsString(transactionDtoAdd);
        mockMvc.perform(post("/api/account/addMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.updatedBalance").value(1000.00))
                .andExpect(jsonPath("$.transactionStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionDirection").value("CREDIT"));

        // Transfer amount > balance
        TransactionDto transactionDtoTransfer = new TransactionDto(
                userId,
                accountId,
                null,
                1000.00,
                1500.00,
                "Transfer amount",
                "DEBIT",
                "Account Holder: John paul, IBAN: DE 7867 8768 3779 3424, BIC: BIX89798",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.DEBIT
        );

        String json2 = objectMapper.writeValueAsString(transactionDtoTransfer);

        mockMvc.perform(post("/api/account/transfermoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Failed: Account Balance low to transfer requested amount"))
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.FAILED.toString()))
                .andExpect(jsonPath("$.updatedBalance").value(1000.00)); //balance should remain same

    }

    @Test
    @DirtiesContext
    void transferMoney_whenCalledWith_CurrentAccountBalanceDoesNotExists_shouldReturnError() throws Exception {
        // Money not added before transfer
        TransactionDto transactionDto = new TransactionDto(
                userId,
                accountId,
                null,
                0.00,
                50.00,
                "Transfer amount",
                "DEBIT",
                "Account Holder: John paul, IBAN: DE 7867 8768 3779 3424, BIC: BIX89798",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.DEBIT
        );

        String json = objectMapper.writeValueAsString(transactionDto);

        mockMvc.perform(post("/api/account/transfermoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Failed: Account Balance low to transfer requested amount"))
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.FAILED.toString()));
    }

    //multiple transfer transactions
    @Test
    @DirtiesContext
    void transferMoney_whenMultipleTimesCalledWith_CurrentAccountBalanceGreaterThanEnteredAmount_shouldReturnSuccess() throws Exception {
        //first add amount
        TransactionDto transactionDtoAdd = new TransactionDto( //dto for add money transaction
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

        String json = objectMapper.writeValueAsString(transactionDtoAdd);

        mockMvc.perform(post("/api/account/addMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.updatedBalance").value(1000.00))
                .andExpect(jsonPath("$.transactionStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionDirection").value("CREDIT"));

        // Transfer amount < balance
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
        TransactionDto transactionDtoTransfer2 = new TransactionDto(
                userId,
                accountId,
                null,
                950.00, //current balance after transfer 1
                60.00, // for transfer 2
                "Transfer amount",
                "DEBIT",
                "Account Holder: John paul, IBAN: DE 7867 8768 3779 3424, BIC: BIX89798",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.DEBIT
        );

        //First transfer
        String json2 = objectMapper.writeValueAsString(transactionDtoTransfer1);
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

        //Second transfer
        String json3 = objectMapper.writeValueAsString(transactionDtoTransfer2);
        mockMvc.perform(post("/api/account/transfermoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("transactionId").exists())
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.transactionType").value("DEBIT"))
                .andExpect(jsonPath("$.updatedBalance").value(890.00)) //balance should be 950-50
                .andExpect(jsonPath("$.transactionDirection").value(TransactionDirection.DEBIT.toString()))
                .andExpect(jsonPath("$.transactionDate").exists());
    }
    //multiple add/transfer transactions together
    @Test
    @DirtiesContext
    void AddMoneyAndTransferMoney_whenMultipleTimesCalledTogetherWith_CurrentAccountBalanceGreaterThanEnteredAmount_shouldReturnSuccess() throws Exception {
        //add amount dto1
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
        //add money dto2
        TransactionDto transactionDtoAdd2 = new TransactionDto( //dto for add money transaction
                userId,
                accountId,
                null,
                950.00,
                40.50, //amount to add
                "Salary credit",
                "added",
                "Employer Payroll",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.CREDIT
        );
        //transfer dto2
        TransactionDto transactionDtoTransfer2 = new TransactionDto(
                userId,
                accountId,
                null,
                990.50, //current balance
                60.30, // for transfer 2
                "Transfer amount",
                "DEBIT",
                "Account Holder: John paul, IBAN: DE 7867 8768 3779 3424, BIC: BIX89798",
                new Date(),
                TransactionStatus.SUCCESS,
                TransactionDirection.DEBIT
        );

        String json1 = objectMapper.writeValueAsString(transactionDtoAdd1);
        String json2 = objectMapper.writeValueAsString(transactionDtoTransfer1);
        String json3 = objectMapper.writeValueAsString(transactionDtoAdd2);
        String json4 = objectMapper.writeValueAsString(transactionDtoTransfer2);

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

        //transaction3 - add money again (950.00+ 40.50 = 990.50)
        mockMvc.perform(post("/api/account/addMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.updatedBalance").value(990.50)) //950.00+40.50
                .andExpect(jsonPath("$.transactionStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionDirection").value("CREDIT"));

        //transaction4 - Transfer money again (990.50 - 60.30 = 930.20)
        mockMvc.perform(post("/api/account/transfermoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("transactionId").exists())
                .andExpect(jsonPath("$.transactionStatus").value(TransactionStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.transactionType").value("DEBIT"))
                .andExpect(jsonPath("$.updatedBalance").value(930.20)) //balance should be 990.50 - 60.30
                .andExpect(jsonPath("$.transactionDirection").value(TransactionDirection.DEBIT.toString()))
                .andExpect(jsonPath("$.transactionDate").exists());
    }

}

//when balance < amount