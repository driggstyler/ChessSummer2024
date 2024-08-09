package dataaccess;

import models.Authtoken;
import services.ClearService;
import dataaccess.dao.AuthtokenDAO;
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
            authtokenDAO.insert(new Authtoken("test123", "tester1"));
            Assertions.assertNotEquals(authtokenDAO.find("test123"), null);
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
            authtokenDAO.insert(new Authtoken("test456", "tester2"));
            authtokenDAO.remove("test456");
            Assertions.assertEquals(authtokenDAO.find("test456"), null);
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
            authtokenDAO.insert(authtoken);
            Assertions.assertNotEquals(authtokenDAO.find("test123"), null);
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
            Assertions.assertEquals(authtokenDAO.find("test321"), null);
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
            authtokenDAO.insert(new Authtoken("test123", "tester1"));
            authtokenDAO.remove("test123");
            Assertions.assertEquals(authtokenDAO.find("test123"), null);
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
            authtokenDAO.insert(new Authtoken("test123", "tester1"));
            authtokenDAO.remove("test123");
            authtokenDAO.insert(new Authtoken("test123", "tester1"));
            Assertions.assertNotEquals(authtokenDAO.find("test123"), null);
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
            authtokenDAO.insert(new Authtoken("test123", "tester1"));
            authtokenDAO.insert(new Authtoken("test1234", "tester2"));
            authtokenDAO.clear();
            Assertions.assertEquals(authtokenDAO.find("test123"), null);
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("AuthtokenDAO Clear threw an exception");
        }
    }

    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.execute();
    }
}

