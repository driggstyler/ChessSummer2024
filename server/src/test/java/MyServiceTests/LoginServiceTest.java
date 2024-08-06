package MyServiceTests;

import Models.User;
import Requests.LoginRequest;
import Results.LoginResult;
import Services.ClearService;
import Services.LoginService;
import dataaccess.DAO.AuthtokenDAO;
import dataaccess.DAO.GameDAO;
import dataaccess.DAO.UserDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginServiceTest {
    @BeforeEach
    public void setup() {
        try (Connection conn = DatabaseManager.getConnection()){
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            GameDAO gameDAO = new GameDAO(conn);
            authtokenDAO.clear();
            gameDAO.clear();
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("JoinGameTest setup threw an exception.");
        }
    }
    @Test
    @DisplayName("Login Success")
    public void loginSuccess() {
        try (Connection conn = DatabaseManager.getConnection()){
            User user = new User("testUser1", "password1", "testEmail1");
            UserDAO userDAO = new UserDAO(conn);
            userDAO.Insert(user);
            LoginRequest loginRequest = new LoginRequest("testUser1", "password1");
            LoginService loginService = new LoginService();
            LoginResult loginResult = loginService.Execute(loginRequest);
            Assertions.assertEquals("Logged in successfully.", loginResult.getMessage());
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("Login Service Test threw an exception.");
        }
    }
    @Test
    @DisplayName("Login Failure")
    public void loginFail() {
        LoginRequest loginRequest = new LoginRequest("testUser1", "password1");
        LoginService loginService = new LoginService();
        LoginResult loginResult = loginService.Execute(loginRequest);
        Assertions.assertEquals("Error: User not found in the database.", loginResult.getMessage());
    }
    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.Execute();
    }
}
