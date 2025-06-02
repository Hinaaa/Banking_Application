package com.example.backend.service;

import com.example.backend.exception.InvalidCredentialsException;
import com.example.backend.model.Response;
import com.example.backend.model.User;
import com.example.backend.model.UserLogin;
import com.example.backend.model.UserRegister;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;

    public UserService(UserRepo userRepo, AccountRepo accountRepo) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
    }
    //register user
    public Response registerUser(UserRegister userRegister) {

        User user = new User(); // Map record to entity
        user.setFirstName(userRegister.firstName());
        user.setLastName(userRegister.lastName());
        user.setEmail(userRegister.email());
        user.setPassword(userRegister.password()); // In real apps, hash this
        user.setPhoneNumber(userRegister.phoneNumber());
        user.setYearOfBirth(userRegister.yearOfBirth());
        user.setStreetAddress(userRegister.streetAddress());
        user.setCity(userRegister.city());
        user.setPostalCode(userRegister.postalCode());
        user.setCountry(userRegister.country());
        userRepo.save(user);
        return new Response("User registered", "Success", user.getId(), true, false); //is registered = true, have account = false

    }
    //login user
    public Response loginUser(UserLogin userLogin) {
        //db mapping:
        User registeredUser = userRepo.findByEmail(userLogin.email()); // fetching user from db

        if (registeredUser == null
                || !userLogin.email().equals(registeredUser.getEmail())
                || !userLogin.password().equals(registeredUser.getPassword())) {
            //return new Response( "Wrong email or password","fail");
            throw new InvalidCredentialsException("Wrong Email or Password");
        }
        boolean accountExists = accountRepo.existsByUser(registeredUser);//check if user exists return true or false accordingly
        return new Response("Login successful", "success",registeredUser.getId(),true,accountExists); //as response needed more details so userRegistered = true (just registered here) and accountCreated = false (as no account created when user newly registered)
    }
}
