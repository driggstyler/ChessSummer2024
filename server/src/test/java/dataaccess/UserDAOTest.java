package dataaccess;

import models.User;
import services.ClearService;
import dataaccess.dao.UserDAO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDAOTest {
    @BeforeEach
    public void setup() {

    }
    @Test
    @DisplayName("Insert successful")
    public void insertSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            userDAO.insert(new User("John", "Johnson", "JJ@gmail.com"));
            Assertions.assertNotEquals(null, userDAO.find("John"));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("UserDAO Insert threw an exception");
        }
    }

    @Test
    @DisplayName("Insert failed")
    public void insertFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            userDAO.insert(new User("John", "Johnson", "JJ@gmail.com"));
            userDAO.remove("John");
            Assertions.assertEquals(null, userDAO.find("John"));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("UserDAO InsertFail threw an exception");
        }
    }

    @Test
    @DisplayName("Find successful")
    public void findSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            User user = new User("John", "Johnson", "JJ@gmail.com");
            userDAO.insert(user);
            Assertions.assertNotEquals(null, userDAO.find("John"));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("UserDAO Find threw an exception");
        }
    }

    @Test
    @DisplayName("Find failed")
    public void findFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            Assertions.assertEquals(null, userDAO.find("John"));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("UserDAO FindFail threw an exception");
        }
    }

    @Test
    @DisplayName("Remove successful")
    public void removeSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            userDAO.insert(new User("Joe", "Swanson", "JS@gmail.com"));
            userDAO.remove("Joe");
            Assertions.assertEquals(null, userDAO.find("John"));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("UserDAO Remove threw an exception");
        }
    }

    @Test
    @DisplayName("Remove failed")
    public void removeFail() {
        try (Connection conn = DatabaseManager.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            userDAO.insert(new User("John", "Johnson", "JJ@gmail.com"));
            userDAO.remove("John");
            userDAO.insert(new User("John", "Johnson", "JJ@gmail.com"));
            Assertions.assertNotEquals(null, userDAO.find("John"));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("UserDAO RemoveFail threw an exception");
        }
    }

    @Test
    @DisplayName("Clear success")
    public void clearSuccess() {
        try (Connection conn = DatabaseManager.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            userDAO.insert(new User("John", "Johnson", "JJ@gmail.com"));
            userDAO.insert(new User("Steve", "Rogers", "flag@gmail.com"));
            userDAO.clear();
            Assertions.assertEquals(null, userDAO.find("John"));
        }
        catch (DataAccessException | SQLException e) {
            System.out.println("UserDAO Clear threw an exception");
        }
    }

    @AfterEach
    public void tearDown() {
        ClearService clearService = new ClearService();
        clearService.execute();
    }
}
