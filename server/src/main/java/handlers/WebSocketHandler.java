package handlers;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.dao.AuthtokenDAO;
import dataaccess.dao.GameDAO;
import models.Authtoken;
import models.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
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
            case CONNECT:
                connect(session, userGameCommand);
                break;
            case MAKE_MOVE:
                // Ensure you're casting the correct type
                MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
                makeMove(session, makeMoveCommand);
                break;
            case LEAVE:
                leave(session, userGameCommand);
                break;
            case RESIGN:
                resign(session, userGameCommand);
                break;
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
    public void makeMove(Session session, MakeMoveCommand makeMoveCommand) throws IOException {
        Game game = null;
        Authtoken auth = null;
        String playerColor = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            game = gameDAO.find(makeMoveCommand.getGameID());
            auth = authtokenDAO.find(makeMoveCommand.getAuthToken());
            try {
                if (auth == null) {
                    throw new InvalidMoveException("InvalidMove: Bad authtoken.");
                }
                if (auth.getUsername().equals(game.getWhiteUsername())) {
                    playerColor = "WHITE";
                }
                else if (auth.getUsername().equals(game.getBlackUsername())) {
                    playerColor = "BLACK";
                }
                if (playerColor == null || !playerColor.equalsIgnoreCase(game.getGame().getTeamTurn().name())) {
                    throw new InvalidMoveException("It is not your turn.");
                }
                    game.getGame().makeMove(makeMoveCommand.getMove());
                    //Update game state
                    gameDAO.update(game);
            } catch (InvalidMoveException invalidMoveException) {
                System.out.println("WebsocketHandler threw an Invalid move exception from makeMove.");
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.setErrorMessage(invalidMoveException.getMessage());
                sendMessage(session, new Gson().toJson(errorMessage));
                return;
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
        broadcastMessage(game.getGameID(), responseMessage, session);
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notificationMessage.setMessage("Move successful.");
        broadcastMessage(game.getGameID(), new Gson().toJson(notificationMessage), session);
    }
    public void leave(Session session, UserGameCommand userGameCommand) throws IOException {
        //update that the spot open
        webSocketSessions.removeSession(userGameCommand.getGameID(), session);
        Game game = null;
        Authtoken auth = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            game = gameDAO.find(userGameCommand.getGameID());
            auth = authtokenDAO.find(userGameCommand.getAuthToken());
            //open spot
            if (auth.getUsername().equals(game.getWhiteUsername())) {
                game.setWhiteUsername(null);
            }
            else if (auth.getUsername().equals(game.getBlackUsername())) {
                game.setBlackUsername(null);
            }
            gameDAO.update(game);
        } catch (DataAccessException | SQLException e) {
            System.out.println("Exception thrown in leave() in WebsocketHandler");
        }
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        String responseMessage = new Gson().toJson(notificationMessage);
        sendMessage(session, responseMessage);
        notificationMessage.setMessage(auth.getUsername() + " left the game.");
        broadcastMessage(game.getGameID(), new Gson().toJson(notificationMessage), session); //Needs to be NOTIFICATION class message
    }
    public void resign(Session session, UserGameCommand userGameCommand) throws IOException {
        // Game set to over, but you can still hang out

        //Set the gameOver value in Chessgame to true
        //Send messages client and others
        Game game = null;
        Authtoken auth = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            GameDAO gameDAO = new GameDAO(conn);
            AuthtokenDAO authtokenDAO = new AuthtokenDAO(conn);
            game = gameDAO.find(userGameCommand.getGameID());
            auth = authtokenDAO.find(userGameCommand.getAuthToken());
            if (game.getGame().isGameOVer()) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.setErrorMessage("The game is already over, you can't resign.");
            }
            if (auth.getUsername() == null) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.setErrorMessage("You are observing, you can't resign.");
            }
            game.getGame().setGameOver(true);
            gameDAO.update(game);
        } catch (DataAccessException | SQLException e) {

        }
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notificationMessage.setMessage("You have resigned.");
        String responseMessage = new Gson().toJson(notificationMessage);
        sendMessage(session, responseMessage);
        notificationMessage.setMessage(auth.getUsername() + " resigned.");
        broadcastMessage(game.getGameID(), new Gson().toJson(notificationMessage), session);
    }
    public void sendMessage(Session session, String jsonResponse) throws IOException {
        //Sends the message/game to root client
        if (session != null && session.isOpen()) {
            try {
                session.getRemote().sendString(jsonResponse);
            } catch (Exception e) {
                System.out.println("Exception thrown from sendMessage in WebSocketHandler.");
            }
        } else if (!session.isOpen()){
            System.out.println("Session close, unable to sendMessage.");
        }
        else {
            System.out.println("Session is null, unable to sendMessage.");
        }
    }
    public void broadcastMessage(int gameID, String message, Session exceptThisSession) throws IOException {
        System.out.println("Brakpont");
        for (Session session : webSocketSessions.getSessions().get(gameID)) {
            if (session == exceptThisSession) {
                continue;
            }
            sendMessage(session, message);
        }

        //        for (Session session : webSocketSessions.getSessions().get(gameID)) {
//            if (session.hashCode() != exceptThisSession.hashCode()) {
//                if (session != null && session.isOpen()) {
//                    try {
//                        session.getRemote().sendString(message);
//                    } catch (IOException e) {
//                        System.out.println("Exception thrown in broadcastMessage in WebSocketHandler");
//                    }
//                } else if (!session.isOpen()){
//                    System.out.println("Session is closed, can't broadcastMessage.");
//                }
//                else {
//                    System.out.println("Session is null, can't broadcastMessage.");
//                }
//            }
//        }
    }

//    @OnWebSocketClose
//    public void onClose(Session session, int statusCode, String reason) {
//        // Remove the session from the session list
//        webSocketSessions.removeSession(session); // Remove the session when it's closed
//
//        // Log the reason for the closure (optional)
//        System.out.println("Session closed. Status Code: " + statusCode + ", Reason: " + reason);
//    }
}
