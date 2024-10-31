package handlers;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import models.Authtoken;
import models.Game;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import requests.JoinGameRequest;
import results.JoinGameResult;
import services.JoinGameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadService;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebSocket
public class WebSocketHandler {
    private WebSocketSessions webSocketSessions = new WebSocketSessions();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Gson gson = new Gson();
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);
        UserGameCommand.CommandType commandType = userGameCommand.getCommandType();
        String jsonResponse;
        switch (commandType) {
            case CONNECT ->  connect(session, userGameCommand);
            case MAKE_MOVE -> makeMove(session, userGameCommand);
            case LEAVE -> leave(session, userGameCommand);
            case RESIGN -> resign(session, userGameCommand);
        }
    }
    public void connect(Session session, UserGameCommand userGameCommand) throws IOException {
        webSocketSessions.addSession(userGameCommand.getGameID(), session);
        Game game = null;
        Authtoken auth = null;
        String whiteUsername = null;
        String blackUsername = null;
        String authUsername = null;
            try (Connection conn = DatabaseManager.getConnection()) {
                GameDAO gameDAO = new GameDAO(conn);
                AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
                game = gameDAO.find(userGameCommand.getGameID());
                auth = authtokenDAO.find(userGameCommand.getAuthToken());
                whiteUsername = game.getWhiteUsername();        // compared to authtoken username
                blackUsername = game.getBlackUsername();
                authUsername = auth.getUsername();              //Use authtoken to get username
            } catch (DataAccessException | SQLException | NullPointerException e) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.setErrorMessage("Game does not exist.");
                String responseMessage = new Gson().toJson(errorMessage);
                sendMessage(session, responseMessage);
                return;
            }
        LoadService loadService = new LoadService(ServerMessage.ServerMessageType.LOAD_GAME);
            loadService.setGame(game);
        String responseMessage = new Gson().toJson(loadService);
        sendMessage(session, responseMessage);
        //ServerMessage broadcast = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        if (authUsername.equals(whiteUsername)) {
            notificationMessage.setMessage(whiteUsername + " joined the game as white.");
        }
        else if (authUsername.equals(blackUsername)) {
            notificationMessage.setMessage(blackUsername + " joined the game as black.");
        }
        else {
            notificationMessage.setMessage(authUsername + " joined the game as an observer.");
        }
        broadcastMessage(game.getGameID(), new Gson().toJson(notificationMessage), session); //Needs to be NOTIFICATION class message
    }
    public void makeMove(Session session, UserGameCommand userGameCommand) throws IOException {
        Game game = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            game = gameDAO.find(userGameCommand.getGameID());
            try {
                game.getGame().makeMove(((MakeMoveCommand) userGameCommand).getMove());
            } catch (InvalidMoveException ignore) {
                System.out.println("WebsocketHandler threw an Invalid move exception from makeMove.");
            }
        } catch (DataAccessException | SQLException e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.setErrorMessage("Game does not exist.");
            String responseMessage = new Gson().toJson(errorMessage);
            sendMessage(session, responseMessage);
        }
        LoadService loadService = new LoadService(ServerMessage.ServerMessageType.LOAD_GAME);
        loadService.setGame(game);
        String responseMessage = new Gson().toJson(loadService);
        sendMessage(session, responseMessage);
        //ServerMessage broadcast = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        broadcastMessage(game.getGameID(), new Gson().toJson(notificationMessage), session); //Needs to be NOTIFICATION class message
    }
    public void leave(Session session, UserGameCommand userGameCommand) throws IOException {
        webSocketSessions.removeSession(session);
        Game game = null;
        Authtoken auth = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            game = gameDAO.find(userGameCommand.getGameID());
            auth = authtokenDAO.find(userGameCommand.getAuthToken());
        } catch (DataAccessException | SQLException e) {

        }
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        String responseMessage = new Gson().toJson(notificationMessage);
        sendMessage(session, responseMessage);
        //NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notificationMessage.setMessage(auth.getUsername() + " left the game.");
        broadcastMessage(game.getGameID(), new Gson().toJson(notificationMessage), session); //Needs to be NOTIFICATION class message
    }
    public void resign(Session session, UserGameCommand userGameCommand) throws IOException {
        //Set the gameOver value in Chessgame to true
        //Send messages client and others
        Game game = null;
        Authtoken auth = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            game = gameDAO.find(userGameCommand.getGameID());
            auth = authtokenDAO.find(userGameCommand.getAuthToken());
            game.getGame().setGameOVer(true);
        } catch (DataAccessException | SQLException e) {

        }
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        String responseMessage = new Gson().toJson(notificationMessage);
        sendMessage(session, responseMessage);
        //NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notificationMessage.setMessage(auth.getUsername() + " resigned.");
        broadcastMessage(game.getGameID(), new Gson().toJson(notificationMessage), session);
    }
    public void sendMessage(Session session, String jsonResponse) throws IOException {
        //Sends the message/game to root client
        session.getRemote().sendString(jsonResponse);
    }
    public void broadcastMessage(int gameID, String message, Session exceptThisSession) throws IOException {
        for (Session session : webSocketSessions.getSessions().get(gameID)) {
            if (session != exceptThisSession) {
                session.getRemote().sendString(message);
            }
        }
    }
}
