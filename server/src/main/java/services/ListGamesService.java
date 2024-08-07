package services;

import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import models.Game;
import results.ListGamesResult;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A service to handle the logic for the list games operation.
 */
public class ListGamesService {
    /**
     * Lists all the games in the database.
     * @param authtoken The authtoken of the signed-in user.
     * @return A ListGamesResult object containing the results of the listGames operation.
     */
    public ListGamesResult execute(String authtoken){
        ListGamesResult listGamesResult = new ListGamesResult();
        try (Connection conn = DatabaseManager.getConnection()){
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            GameDAO gameDAO = new GameDAO(conn);
            if (authtokenDAO.find(authtoken) == null) {
                listGamesResult.setSuccess(false);
                listGamesResult.setMessage("Error: Unauthorized");
                return listGamesResult;
            }
            ArrayList<Game> games = gameDAO.findAll();
            listGamesResult.setGames(games);
            listGamesResult.setSuccess(true);
            listGamesResult.setMessage("Listed games successfully.");
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            listGamesResult.setMessage("Error in listing games.");
        }
        return listGamesResult;
    }
}
