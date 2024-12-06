package services;

import chess.ChessGame;
import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import models.Game;
import requests.CreateGameRequest;
import results.CreateGameResult;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A service to handle the logic for the create game operation.
 */
public class CreateGameService {
    /**
     * Creates a new game in the database.
     * @param createGameRequest Containing the information needed to create the new game.
     * @param authtoken The authtoken of the signed-in user.
     * @return A CreateGameResult the contains the results of the createGame operation.
     */
    public CreateGameResult execute(CreateGameRequest createGameRequest, String authtoken) {
        CreateGameResult createGameResult = new CreateGameResult();
        try (Connection conn = DatabaseManager.getConnection()){
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            GameDAO gameDAO = new GameDAO(conn);
            if (authtokenDAO.find(authtoken) == null) {
                createGameResult.setSuccess(false);
                createGameResult.setMessage("Error: Unauthorized");
                return createGameResult;
            }
            if (createGameRequest.getGameName() == null) {
                createGameResult.setSuccess(false);
                createGameResult.setMessage("Error: Bad request.");
                return createGameResult;
            }

            int gameID;
            if (gameDAO.findAll() != null) {
                gameID = gameDAO.findAll().size() + 1;
            }
            else {
                gameID = 1;
            }
            Game game = new Game();
            game.setGameName(createGameRequest.getGameName());
            game.setGame(new ChessGame());
            gameDAO.insert(gameID, game, createGameRequest.getGameName());
            createGameResult.setGameID(gameID);
            createGameResult.setSuccess(true);
            createGameResult.setMessage("Successfully created a new game.");
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            createGameResult.setGameID(null);
            createGameResult.setSuccess(false);
            createGameResult.setMessage("Error in creating a new game.");
        }
        return createGameResult;
    }
}
