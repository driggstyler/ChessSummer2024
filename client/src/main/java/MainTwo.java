import chess.*;
import clientcomm.PreLogin;

public class MainTwo {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        //Run looping pre and post screens until quit
        PreLogin preLogin = new PreLogin(Integer.parseInt(args[0]));
        preLogin.run();
    }
}