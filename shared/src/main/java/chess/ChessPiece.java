package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //suggested: make a switch statement for each function per pieceType
        Set<ChessMove> possibleMoves = new HashSet<>();
        ChessPiece.PieceType type = board.getPiece(myPosition).getPieceType();
        switch(type) {
            case ROOK: possibleMoves = rookPossibleMoves(possibleMoves, board, myPosition);
            break;
            case KNIGHT: possibleMoves = knightPossibleMoves(possibleMoves, board, myPosition);
            break;
            case BISHOP: possibleMoves = bishopPossibleMoves(possibleMoves, board, myPosition);
            break;
            case QUEEN: possibleMoves = queenPossibleMoves(possibleMoves, board, myPosition);
            break;
            case KING: possibleMoves = kingPossibleMoves(possibleMoves, board, myPosition);
            break;
            case PAWN: possibleMoves = pawnPossibleMoves(possibleMoves, board, myPosition);
            break;
        }
        return possibleMoves;
    }

    public Set<ChessMove> rookPossibleMoves(Set<ChessMove> possibleMoves, ChessBoard board, ChessPosition myPosition) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int l = 1; l <= 8; l++) {
                    if (i == j || (i == -1 && j == 1) || (i == 1 && j == -1)) {
                        break;
                    }
                    ChessPosition currPosition = new ChessPosition(myPosition.getRow() + (i*l), myPosition.getColumn() + (j*l));
                    if (currPosition.getRow() < 1 || currPosition.getRow() > 8) {
                        break;
                    }
                    if (currPosition.getColumn() < 1 || currPosition.getColumn() > 8) {
                        break;
                    }
                    if (board.getPiece(currPosition) == null || board.getPiece(currPosition).getTeamColor() != pieceColor) {
                        possibleMoves.add(new ChessMove(myPosition, currPosition, null));
                    }
                    if (board.getPiece(currPosition) != null) {
                        break;
                    }
                }
            }
        }
        return possibleMoves;
    }

    public Set<ChessMove> knightPossibleMoves(Set<ChessMove> possibleMoves, ChessBoard board, ChessPosition myPosition) {
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i == j || i == (-1 * j) || i == 0 || j == 0) {
                    continue;
                }
                ChessPosition currPosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + j);
                if (currPosition.getRow() < 1 || currPosition.getRow() > 8 || currPosition.getColumn() < 1 || currPosition.getColumn() > 8) {
                    continue;
                }
                if (board.getPiece(currPosition) == null || board.getPiece(currPosition).getTeamColor() != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, currPosition, null));
                }
            }
        }
        return possibleMoves;
    }

    public Set<ChessMove> bishopPossibleMoves(Set<ChessMove> possibleMoves, ChessBoard board, ChessPosition myPosition) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int l = 1; l <= 8; l++) {
                    if (i == 0 || j == 0) {
                        break;
                    }
                    ChessPosition currPosition = new ChessPosition(myPosition.getRow() + (i*l), myPosition.getColumn() + (j*l));
                    if (currPosition.getRow() < 1 || currPosition.getRow() > 8 || currPosition.getColumn() < 1 || currPosition.getColumn() > 8) {
                        break;
                    }
                    if (board.getPiece(currPosition) == null || board.getPiece(currPosition).getTeamColor() != pieceColor) {
                        possibleMoves.add(new ChessMove(myPosition, currPosition, null));
                    }
                    if (board.getPiece(currPosition) != null) {
                        break;
                    }
                }
            }
        }
        return possibleMoves;
    }

    public Set<ChessMove> queenPossibleMoves(Set<ChessMove> possibleMoves, ChessBoard board, ChessPosition myPosition) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int l = 1; l <= 8; l++) {
                    ChessPosition currPosition = new ChessPosition(myPosition.getRow() + (i*l), myPosition.getColumn() + (j*l));
                    if (outOfBoundsChecker(myPosition, i, j, l)) {
                        break;
                    }
                    if (board.getPiece(currPosition) == null || board.getPiece(currPosition).getTeamColor() != pieceColor) {
                        possibleMoves.add(new ChessMove(myPosition, currPosition, null));
                    }
                    if (board.getPiece(currPosition) != null) {
                        break;
                    }
                }
            }
        }
        return possibleMoves;
    }

    public Set<ChessMove> kingPossibleMoves(Set<ChessMove> possibleMoves, ChessBoard board, ChessPosition myPosition) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int l = 1; l <= 8; l++) {
                    if (l > 1) {
                        break;
                    }
                    ChessPosition currPosition = new ChessPosition(myPosition.getRow() + (i*l), myPosition.getColumn() + (j*l));
                    boolean outOfBounds = outOfBoundsChecker(myPosition, i, j, l);
                    if (outOfBounds) {
                        break;
                    }
                    if (board.getPiece(currPosition) != null && board.getPiece(currPosition).getTeamColor() == pieceColor){
                        break;
                    }
                    possibleMoves.add(new ChessMove(myPosition, currPosition, null));
                }
            }
        }
        return possibleMoves;
    }

    public Set<ChessMove> pawnPossibleMoves(Set<ChessMove> possibleMoves, ChessBoard board, ChessPosition myPosition) {
        for (int j = -1; j <= 1; j++) {
            int i = 1;
            if (pieceColor == ChessGame.TeamColor.BLACK) {
                i = -1;
            }
            ChessPosition currPosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + j);
            if (currPosition.getRow() < 1 || currPosition.getRow() > 8 || currPosition.getColumn() < 1 || currPosition.getColumn() > 8) {
                break;
            }

            if ((j != 0 && board.getPiece(currPosition) != null && board.getPiece(currPosition).getTeamColor() != pieceColor) ||
                    (j == 0 && board.getPiece(currPosition) == null)) {
                if ((currPosition.getRow() == 8 && pieceColor == ChessGame.TeamColor.WHITE) ||
                        (currPosition.getRow() == 1 && pieceColor == ChessGame.TeamColor.BLACK)) {
                    possibleMoves.add(new ChessMove(myPosition, currPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, currPosition, PieceType.KNIGHT));
                    possibleMoves.add(new ChessMove(myPosition, currPosition, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(myPosition, currPosition, PieceType.QUEEN));
                }
                else {
                    possibleMoves.add(new ChessMove(myPosition, currPosition, null));
                }
            }

            if ((pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2 && j == 0) ||
                    (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7 && j == 0)) {
                ChessPosition movedTwice = new ChessPosition(currPosition.getRow() + i, currPosition.getColumn());
                if (board.getPiece(currPosition) == null && board.getPiece(movedTwice) == null) {
                    possibleMoves.add(new ChessMove(myPosition, movedTwice, null));
                }
            }
        }
        return possibleMoves;
    }
    public boolean outOfBoundsChecker(ChessPosition myPosition, int i, int j, int l) {
        boolean outOfBounds = false;
        ChessPosition currPosition = new ChessPosition(myPosition.getRow() + (i*l), myPosition.getColumn() + (j*l));
        if (currPosition.getRow() < 1 || currPosition.getRow() > 8 || currPosition.getColumn() < 1 || currPosition.getColumn() > 8) {
            outOfBounds = true;
        }
        return outOfBounds;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
