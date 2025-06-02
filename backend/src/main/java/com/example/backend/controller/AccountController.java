package com.example.backend.controller;

import com.example.backend.model.AccountDetail;
import com.example.backend.model.AccountDetailResponse;
import com.example.backend.model.Response;
import com.example.backend.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    //register account
    @PostMapping("/registeraccount")
    public ResponseEntity<Response> registerAccount(@RequestBody AccountDetail accountDetail) {
        return ResponseEntity.ok(accountService.registerAccount(accountDetail));
    }
    @GetMapping("/viewaccountdetails")
    public ResponseEntity<AccountDetailResponse> viewAccountDetails(@RequestParam("user_id") Long user_id) { //GET /api/account/viewaccountdetails?user_id=123
        return  ResponseEntity.ok(accountService.viewAccountDetails(user_id));
    }
}
