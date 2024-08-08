package service;

import Models.Authtoken;
import Models.Game;
import Requests.JoinGameRequest;
import Results.JoinGameResult;
import services.ClearService;
import services.JoinGameService;
import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class JoinGameServiceTest {
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
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {
        try (Connection conn = DatabaseManager.getConnection()){
            Authtoken authtoken = new Authtoken("abcdefg", "testUser1");
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            authtokenDAO.Insert(authtoken);
            GameDAO gameDAO = new GameDAO(conn);
            Game game = new Game();
            gameDAO.Insert(101, game, "First Game");
            JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 101);
            JoinGameService joinGameService = new JoinGameService();
            JoinGameResult joinGameResult = joinGameService.Execute(joinGameRequest, "abcdefg");
            Assertions.assertEquals("Joined game successfully.", joinGameResult.getMessage());
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("Join Game Test threw an exception.");
        }
    }
    @Test
    @DisplayName("Join Game Failure")
    public void joinGameFail() {
        JoinGameRequest joinGameRequest = new JoinGameRequest("White", 101);
        JoinGameService joinGameService = new JoinGameService();
        JoinGameResult joinGameResult = joinGameService.Execute(joinGameRequest, "abcdefg");
        Assertions.assertEquals("Error: Unauthorized.", joinGameResult.getMessage());
    }
    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.Execute();
    }
}
