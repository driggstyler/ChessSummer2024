import chess.*;
import clientComm.PreLogin;

public class MainTwo {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        //Run looping pre and post screens until quit
        PreLogin preLogin = new PreLogin(Integer.parseInt(args[0]));
        preLogin.run();
    }
}


//PreLogin class
//PostLogin class
//ServerFacade class
//Chessboard UI
    //main
    //helper - print top/bottoms rows/column of letters, squares, and letters
//
