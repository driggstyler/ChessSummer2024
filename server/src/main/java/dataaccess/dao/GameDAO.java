package dataaccess.dao;

import models.Game;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A Data Access Object to interact with the games in the database.
 */
public class GameDAO {
    private final Connection conn;

    public GameDAO(Connection conn) {
        this.conn = conn;
        try {
            DatabaseManager.createDatabase();
            var createGameTable = """
                    CREATE TABLE IF NOT EXISTS `game` (
                         `gameID` INT NOT NULL,
                         `gameName` VARCHAR(225) NOT NULL,
                         `game` JSON NULL,
                         PRIMARY KEY (`gameID`),
                         UNIQUE INDEX `authtoken_UNIQUE` (`gameID` ASC) VISIBLE);
                    """;
            try (var createTableStatement = conn.prepareStatement(createGameTable)) {
                createTableStatement.executeUpdate();
            }
        }
        catch (DataAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Claims the desired white or black position on the board for the user (Does not affect spectators).
     * @param gameID GameID of the desired game.
     * @param teamColor The teamColor the user wishes to claim.
     * @param username The user's username to put into the game.
     * @return True if the spot was claimed, false if the spot was not able to be claimed.
     * @throws DataAccessException
     */
    public boolean claimSpot(int gameID, String teamColor, String username) throws DataAccessException {
        Game game = find(gameID);
        if (game == null) {
            return false;
        }
        if (Objects.equals(teamColor, "WHITE") && game.getWhiteUsername() == null) {
            game.setWhiteUsername(username);
            Gson gson = new Gson();
            String json = gson.toJson(game);
            String sql = "UPDATE game SET game = ? WHERE gameID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, json);
                stmt.setInt(2, gameID);
                stmt.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
                throw new DataAccessException("Error encountered while inserting an authtoken into the database");
            }
            return true;
        }
        else if (Objects.equals(teamColor, "BLACK")  && game.getBlackUsername() == null) {
            game.setBlackUsername(username);
            Gson gson = new Gson();
            String json = gson.toJson(game);
            String sql = "UPDATE game SET game = ? WHERE gameID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, json);
                stmt.setInt(2, gameID);
                stmt.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
                throw new DataAccessException("Error encountered while inserting an authtoken into the database");
            }
            return true;
        }
        return false;
    }

    /**
     * Inserts a new game into the databse.
     * @param gameID GameID of the new game to insert.
     * @param game The new game to insert
     * @param gameName The name of the new game to insert.
     * @throws DataAccessException
     */
    public void insert(int gameID, Game game, String gameName) throws DataAccessException {
        game.setGameName(gameName);
        game.setGameID(gameID);
        String sql = "INSERT INTO game (gameID, gameName, game) VALUES(?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Gson gson = new Gson();
            String json = gson.toJson(game);
            stmt.setInt(1, gameID);
            stmt.setString(2, gameName);
            stmt.setString(3, json);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting an authtoken into the database");
        }
    }

    /**
     * Finds a specific game.
     * @param gameID GameID of the game to find
     * @return The game corresponding to the given gameID, or null if the game is not in the database.
     * @throws DataAccessException
     */
    public Game find(int gameID) throws DataAccessException{
        ResultSet rs;
        String sql = "SELECT * FROM game WHERE gameID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String json = rs.getString("game");
                Gson gson = new Gson();
                return gson.fromJson(json, Game.class);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an game in the database.");
        }
    }

    /**
     * Finds all the games in the database.
     * @return All games int the database in an ArrayList.
     * @throws DataAccessException
     */
    public ArrayList<Game> findAll() throws DataAccessException{
        ArrayList<Game> games = new ArrayList<>();
        ResultSet rs;
        String sql = "SELECT game FROM game;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            rs = stmt.executeQuery();
            int i = 0;
            while (rs.next()) {
                System.out.println(i++);
                String json = rs.getString("game");
                Game game = new Gson().fromJson(json, Game.class);
                games.add(game);
            }
            if (games.isEmpty()) {
                return games;
            }
            return games;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an game in the database.");
        }


    }

    /**
     * Clears all games in the database.
     * @throws DataAccessException
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM game";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the games table.");
        }
    }

    public void remove(int gameID) throws DataAccessException {
        String sql = "DELETE FROM game WHERE gameID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while deleting a game from the database");
        }
    }

    public void update(Game game) throws DataAccessException {
        remove(game.getGameID());
        insert(game.getGameID(), game, game.getGameName());
    }
}
