package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamColor = TeamColor.WHITE;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> possibleMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> result = new HashSet<>();
        for (ChessMove element : possibleMoves) {
            ChessPiece capturedPiece = board.getPiece(element.getEndPosition());
            board.addPiece(element.getEndPosition(), board.getPiece(element.getStartPosition()));
            board.removePiece(element.getStartPosition());
            if (!isInCheck(board.getPiece(element.getEndPosition()).getTeamColor())) {
                result.add(element);
            }
            board.addPiece(element.getStartPosition(), board.getPiece(element.getEndPosition()));
            board.removePiece(element.getEndPosition());
            if (capturedPiece != null ) {
                board.addPiece(element.getEndPosition(), capturedPiece);
            }
        }
        return result;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (teamColor != board.getPiece(move.getStartPosition()).getTeamColor()){
            throw new InvalidMoveException();
        }
        if (validMoves.contains(move)) {
            if (move.getPromotionPiece() != null) {
                ChessPiece piece =  new ChessPiece(board.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), piece);
            }
            else {
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            }
            board.removePiece(move.getStartPosition());

        } else {
            throw new InvalidMoveException();
        }
        if (teamColor == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //Find active player's king
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i,j);
                if (board.getPiece(currPosition) != null
                        && board.getPiece(currPosition).getPieceType() == ChessPiece.PieceType.KING
                        && board.getPiece(currPosition).getTeamColor() == teamColor) {
                    kingPosition = currPosition;
                }
            }
        }
        //If for some reason there is no king to check
        if (kingPosition == null) {
            return false;
        }

        //Iterate through each square and check if an enemy piece can capture the king
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i,j);
                if (board.getPiece(currPosition) == null || board.getPiece(currPosition).getTeamColor() == teamColor) {
                    continue;
                }
                Collection<ChessMove> enemyMoves = board.getPiece(currPosition).pieceMoves(board, currPosition);
                for (ChessMove element : enemyMoves) {
                    if (element.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //Check if king is currently in check.
        if (!isInCheck(teamColor)) {
            return false;
        }
        //Check if any move can get the king out of check.
        return isEveryOtherSquareUnsafe(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //Check if king is currently in check.
        if (isInCheck(teamColor)) {
            return false;
        }
        //Check if any move is valid
        return isEveryOtherSquareUnsafe(teamColor);
    }

    public boolean isEveryOtherSquareUnsafe(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                if (board.getPiece(currPosition) == null || board.getPiece(currPosition).getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> validMoves = validMoves(currPosition);
                if (!validMoves.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
