package com.example.backend.controller;

import com.example.backend.model.Response;
import com.example.backend.model.UserLogin;
import com.example.backend.model.UserRegister;
import com.example.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public String register(@RequestBody UserRegister user) {
        return userService.registerUser(user);
    }
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody UserLogin user) { //ResponseEntity for whole http response
        return ResponseEntity.ok(userService.loginUser(user));
    }
}
