package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] boardArray = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        boardArray[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public void removePiece(ChessPosition position) {
        boardArray[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardArray[position.getRow() - 1][position.getColumn() - 1];
    }

    public ChessPiece[][] getBoardArray() {
        return boardArray;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                //Reset White Pieces
                if ((i == 0 && j == 0) || (i == 0 && j == 7)) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                }
                else if ((i == 0 && j == 1) || (i == 0 && j == 6)) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                }
                else if ((i == 0 && j == 2) || (i == 0 && j == 5)) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                }
                else if (i == 0 && j == 3) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                }
                else if (i == 0 && j == 4) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                }
                else if (i == 1) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                }
                //Reset Black Pieces
                else if ((i == 7 && j == 0) || (i == 7 && j == 7)) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                }
                else if ((i == 7 && j == 1) || (i == 7 && j == 6)) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                }
                else if ((i == 7 && j == 2) || (i == 7 && j == 5)) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                }
                else if (i == 7 && j == 3) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                }
                else if (i == 7 && j == 4) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                }
                else if (i == 6) {
                    boardArray[i][j] =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                }
                else {
                    boardArray[i][j] = null;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChessBoard chessBoard = (ChessBoard) o;
        return Arrays.deepEquals(boardArray, chessBoard.getBoardArray());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(boardArray);
    }
}
