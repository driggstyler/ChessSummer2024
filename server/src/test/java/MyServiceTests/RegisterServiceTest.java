package MyServiceTests;

import Requests.RegisterRequest;
import Results.RegisterResult;
import Services.ClearService;
import Services.RegisterService;
import dataaccess.DAO.AuthtokenDAO;
import dataaccess.DAO.GameDAO;
import dataaccess.DAO.UserDAO;
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
        RegisterResult registerResult = registerService.Execute(registerRequest);
        Assertions.assertEquals("Registered successfully.", registerResult.getMessage());
    }
    @Test
    @DisplayName("Register Failure")
    public void registerFail() {
        RegisterRequest registerRequest = new RegisterRequest("testUser1", null, null);
        RegisterService registerService = new RegisterService();
        RegisterResult registerResult = registerService.Execute(registerRequest);
        Assertions.assertEquals("Error: Missing information to register.", registerResult.getMessage());
    }
    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.Execute();
    }
}
