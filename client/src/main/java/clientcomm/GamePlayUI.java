package clientcomm;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import models.Game;
import ui.ChessboardUI;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

public class GamePlayUI implements GameHandler {
    private WebSocketFacade webSocketFacade;
    private Game game;
    private String playerColor = null;
    public void run(int port, String authtoken, int gameID, Scanner scanner, String playerColor) {
        //Convert usercommand to json
        webSocketFacade = new WebSocketFacade(port, this);
        System.out.println("Successfully joined game! Type help to see commands");
        while(true) {
            if (scanner.nextLine().equals("help")) {
                System.out.println("""
                        Options:
                        help - Shows the commands to play the game.
                        redraw chess board - Redraws the current chessboard.
                        leave - Leave the game.
                        make move - Make a move (do this only on your turn).
                        resign - Resign and forfeit the match (give up).
                        highlight legal moves - highlight the available moves of a given piece.""");
            }
            else if (scanner.nextLine().equals("redraw chess board")) {
                redrawCommand(port, authtoken, gameID, game);
            }
            else if (scanner.nextLine().equals("leave")) {
                leaveCommand(port, authtoken, gameID);
                break;
            }
            else if (scanner.nextLine().equals("make move")) {
                makeMoveCommand(port, authtoken, gameID, game, scanner, playerColor);
            }
            else if (scanner.nextLine().equals("resign")) {
                resignCommand(port, authtoken, gameID);
            }
            else if (scanner.nextLine().equals("highlight legal moves")) {
                highlightMovesCommand(port, authtoken, gameID, game, scanner);
            }
            else {
                System.out.println("Please enter valid command.");
            }
        }
    }

    public void redrawCommand(int port, String authtoken, int gameID, Game game) {
        //Only redraw board
        Collection<ChessMove> noMoves = new HashSet<>();
        ChessboardUI.run(game.getGame().getBoard(), game.getGame().getTeamTurn(), noMoves);
    }

    public void leaveCommand(int port, String authtoken, int gameID) {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authtoken, gameID);

        try {
            webSocketFacade.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e) {
            System.out.println("Error thrown in leaveCommand");
        }
    }

    public void makeMoveCommand(int port, String authtoken, int gameID, Game game, Scanner scanner, String playerColor) {
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
            if (scanner.nextLine().equals("0")) {
                break;
            }
            String input = scanner.nextLine(); // a1 a2
            //make sure is size 5 and input is valid
            if (input.length() != 5) {
                continue;
            }
            char potentialI = input.charAt(0);
            int startI = 0;
            char potentialJ = input.charAt(3);
            int startJ = 0;
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
                case 'a' -> startJ = 0;
                case 'b' -> startJ = 1;
                case 'c' -> startJ = 2;
                case 'd' -> startJ = 3;
                case 'e' -> startJ = 4;
                case 'f' -> startJ = 5;
                case 'g' -> startJ = 6;
                case 'h' -> startJ = 7;
                default -> inputReal = false;
            }
            if (!inputReal) {
                System.out.println("Invalid input.");
                continue;
            }
            int endI = (int)input.charAt(1);
            int endJ = (int)input.charAt(4);
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
            ChessPosition startPosition = new ChessPosition(startI, startJ);
            ChessPosition endPosition = new ChessPosition(endI, endJ);
            ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
            if (game.getGame().validMoves(startPosition).contains(move)) {
                MakeMoveCommand makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authtoken, gameID);
                makeMoveCommand.setPlayerColor(playerColor);
                try {
                    webSocketFacade.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
                    System.out.println("Piece moved from " + startPosition + " to " + endPosition);
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
            if (scanner.nextLine().equals("0")) {
                break;
            }
            String input = scanner.nextLine(); // a1 a2
            //make sure is size 5 and input is valid
            if (input.length() != 2) {
                continue;
            }
            char potentialI = input.charAt(0);
            int positionI = 0;
            int positionJ = (int)input.charAt(1);
            boolean inputReal = true;
            switch (potentialI) {
                case 'a' -> positionI = 0;
                case 'b' -> positionI = 1;
                case 'c' -> positionI = 2;
                case 'd' -> positionI = 3;
                case 'e' -> positionI = 4;
                case 'f' -> positionI = 5;
                case 'g' -> positionI = 6;
                case 'h' -> positionI = 7;
                default -> inputReal = false;
            }
            if (!inputReal) {
                System.out.println("Invalid input.");
                continue;
            }
            ChessPosition chessPosition = new ChessPosition(positionI, positionJ);
            if (game.getGame().getBoard().getPiece(chessPosition) == null) {
                System.out.println("There is no piece there.");
            }
            Collection<ChessMove> possibleMoves = game.getGame().validMoves(chessPosition);
            updateGame(game, game.getGame().getTeamTurn(), possibleMoves);
            break;
        }
    }

    @Override
    public void updateGame(Game game, ChessGame.TeamColor teamColor, Collection<ChessMove> possibleMoves) {
        //print current board
        this.game = game;
        ChessboardUI.run(game.getGame().getBoard(), teamColor, possibleMoves);
    }

    @Override
    public void updateMessage(String message) {
        System.out.println(message);
    }
}
