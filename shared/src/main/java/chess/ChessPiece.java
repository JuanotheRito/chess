package chess;

import chess.movecalc.*;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;
    private boolean hasMoved = false;
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

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
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

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /** Checks to see if the piece has moved
     *
     * @return True if the piece has moved, false if it has not
     */
    public boolean hasMoved() {
        return !this.hasMoved;
    }

    /** Flags the piece as having moved*/

    public void pieceMoved() {
        this.hasMoved = true;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        MoveCalculator calc = null;
        if (board.getPiece(myPosition).getPieceType() == PieceType.PAWN) {
            if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
                calc = new WhitePawnMoveCalculator();

            } else {
                calc = new BlackPawnMoveCalculator();
            }
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.KING) {
            calc = new KingMoveCalculator();
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.QUEEN){
            calc = new QueenMoveCalculator();
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.ROOK){
            calc = new RookMoveCalculator();
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.BISHOP){
            calc = new BishopMoveCalculator();
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.KNIGHT) {
            calc = new KnightMoveCalculator();
        }
        if (calc == null){
            return null;
        }
        return calc.calculateMoves(board, myPosition);
    }
}
