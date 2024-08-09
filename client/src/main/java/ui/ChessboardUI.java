package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static ui.EscapeSequences.*;

public class ChessboardUI {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String K = " K ";
    private static final String Q = " Q ";
    private static final String B = " B ";
    private static final String N = " N ";
    private static final String R = " R ";
    private static final String P = " P ";

    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard();
        run(chessBoard);
    }

    public static void run(ChessBoard chessBoard) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        chessBoard.resetBoard();

        out.print(ERASE_SCREEN);

        String white = "white";
        String black = "black";

        drawHeaders(out, white);

        drawTicTacToeBoard(out, chessBoard, white);

        drawHeaders(out, white);

        drawHeaders(out, black);

        drawTicTacToeBoard(out, chessBoard, black);

        drawHeaders(out, black);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, String perspective) {

        setBlack(out);

        if (perspective.equals("white")){
            String[] headers = {"    a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                drawHeader(out, headers[boardCol]);

                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                }
            }

            out.println();
        }
        if (perspective.equals("black")){
            String[] headers = {"    h ", " g ", " f ", " e ", " d ", " c ", " b ", " a "};
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                drawHeader(out, headers[boardCol]);

                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                }
            }

            out.println();
        }
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawTicTacToeBoard(PrintStream out, ChessBoard chessBoard, String perspective) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            int rowLabel = 0;
            if (perspective.equals("white")) {
                rowLabel = 8 - boardRow;
            } else {
                rowLabel = boardRow + 1;
            }
            drawRowOfSquares(out, boardRow, chessBoard, perspective, rowLabel);

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow, ChessBoard chessBoard, String perspective, int rowLabel) {
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    if (boardCol == 0) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(" " + rowLabel + " ");
                    }
                }
                else {
                    if (boardCol == 0) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print("   ");
                    }
                }

                if ((boardCol % 2 == 0 && boardRow % 2 == 0) || (boardCol % 2 != 0 && boardRow % 2 != 0)) {
                    setWhite(out);
                }
                else {
                    setBlack(out);
                }
                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
                    String letter;
                    ChessPiece piece;
                    if (perspective.equals("white")) {
                        piece = chessBoard.getPiece(new ChessPosition((7 - boardRow) + 1, boardCol + 1));
                    }
                    else {
                        piece = chessBoard.getPiece(new ChessPosition(9 - ((7 - boardRow) + 1), 9 -(boardCol + 1)));
                    }
                    String color = "";
                    //switch statements for pieces
                    if (piece != null) {
                        letter = pieceLetter(piece.getPieceType());
                        color = pieceColorToString(piece.getTeamColor());
                    }
                    else {
                        letter = EMPTY;
                    }
                    out.print(EMPTY.repeat(prefixLength));
                    printPlayer(out, letter, color);
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    if (boardCol == 7) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(" " + rowLabel + " ");
                    }
                }
                else {
                    if (boardCol == 7) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print("   ");
                    }
                }

                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    // Draw vertical column separator.
                    setRed(out);
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                }

                setBlack(out);
            }

            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, String player, String color) {
        if (color.equals("white")) {
            out.print(SET_TEXT_COLOR_RED);
        }
        else if (color.equals("black")){
            out.print(SET_TEXT_COLOR_BLUE);
        }
        out.print(player);
    }
    private static String pieceLetter(ChessPiece.PieceType pieceType) {
        String letter;
        switch (pieceType) {
            case KING -> letter = K;
            case QUEEN -> letter = Q;
            case BISHOP -> letter = B;
            case KNIGHT -> letter = N;
            case ROOK -> letter = R;
            case PAWN -> letter = P;
            default -> letter = EMPTY;
        }
        return letter;
    }
    private static String pieceColorToString(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            return "white";
        }
        else {
            return "black";
        }
    }
}