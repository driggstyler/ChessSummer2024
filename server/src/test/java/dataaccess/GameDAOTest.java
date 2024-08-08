package dataaccess;

import models.Game;
import models.User;
import services.ClearService;
import dataaccess.dao.GameDAO;
import dataaccess.dao.UserDAO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class GameDAOTest {
    @BeforeEach
    public void setup() {

    }

    @Test
    @DisplayName("ClaimSpot success")
    public void claimSpotSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            gameDAO.Insert(123, new Game(), "FirstGame");
            userDAO.Insert(new User("John", "Johnson", "JJ@gmail.com"));
            boolean joined = gameDAO.claimSpot(123, "WHITE", "John");
            Assertions.assertTrue(joined);
        } catch (DataAccessException | SQLException e) {
            System.out.println("GameDAO ClaimSpot threw an exception");
        }
    }

    @Test
    @DisplayName("ClaimSpot failed")
    public void claimSpotFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            boolean joined = gameDAO.claimSpot(123, "WHITE", "John");
            Assertions.assertFalse(joined);
        } catch (DataAccessException | SQLException e) {
            System.out.println("GameDAO ClaimSpotFail threw an exception");
        }
    }

    @Test
    @DisplayName("Insert successful")
    public void insertSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            gameDAO.Insert(123, new Game(), "FirstGame");
            Assertions.assertNotEquals(null, gameDAO.Find(123));
        } catch (DataAccessException | SQLException e) {
            System.out.println("GameDAO Insert threw an exception");
        }
    }

    @Test
    @DisplayName("Insert failed")
    public void insertFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            gameDAO.Insert(123, new Game(), "FirstGame");
            gameDAO.clear();
            Assertions.assertEquals(null, gameDAO.Find(123));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("GameDAO InsertFail threw an exception");
        }
    }

    @Test
    @DisplayName("Find successful")
    public void findSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            gameDAO.Insert(123, new Game(), "FirstGame");
            Assertions.assertNotEquals(null, gameDAO.Find(123));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("GameDAO Find threw an exception");
        }
    }

    @Test
    @DisplayName("Find failed")
    public void findFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            Assertions.assertEquals(null, gameDAO.Find(123));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("GameDao FindFail threw an exception");
        }
    }

    @Test
    @DisplayName("FindAll success")
    public void findAllSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            gameDAO.Insert(123, new Game(), "FirstGame");
            gameDAO.Insert(456, new Game(), "SecondGame");
            ArrayList<Game> list = gameDAO.FindAll();
            Assertions.assertFalse(list.isEmpty());
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("GameDAO FindAll threw an exception");
        }
    }

    @Test
    @DisplayName("FindAll failed")
    public void findAllFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            gameDAO.Insert(123, new Game(), "FirstGame");
            gameDAO.Insert(456, new Game(), "SecondGame");
            gameDAO.clear();
            ArrayList<Game> list = gameDAO.FindAll();
            Assertions.assertTrue(list.isEmpty());
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("GameDAO FindAllFail threw an exception");
        }
    }

    @Test
    @DisplayName("Clear success")
    public void clearSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            gameDAO.Insert(123, new Game(), "FirstGame");
            gameDAO.Insert(456, new Game(), "SecondGame");
            gameDAO.clear();
            Assertions.assertEquals(null, gameDAO.Find(123));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("GameDAO Clear threw an exception");
        }
    }

    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.Execute();
    }
}
