package clientcomm;

import com.google.gson.Gson;
import handlers.WebSocketSessions;
import models.Game;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadService;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@WebSocket
public class WebSocketFacade extends Endpoint {
    private int port;
    private GameHandler gameHandler;
    public Session session;

    public WebSocketFacade(int port, GameHandler gameHandler) {
        this.port = port;
        this.gameHandler = gameHandler;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            this.session = container.connectToServer(this, new URI("ws://localhost" + port + "/ws"));
        } catch (Exception e) {
            System.out.println("Error thrown in Websocket facade");
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) throws IOException {
        Gson gson = new Gson();
        ServerMessage serverMessage =gson.fromJson(message, ServerMessage.class);
        ServerMessage.ServerMessageType serverMessageType = serverMessage.getServerMessageType();
        switch (serverMessageType) {
            case LOAD_GAME ->  loadGame(((LoadService)serverMessage).getGame());
            case ERROR -> error(((ErrorMessage)serverMessage).getErrorMessage());
            case NOTIFICATION -> notification(((NotificationMessage)serverMessage).getMessage());
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
}
