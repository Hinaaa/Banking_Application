package com.example.backend.controller;

import com.example.backend.model.AccountDetail;
import com.example.backend.model.Response;
import com.example.backend.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    //register account
    @PostMapping("/accountregister")
    public ResponseEntity<Response> registerAccount(@RequestBody AccountDetail accountDetail) {
        return ResponseEntity.ok(accountService.registerAccount(accountDetail));
    }
}
