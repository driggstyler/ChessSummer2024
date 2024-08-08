package dataaccess.dao;

import models.User;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A Data Access Object to interact with the users in the database.
 */
public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
        try {
            DatabaseManager.createDatabase();
            var createUserTable = """
                    CREATE TABLE IF NOT EXISTS `user` (
                       `password` VARCHAR(225) NOT NULL,
                       `username` VARCHAR(225) NOT NULL,
                       `email` VARCHAR(225) NOT NULL);
                    """;
            try (var createTableStatement = conn.prepareStatement(createUserTable)) {
                createTableStatement.executeUpdate();
            }
        }
        catch (DataAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new user into the database.
     * @param user The user object.
     * @throws DataAccessException
     */
    public void insert(User user) throws DataAccessException {
        String sql = "INSERT INTO user (username, password, email) VALUES(?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, storeUserPassword(user.getPassword()));
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a user into the database.");
        }
    }

    /**
     * Finds a particular user in the database.
     * @param username Username of the user to find.
     * @return The user with the corresponding username, or null if not in the database.
     * @throws DataAccessException
     */
    public User find(String username) throws DataAccessException {
        User user;
        ResultSet rs;
        String sql = "SELECT * FROM user WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"),
                        rs.getString("email"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a user in the database");
        }
    }

    /**
     * Removes a particular user from the database.
     * @param username Username of the user to remove.
     * @throws DataAccessException
     */
    public void remove(String username) throws DataAccessException {
        String sql = "DELETE FROM user WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while deleting a user.");
        }
    }

    /**
     * Clear all users from the database.
     * @throws DataAccessException
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM user";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the user table.");
        }
    }
    String storeUserPassword(String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashedPassword;
    }
    public boolean verifyUser(String hashedPassword, String providedClearTextPassword) {
        // read the previously hashed password from the database
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

}
