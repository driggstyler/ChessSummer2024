package dataaccess.dao;

import Models.Authtoken;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A Data Access Object to interact with the authtokens in the database.
 */
public class AuthtokenDAO {
    private final Connection conn;

    public AuthtokenDAO(Connection conn) {
        this.conn = conn;
        try {
            DatabaseManager.createDatabase();
            var createAuthtokenTable = """
                    CREATE TABLE IF NOT EXISTS `authentication` (
                         `authtoken` VARCHAR(225) NOT NULL,
                         `username` VARCHAR(225) NOT NULL,
                         PRIMARY KEY (`authtoken`),
                         UNIQUE INDEX `authtoken_UNIQUE` (`authtoken` ASC) VISIBLE);
                    """;
            try (var createTableStatement = conn.prepareStatement(createAuthtokenTable)) {
                createTableStatement.executeUpdate();
            }
        }
        catch (DataAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a given authtoken object into the database.
     * @param authtoken The authtoken object to insert.
     * @throws DataAccessException
     */
    public void Insert(Authtoken authtoken) throws DataAccessException {
        String sql = "INSERT INTO authentication (authtoken, username) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authtoken.getAuthtoken());
            stmt.setString(2, authtoken.getUsername());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting an authtoken into the database");
        }
    }

    /**
     * Find an authtoken object matching the given authtoken string.
     * @param authtokenString The string of the authtoken to find.
     * @return The authoken object with the string matching the given authtoken string, or null if it's not in the database.
     * @throws DataAccessException
     */
    public Authtoken Find(String authtokenString) throws DataAccessException {
        Authtoken aToken;
        ResultSet rs;
        String sql = "SELECT * FROM authentication WHERE authtoken = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authtokenString);
            rs = stmt.executeQuery();
            if (rs.next()) {
                aToken = new Authtoken(rs.getString("authtoken"), rs.getString("username"));
                return aToken;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an authtoken int the database.");
        }
    }

    /**
     * Remove an authroken object matching the given authtoken string.
     * @param authtokenString The string of the authtoken to find.
     * @throws DataAccessException
     */
    public void Remove(String authtokenString) throws DataAccessException {
        String sql = "DELETE FROM authentication WHERE authtoken = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authtokenString);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while deleting an authtoken.");
        }
    }

    /**
     * Clear the database of all authtoken objects.
     * @throws DataAccessException
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM authentication";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the authtoken table.");
        }
    }
}
