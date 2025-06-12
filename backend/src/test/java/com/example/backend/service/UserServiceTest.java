package com.example.backend.service;

import com.example.backend.exception.InvalidCredentialsException;
import com.example.backend.model.Response;
import com.example.backend.model.User;
import com.example.backend.model.UserLogin;
import com.example.backend.model.UserRegister;
import com.example.backend.repo.AccountRepo;
import com.example.backend.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserRepo mockUserRepo; //Mock dependencies
    private AccountRepo mockAccountRepo;
    private UserService userService; //service under test
    UserRegister userRegister; //sample input and output objects used in the tests to simulate real data:
    UserLogin userLogin;
    User user;

    @BeforeEach
        //before each test
    void setUp() {
        mockUserRepo = Mockito.mock(UserRepo.class); // Create mocks for UserRepo
        mockAccountRepo = Mockito.mock(AccountRepo.class); //Create mocks for AccountRepo
        userService = new UserService(mockUserRepo, mockAccountRepo); //initialized service with mock repo

        //registration DTO hardcoded input
        userRegister = new UserRegister(
                "Jana",
                "Jorden",
                "1234567890",
                "jana.jorden@example.com",
                "test123",
                1990,
                "123 abc Str",
                "Berlin",
                "13086",
                "Germany"
        );
        //Login DTO hardcoded input
        userLogin = new UserLogin(
                null, //cant give 1 becuase its being created origninally in entity
                "jana.jorden@example.com",
                "test123"
        );

        // Entity to simulate saved User with assigned ID
        user = new User(
                1L,
                "Jana",
                "Jorden",
                "jana.jorden@example.com",
                "test123",
                "1234567890",
                1990,
                "123 abc Str",
                "Berlin",
                "13086",
                "Germany"
        );
    }

    @Test //register
    void registerUser_shouldSaveUser_andReturnSuccessResponse() {
        Mockito.when(mockUserRepo.save(Mockito.any(User.class)))// Mock save method of UserRepo to return the prepared user when saving any User object
                .thenReturn(user); // Mock, when service calls userRepo.save() it return hard-coded user defined above
        Response response = userService.registerUser(userRegister); //real service method called with input dto which mocked
        //assert response
        String expected = "User registered"; //expected defined as per out response it should be returned
        String actual = response.message();//Actual: response.message() obtained from service result
        assertEquals(expected,actual); //assert
        assertEquals("Success", response.messageType());//status assert
        assertTrue(response.isRegistered());
        assertFalse(response.hasAccount());
        // Verify interactions
        Mockito.verify(mockUserRepo, Mockito.times(1)).save(Mockito.any(User.class)); //varify save
    }
    @Test //login
    void loginUser_shouldReturnSuccessResponse_whenCredentialsAreValid() {
        Mockito.when(mockUserRepo.findByEmail(userLogin.email())).thenReturn(user); // Mock findByEmail() to return simulated user
        Response response = userService.loginUser(userLogin); //real service response
        assertEquals("Login successful", response.message());
        assertEquals("success", response.messageType());
        assertEquals(user.getId(), response.userId());
        assertTrue(response.isRegistered());
        assertFalse(response.hasAccount()); //by default value
        Mockito.verify(mockUserRepo, Mockito.times(1)).findByEmail(userLogin.email());
    }
    @Test //validate error message
    void loginUser_shouldReturnErrorResponse_whenInValidPassword() {
        User invalidPasswordUser = new User(1L, "Jana", "Jorden",
                "jana.jorden@example.com", "invalidPassword",
                "1234567890", 1990,
                "123 abc Str", "Berlin", "13086", "Germany"); //simulate user invalid password, db entity simulation
        Mockito.when(mockUserRepo.findByEmail(userLogin.email())).thenReturn(invalidPasswordUser); //by this, userLogin.email() return this invalidPasswordUser object."
        // Response response = userService.loginUser(userLogin); //call response
        InvalidCredentialsException thrown = assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(userLogin);
        }); //loginUser method is throwing an InvalidCredentialsException instead of response
        assertEquals("Wrong Email or Password", thrown.getMessage());
    }
    @Test //validate error message
    void loginUser_shouldThrowException_whenUserNotFound() {
           Mockito.when(mockUserRepo.findByEmail(userLogin.email())).thenReturn(null); //by this, userLogin.email() return null
        // Response response = userService.loginUser(userLogin); //call response
        InvalidCredentialsException thrown = assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(userLogin);
        });
        assertEquals("Wrong Email or Password", thrown.getMessage());
    }
    //when email null or empty
    @Test
    void loginUser_shouldThrowException_whenEmailIsNull() {
        UserLogin invalidLogin = new UserLogin(null, null, "test123");
        InvalidCredentialsException thrown = assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(invalidLogin);
        });
        assertEquals("Wrong Email or Password", thrown.getMessage()); // Or your validation message
    }

    @Test
    void loginUser_shouldThrowException_whenEmailIsEmpty() {
        UserLogin invalidLogin = new UserLogin(null, "", "test123");
        InvalidCredentialsException thrown = assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(invalidLogin);
        });
        assertEquals("Wrong Email or Password", thrown.getMessage());
    }
    //when password null or empty
    @Test
    void loginUser_shouldThrowException_whenPasswordIsNull() {
        UserLogin invalidLogin = new UserLogin(null, "jana.jorden@example.com", null);
        InvalidCredentialsException thrown = assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(invalidLogin);
        });
        assertEquals("Wrong Email or Password", thrown.getMessage()); // Your validation message
    }

    @Test
    void loginUser_shouldThrowException_whenPasswordIsEmpty() {
        UserLogin invalidLogin = new UserLogin(null, "jana.jorden@example.com", "");
        InvalidCredentialsException thrown = assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(invalidLogin);
        });
        assertEquals("Wrong Email or Password", thrown.getMessage());
    }
}

//Unit test: Test the service layer by mocking the repositories (use Mockito to fake repo behavior).