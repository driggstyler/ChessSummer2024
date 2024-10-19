package websocket.messages;

import models.Game;

public class LoadService extends ServerMessage {
    public LoadService(ServerMessageType type) {
        super(type);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private Game game;
    
}
