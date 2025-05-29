package com.example.backend.service;

import com.example.backend.exception.InvalidCredentialsException;
import com.example.backend.model.Response;
import com.example.backend.model.User;
import com.example.backend.model.UserLogin;
import com.example.backend.model.UserRegister;
import com.example.backend.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    public String registerUser(UserRegister userRegister) {

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
        return "user registered";
    }

    public Response loginUser(UserLogin userLogin) {
        //db mapping:
        User registeredUser = userRepo.findByEmail(userLogin.email()); // fetching user from db

        if (registeredUser == null
                || !userLogin.email().equals(registeredUser.getEmail())
                || !userLogin.password().equals(registeredUser.getPassword())) {
            //return new Response( "Wrong email or password","fail");
            throw new InvalidCredentialsException("Wrong Email or Password");
        }
        return new Response("Login successful", "success",registeredUser.getId());
    }

    public Response registerAccount(UserRegister user) {
        return null;
    }
}
