package services;

import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.UserDAO;
import Results.ClearResult;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A service to handle the logic for the clear operation.
 */
public class ClearService {
    /**
     * Clears everything in the database (Authtokens, Games, and Users).
     * @return A ClearResponse object containing the results of the clear operation.
     */
    public ClearResult Execute() {
        ClearResult clearResult = new ClearResult();
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            GameDAO gameDAO = new GameDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            authtokenDAO.clear();
            gameDAO.clear();
            userDAO.clear();
            clearResult.setSuccess(true);
            clearResult.setMessage("Clear succeeded.");
        }
        catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            clearResult.setSuccess(false);
            clearResult.setMessage("Error occured while trying to clear the database.");
        }
        return clearResult;
    }
}
