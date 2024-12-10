package clientcomm;

import chess.ChessGame;
import chess.ChessMove;
import models.Game;
import ui.ChessboardUI;
import websocket.commands.UserGameCommand;

import java.util.Collection;

public interface GameHandler {
    void updateGame(Game game, Collection<ChessMove> possibleMoves);
    void updateMessage(String message);
}
