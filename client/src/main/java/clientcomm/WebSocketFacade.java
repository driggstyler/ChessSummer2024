package clientcomm;

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

public class WebSocketFacade extends Endpoint implements MessageHandler.Whole<String> {
    private int port;
    private GameHandler gameHandler;
    public Session session;

    public WebSocketFacade(int port, GameHandler gameHandler) {
        this.port = port;
        this.gameHandler = gameHandler;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            this.session = container.connectToServer(this, new URI("ws://localhost:" + port + "/ws"));
            session.addMessageHandler(this);
        } catch (Exception e) {
            System.out.println("Error thrown in Websocket facade");
        }
    }

    public void onMessage(String message) {
        try {
            Gson gson = new Gson();
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            ServerMessage.ServerMessageType serverMessageType = serverMessage.getServerMessageType();
            switch (serverMessageType) {
                case LOAD_GAME -> loadGame(((LoadService) serverMessage).getGame());
                case ERROR -> error(((ErrorMessage) serverMessage).getErrorMessage());
                case NOTIFICATION -> notification(((NotificationMessage) serverMessage).getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error with onMessage in Websocket facade.");
        }
    }
    public void loadGame(Game game) {
        gameHandler.updateGame(game, game.getGame().getTeamTurn(), null);
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

    //Send stuff
}
