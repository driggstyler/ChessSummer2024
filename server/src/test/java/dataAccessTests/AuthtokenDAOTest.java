package dataAccessTests;

import Models.Authtoken;
import Services.ClearService;
import dataaccess.DAO.AuthtokenDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthtokenDAOTest {
    @BeforeEach
    public void setup() {

    }
    @Test
    @DisplayName("Insert successful")
    public void insertSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            authtokenDAO.Insert(new Authtoken("test123", "tester1"));
            Assertions.assertNotEquals(authtokenDAO.Find("test123"), null);
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("AuthtokenDAO Insert threw an exception");
        }
    }

    @Test
    @DisplayName("Insert failed")
    public void insertFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            authtokenDAO.Insert(new Authtoken("test123", "tester1"));
            authtokenDAO.Remove("test123");
            Assertions.assertEquals(authtokenDAO.Find("test123"), null);
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("AuthtokenDAO InsertFail threw an exception");
        }
    }

    @Test
    @DisplayName("Find successful")
    public void findSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            Authtoken authtoken = new Authtoken("test123", "tester1");
            authtokenDAO.Insert(authtoken);
            Assertions.assertNotEquals(authtokenDAO.Find("test123"), null);
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("AuthtokenDAO Find threw an exception");
        }
    }

    @Test
    @DisplayName("Find failed")
    public void findFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            Assertions.assertEquals(authtokenDAO.Find("test321"), null);
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("AuthtokenDAO FindFail threw an exception");
        }
    }

    @Test
    @DisplayName("Remove successful")
    public void removeSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            authtokenDAO.Insert(new Authtoken("test123", "tester1"));
            authtokenDAO.Remove("test123");
            Assertions.assertEquals(authtokenDAO.Find("test123"), null);
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("AuthtokenDAO Remove threw an exception");
        }
    }

    @Test
    @DisplayName("Remove failed")
    public void removeFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            authtokenDAO.Insert(new Authtoken("test123", "tester1"));
            authtokenDAO.Remove("test123");
            authtokenDAO.Insert(new Authtoken("test123", "tester1"));
            Assertions.assertNotEquals(authtokenDAO.Find("test123"), null);
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("AuthtokenDAO RemoveFail threw an exception");
        }
    }

    @Test
    @DisplayName("Clear success")
    public void clearSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            authtokenDAO.Insert(new Authtoken("test123", "tester1"));
            authtokenDAO.Insert(new Authtoken("test1234", "tester2"));
            authtokenDAO.clear();
            Assertions.assertEquals(authtokenDAO.Find("test123"), null);
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("AuthtokenDAO Clear threw an exception");
        }
    }

    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.Execute();
    }
}

