package service;

import models.Authtoken;
import results.ListGamesResult;
import services.ClearService;
import services.ListGamesService;
import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class ListGamesServiceTest {
    @BeforeEach
    public void setup() {
        try (Connection conn = DatabaseManager.getConnection()){
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            GameDAO gameDAO = new GameDAO(conn);
            gameDAO.clear();
            authtokenDAO.clear();
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("JoinGameTest setup threw an exception.");
        }
    }
    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() {
        try (Connection conn = DatabaseManager.getConnection()){
            Authtoken authtoken = new Authtoken("abcdefg", "testUser1");
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            authtokenDAO.insert(authtoken);
            ListGamesService listGamesService = new ListGamesService();
            ListGamesResult listGamesResult = listGamesService.execute("abcdefg");
            Assertions.assertEquals("Listed games successfully.", listGamesResult.getMessage());
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("List games test threw an exception.");
        }
    }
    @Test
    @DisplayName("List Games Failure")
    public void listGamesFail() {
        ListGamesService listGamesService = new ListGamesService();
        ListGamesResult listGamesResult = listGamesService.execute("abcdefg");
        Assertions.assertEquals("Error: Unauthorized", listGamesResult.getMessage());
    }
    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.execute();
    }
}
