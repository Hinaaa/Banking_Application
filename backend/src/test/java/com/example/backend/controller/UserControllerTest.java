package com.example.backend.controller;

import com.example.backend.model.UserLogin;
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

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private UserRegister userRegister;
    private UserLogin userLogin;

    @BeforeEach
    void setUp() {
           userRegister = new UserRegister(
                "Jana",
                "John",
                "+49233813219",
                "jana.john@gmail.com",
                "Password@123",
                1994,
                "xyz str. 21",
                "Berlin",
                "13068",
                "Germany"
        );
        userLogin = new UserLogin(null, "jana.john@gmail.com", "Password@123");
    }

    @Test
    void registerUser_shouldReturnSuccessResponse_whenCalledWithValidUserRegisterData() throws Exception {
        String json = objectMapper.writeValueAsString(userRegister);
        System.out.println("Register JSON: " + json); // optional check
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered"))
                .andExpect(jsonPath("$.messageType").value("Success"))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.isRegistered").value(true))
                .andExpect(jsonPath("$.hasAccount").value(false));
    }

    @Test
    void loginUser_shouldReturnSuccessResponse_whenCalledWithValidCredentials() throws Exception {
        mockMvc.perform(post("/api/user/register") // First register
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegister)))
                .andExpect(status().isOk());

        String loginJson = objectMapper.writeValueAsString(userLogin); //login
        System.out.println("Login JSON: " + loginJson); // optional
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.messageType").value(equalToIgnoringCase("success")))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.isRegistered").value(true))
                .andExpect(jsonPath("$.hasAccount").value(false));
    }
    @Test
    void loginUser_shouldReturnUnauthorized_whenCalledWithInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage").value("Error Wrong Email or Password"))
                .andExpect(jsonPath("$.statusCode").value("UNAUTHORIZED"));
    }
}
