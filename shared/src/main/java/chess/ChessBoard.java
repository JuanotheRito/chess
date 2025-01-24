package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public ChessPiece[][] board = new ChessPiece[8][8];

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }

    /**
     * A class method that sets the board to its default state
     * at the beginning of a game of chess.
     */

    public ChessBoard() {

    }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int col = position.getColumn();
        int row = position.getRow();

        this.board[row - 1][col - 1] = piece;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int col = position.getColumn();
        int row = position.getRow();
        if (this.board[row-1][col-1] != null){
            return this.board[row-1][col-1];
        }
        else{
            return null;
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (y == 0) {
                    color = ChessGame.TeamColor.WHITE;
                }
                if (y == 7) {
                    color = ChessGame.TeamColor.BLACK;
                }
                if (y == 0 || y == 7) {
                    if (x == 0 || x == 7) {
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                    }
                    if (x == 1 || x == 6) {
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                    }
                    if (x == 2 || x == 5) {
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                    }
                    if (x == 3) {
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
                    }
                    if (x == 4) {
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.KING);
                    }
                }
                if (y == 1) {
                    this.board[y][x] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                }
                if (1 < y && y < 6) {
                    this.board[y][x] = null;
                }
                if (y == 6) {
                    this.board[y][x] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                }
            }
        }
    }
}
