package services;

import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import Requests.JoinGameRequest;
import Results.JoinGameResult;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A service to handle the logic for the join game operation.
 */
public class JoinGameService {
    /**
     * Makes a user join a game.
     * @param joinGameRequest The information required to join a game.
     * @param authtoken The authoke of the signed-in user.
     * @return A JoinGameResult object containing the results of the joinGame operation.
     */
    public JoinGameResult Execute(JoinGameRequest joinGameRequest, String authtoken){
        JoinGameResult joinGameResult = new JoinGameResult();
        try (Connection conn = DatabaseManager.getConnection()) {
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            GameDAO gameDAO = new GameDAO(conn);
            if (authtokenDAO.Find(authtoken) == null) {
                joinGameResult.setSuccess(false);
                joinGameResult.setMessage("Error: Unauthorized.");
                return joinGameResult;
            }
            if (gameDAO.Find(joinGameRequest.getGameID()) == null ||
                    joinGameRequest.getPlayerColor() == null) {
                joinGameResult.setSuccess(false);
                joinGameResult.setMessage("Error: Bad request.");
                return joinGameResult;
            }
            if (joinGameRequest.getPlayerColor() != null) {
                boolean claimedSpot = gameDAO.claimSpot(joinGameRequest.getGameID(), joinGameRequest.getPlayerColor(), authtokenDAO.Find(authtoken).getUsername());
                if (!claimedSpot) {
                    joinGameResult.setSuccess(false);
                    joinGameResult.setMessage("Error: Already taken.");
                    return joinGameResult;
                }
            }
            joinGameResult.setSuccess(true);
            joinGameResult.setMessage("Joined game successfully.");
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            joinGameResult.setMessage("Error in joining game.");
        }
        return joinGameResult;
    }
}
