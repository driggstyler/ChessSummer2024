package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

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

    private static Random rand = new Random();


    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        run(chessBoard);
    }

    public static void run(ChessBoard chessBoard) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        String white = "white";
        String black = "black";

        drawHeaders(out);

        drawTicTacToeBoard(out, chessBoard, white);

        drawHeaders(out);

        drawHeaders(out);

        drawTicTacToeBoard(out, chessBoard, black);

        drawHeaders(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = { " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h " };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

        out.println();
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

            drawRowOfSquares(out, boardRow, chessBoard, perspective);

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                // Draw horizontal row separator.
                drawHorizontalLine(out);
                setBlack(out);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow, ChessBoard chessBoard, String perspective) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    if (boardCol == 0) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(squareRow);
                    }
                }
                else {
                    if (boardCol == 0) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(" ");
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
                    //Iterate based on perspective
                    int chessRow;
                    int chessCol;
                    if (perspective.equals("white")) {
                        chessRow = 7 - boardRow;
                        chessCol = 7 - boardCol;
                    }
                    else {
                        chessRow = boardRow;
                        chessCol = boardCol;
                    }
                    String letter;
                    ChessPiece piece = chessBoard.getPiece(new ChessPosition(chessRow + 1, chessCol + 1));
                    String color = "";
                    //switch statements for pieces
                    if (piece != null) {
                        switch (piece.getPieceType()) {
                            case KING -> letter = Q;
                            case QUEEN -> letter = K;
                            case BISHOP -> letter = B;
                            case KNIGHT -> letter = N;
                            case ROOK -> letter = R;
                            case PAWN -> letter = P;
                            default -> letter = EMPTY;
                        }
                        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                            color = "white";
                        }
                        else {
                            color = "black";
                        }
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

    private static void drawHorizontalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
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
        //out.print(SET_BG_COLOR_WHITE);
        if (color.equals("white")) {
            out.print(SET_TEXT_COLOR_RED);
        }
        else if (color.equals("black")){
            out.print(SET_TEXT_COLOR_BLUE);
        }
        out.print(player);
    }
}