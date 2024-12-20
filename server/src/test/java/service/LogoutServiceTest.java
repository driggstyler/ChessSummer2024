package service;

import models.Authtoken;
import results.LogoutResult;
import services.ClearService;
import services.LogoutService;
import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class LogoutServiceTest {
    @BeforeEach
    public void setup() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gamerDAO = new GameDAO(conn);
            AuthtokenDAO authentokenDAO = new AuthtokenDAO(conn);
            gamerDAO.clear();
            authentokenDAO.clear();
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("JoinGameTest setup threw an exception.");
        }
    }
    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            Authtoken authtoken = new Authtoken("abcdefg", "testUser1");
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            authtokenDAO.insert(authtoken);
            LogoutService logoutService = new LogoutService();
            LogoutResult logoutResult = logoutService.execute("abcdefg");
            Assertions.assertEquals("Logged out successfully.", logoutResult.getMessage());
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("Logout Service Test threw an exception.");
        }
    }
    @Test
    @DisplayName("Logout Failure")
    public void logoutFail() {
        LogoutService logoutService = new LogoutService();
        LogoutResult logoutResult = logoutService.execute("abcdefg");
        Assertions.assertEquals("Error: Unauthorized", logoutResult.getMessage());
    }
    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.execute();
    }
}
