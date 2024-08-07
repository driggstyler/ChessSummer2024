package service;

import requests.RegisterRequest;
import results.LoginResult;
import services.ClearService;
import services.RegisterService;
import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.UserDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class RegisterServiceTest {
    @BeforeEach
    public void setup() {
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            GameDAO gameDAO = new GameDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            authtokenDAO.clear();
            gameDAO.clear();
            userDAO.clear();
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("JoinGameTest setup threw an exception.");
        }
    }
    @Test
    @DisplayName("Register Success")
    public void registerSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("testUser1", "password1", "testEmail1");
        RegisterService registerService = new RegisterService();
        LoginResult loginResult = registerService.execute(registerRequest);
        Assertions.assertEquals("Registered successfully.", loginResult.getMessage());
    }
    @Test
    @DisplayName("Register Failure")
    public void registerFail() {
        RegisterRequest registerRequest = new RegisterRequest("testUser1", null, null);
        RegisterService registerService = new RegisterService();
        LoginResult loginResult = registerService.execute(registerRequest);
        Assertions.assertEquals("Error: Missing information to register.", loginResult.getMessage());
    }
    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.execute();
    }
}
