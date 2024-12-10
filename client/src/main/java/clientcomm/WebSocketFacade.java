package clientcomm;

import chess.ChessMove;
import com.google.gson.Gson;
import models.Game;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadService;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

public class WebSocketFacade extends Endpoint {
    private int port;
    private GameHandler gameHandler;
    public Session session;

    public WebSocketFacade(int port, GameHandler gameHandler) {
        try {
            this.port = port;
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, new URI("ws://localhost:" + port + "/ws"));
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.println(message);
                    try {
                        Gson gson = new Gson();
                        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                        ServerMessage.ServerMessageType serverMessageType = serverMessage.getServerMessageType();
                        switch (serverMessageType) {
                            case LOAD_GAME ->
                            {
                                LoadService loadService = gson.fromJson(message, LoadService.class);
                                loadGame(loadService.getGame());
                            }
                            case ERROR ->
                            {
                                ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                                error(errorMessage.getErrorMessage());
                            }
                            case NOTIFICATION -> {
                                NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                                notification(notificationMessage.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error with onMessage in Websocket facade.");
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Error thrown in Websocket facade");
        }
    }


    public void loadGame(Game game) {
        gameHandler.updateGame(game, new HashSet<ChessMove>());
    }
    public void error(String message){
        gameHandler.updateMessage(message);
    }
    public void notification(String message){
        gameHandler.updateMessage(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

//    public void playGame(){
//        try {
//            this.session.getBasicRemote().sendText();
//        } catch (IOException e) {
//            System.out.println("Error occurred in plaGame in WebsocketFacade.");
//        }
//    }
//
//    public void makeMove(){
//        try {
//            this.session.getBasicRemote().sendText();
//        } catch (IOException e) {
//            System.out.println("Error occurred in plaGame in WebsocketFacade.");
//        }
//    }
//
//    public void leaveGame(){
//        try {
//            this.session.getBasicRemote().sendText();
//        } catch (IOException e) {
//            System.out.println("Error occurred in plaGame in WebsocketFacade.");
//        }
//    }
}
