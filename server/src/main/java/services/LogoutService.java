package services;

import dataaccess.dao.AuthtokenDAO;
import results.LogoutResult;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A service to handle the logic for the logout operation.
 */
public class LogoutService {
    /**
     * Logs the user out of the server.
     * @param authtoken The authtoken of the signed-in user.
     * @return A LogoutResult object containing the results of the logout operation.
     */
    public LogoutResult Execute(String authtoken){
        LogoutResult logoutResult = new LogoutResult();
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            if (authtokenDAO.Find(authtoken) == null) {
                logoutResult.setSuccess(false);
                logoutResult.setMessage("Error: Unauthorized");
                return logoutResult;
            }
            authtokenDAO.Remove(authtoken);
            logoutResult.setSuccess(true);
            logoutResult.setMessage("Logged out successfully.");
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            logoutResult.setSuccess(false);
            logoutResult.setMessage("Error in logging out.");
        }
        return logoutResult;
    }
}
