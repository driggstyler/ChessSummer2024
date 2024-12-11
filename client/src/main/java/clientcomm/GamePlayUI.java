package clientcomm;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import models.Game;
import org.glassfish.grizzly.http.server.Session;
import ui.ChessboardUI;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

public class GamePlayUI implements GameHandler {
    private ServerFacade serverFacade;
    private WebSocketFacade webSocketFacade;
    private Game game;
    private GameHandler gameHandler;
    private String playerColor;
    public void run(int port, String authtoken, int gameID, Game game, Scanner scanner, String playerColor) {
        this.game = game;
        Session session;
        this.playerColor = playerColor;
        //Convert usercommand to json
        webSocketFacade = new WebSocketFacade(port, this);
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authtoken, gameID);
        try {
            webSocketFacade.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e) {
            System.out.println("Error thrown while sending CONNECT at top of GamePlayUI");
        }
        System.out.println("Successfully joined game! Type help to see commands");
        while(true) {
            String command = scanner.nextLine();
            if (command.equals("help")) {
                System.out.println("""
                        Options:
                        help - Shows the commands to play the game.
                        redraw - Redraws the current chessboard.
                        leave - Leave the game.
                        make move - Make a move (do this only on your turn).
                        resign - Resign and forfeit the match (give up).
                        highlight - highlight the available moves of a given piece.""");
            }
            else if (command.equals("redraw")) {
                redrawCommand(port, authtoken, gameID, this.game);
            }
            else if (command.equals("leave")) {
                leaveCommand(port, authtoken, gameID);
                break;
            }
            else if (command.equals("make move")) {
                makeMoveCommand(port, authtoken, gameID, this.game, scanner);
            }
            else if (command.equals("resign")) {
                resignCommand(port, authtoken, gameID);
            }
            else if (command.equals("highlight")) {
                highlightMovesCommand(port, authtoken, gameID, this.game, scanner);
            }
            else {
                System.out.println("Please enter valid command.");
            }
        }
    }

    public void redrawCommand(int port, String authtoken, int gameID, Game game) {
        //Only redraw board
        //TODO: Only print your perspective
        //TODO: Observe ONLY IN WHITE
        ChessGame.TeamColor teamColor = null;
        if (playerColor == null) {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        else if (playerColor.equals("WHITE")) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else if (playerColor.equals("BLACK")) {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        Collection<ChessMove> noMoves = new HashSet<>();
        ChessboardUI.run(game.getGame().getBoard(), teamColor, noMoves);
    }

    public void leaveCommand(int port, String authtoken, int gameID) {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authtoken, gameID);

        try {
            webSocketFacade.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
            webSocketFacade.session.close();
            System.out.println("You left the game.");
        } catch (Exception e) {
            System.out.println("Error thrown in leaveCommand");
        }
    }

    public void makeMoveCommand(int port, String authtoken, int gameID, Game game, Scanner scanner) {
        if (game.getGame().isGameOVer()) {
            //Return, you can't do it
            System.out.println("The game is over, no more moves can be made.");
            return;
        }

        while(true) {
            System.out.println("""
                                Please indicate where you want to move the piece by typing the start position, 
                                then the end position. i.e. "a1 a2". If you want to go back, type 0.
                                """);
            String input = scanner.nextLine();
            if (input.equals("0")) {
                break;
            }
            //String input = scanner.nextLine(); // a1 a2
            //make sure is size 5 and input is valid
            if (input.length() != 5) {
                continue;
            }
            char potentialI = input.charAt(0);
            int startI = 0;
            char potentialJ = input.charAt(3);
            int endI = 0;
            boolean inputReal = true;
            switch (potentialI) {
                case 'a' -> startI = 0;
                case 'b' -> startI = 1;
                case 'c' -> startI = 2;
                case 'd' -> startI = 3;
                case 'e' -> startI = 4;
                case 'f' -> startI = 5;
                case 'g' -> startI = 6;
                case 'h' -> startI = 7;
                default -> inputReal = false;
            }
            switch (potentialJ) {
                case 'a' -> endI = 0;
                case 'b' -> endI = 1;
                case 'c' -> endI = 2;
                case 'd' -> endI = 3;
                case 'e' -> endI = 4;
                case 'f' -> endI = 5;
                case 'g' -> endI = 6;
                case 'h' -> endI = 7;
                default -> inputReal = false;
            }
            if (!inputReal) {
                System.out.println("Invalid input.");
                continue;
            }
            int startJ = ((int)input.charAt(1) - '0') - 1;
            int endJ = ((int)input.charAt(4) - '0') - 1;
            //check if it is a real position and parse input
            ChessPiece.PieceType promotionPiece = null;
            while(true) {System.out.println("""
                    If this is a pawn being promoted, type one of these promotions:
                    queen
                    bishop
                    knight
                    rook
                    (if not a promotion, just type 0)""");
                String promotion = scanner.nextLine();
                boolean badInput = false;
                switch (promotion) {
                    case "queen" -> promotionPiece = ChessPiece.PieceType.QUEEN;
                    case "bishop" -> promotionPiece = ChessPiece.PieceType.BISHOP;
                    case "knight" -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                    case "rook" -> promotionPiece = ChessPiece.PieceType.ROOK;
                    case "0" -> promotionPiece = null;
                    default -> badInput = true;
                }
                if (!badInput) {
                    break;
                }
                System.out.println("Please enter a valid input.");
            }
            ChessPosition startPosition = new ChessPosition(startJ + 1, startI + 1);
            ChessPosition endPosition = new ChessPosition(endJ + 1, endI + 1);
            ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
            if (game.getGame().validMoves(startPosition).contains(move)) {
                MakeMoveCommand makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authtoken, gameID, move);
                try {
                    webSocketFacade.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
                    //System.out.println("You move you piece " + startPosition + " to " + endPosition);
                } catch (Exception e) {
                    System.out.println("Error thrown in makeMoveCommand");
                }
                break;
            } else {
                System.out.println("That move is not allowed for that piece.");
            }

        }

    }

    public void resignCommand(int port, String authtoken, int gameID) {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authtoken, gameID);
        try {
            webSocketFacade.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e) {
            System.out.println("Error thrown in resignCommand");
        }
    }

    public void highlightMovesCommand(int port, String authtoken, int gameID, Game game, Scanner scanner) {
        //Scannner String for valid moves on a piece, reprint the baord with existing boars, don't need to call server
        while(true) {
            System.out.println("""
                    Please indicate what piece you want to highlight possible moves for by typing the piece's position. 
                    i.e. "a1". If you want to go back, type 0.
                    """);
            String input = scanner.nextLine();
            if (input.equals("0")) {
                System.out.println("Ok, what do you want to do instead?");
                break;
            }
            //String input = scanner.nextLine(); // a1 a2
            //make sure is size 5 and input is valid
            if (input.length() != 2) {
                System.out.println("Please enter a valid input");
                continue;
            }
            char potentialI = input.charAt(0);
            int positionI = 0;
            int positionJ = ((int)input.charAt(1) - '0');
            boolean inputReal = true;
            switch (potentialI) {
                case 'a' -> positionI = 1;
                case 'b' -> positionI = 2;
                case 'c' -> positionI = 3;
                case 'd' -> positionI = 4;
                case 'e' -> positionI = 5;
                case 'f' -> positionI = 6;
                case 'g' -> positionI = 7;
                case 'h' -> positionI = 8;
                default -> inputReal = false;
            }
            if (!inputReal) {
                System.out.println("Invalid input.");
                continue;
            }
            //Switched around I and J because it reads row, then column, while "a1" reads column, then row.
            ChessPosition chessPosition = new ChessPosition(positionJ, positionI);
            if (game.getGame().getBoard().getPiece(chessPosition) == null) {
                System.out.println("There is no piece there. Please enter a valid input");
                continue;
            }
            Collection<ChessMove> possibleMoves = game.getGame().validMoves(chessPosition);
            updateGame(game, possibleMoves);
            break;
        }
    }

    @Override
    public void updateGame(Game game, Collection<ChessMove> possibleMoves) {
        //print current board
        this.game = game;
        if (game != null) {
            System.out.println("GAME");
        }
        if (playerColor == null) {
            ChessboardUI.run(game.getGame().getBoard(), ChessGame.TeamColor.WHITE, possibleMoves);
        }
        else if (playerColor.equals("WHITE")){
            ChessboardUI.run(game.getGame().getBoard(), ChessGame.TeamColor.WHITE, possibleMoves);
        }
        else {
            ChessboardUI.run(game.getGame().getBoard(), ChessGame.TeamColor.BLACK, possibleMoves);
        }
    }

    @Override
    public void updateMessage(String message) {
        System.out.println(message);
    }
}
