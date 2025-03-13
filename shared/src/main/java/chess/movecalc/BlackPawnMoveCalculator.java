package chess.movecalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;

public class BlackPawnMoveCalculator implements MoveCalculator {
    /**
     * Calculates all the valid moves of a black pawn at the position indicated
     *
     * @param board      Current game of chess being played
     * @param myPosition Position of the pawn
     * @return ArrayList of all valid moves of the white pawn
     */
    public ArrayList<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPosition possibleMove;
        boolean hasMoved = false;
        ChessPiece.PieceType[] promotionPieces = new ChessPiece.PieceType[4];
        if (myPosition.getRow() == 2) {
            promotionPieces[0] = ChessPiece.PieceType.QUEEN;
            promotionPieces[1] = ChessPiece.PieceType.KNIGHT;
            promotionPieces[2] = ChessPiece.PieceType.ROOK;
            promotionPieces[3] = ChessPiece.PieceType.BISHOP;
        }
        if (myPosition.getRow() != 1) {
            //checks to see if the Pawn has moved
            if (myPosition.getRow() != 7) {
                hasMoved = true;
            }
            //Checks to see if a double move is valid
            possibleMove = new ChessPosition((myPosition.getRow()) - 2, myPosition.getColumn());
            if (!hasMoved && board.getPiece(possibleMove) == null &&
                    board.getPiece(new ChessPosition(possibleMove.getRow() + 1, possibleMove.getColumn())) == null) {
                addValidPawnMove(validMoves, myPosition, possibleMove, promotionPieces);
            }
            //Checks to see if a single move is valid
            possibleMove = new ChessPosition((myPosition.getRow()) - 1, myPosition.getColumn());
            if (board.getPiece(possibleMove) == null) {
                addValidPawnMove(validMoves, myPosition, possibleMove, promotionPieces);
            }
            //checks to see if it can capture left diagonally
            possibleMove = new ChessPosition((myPosition.getRow()) - 1, (myPosition.getColumn() - 1));
            if (myPosition.getColumn() != 1) {
                if (board.getPiece(possibleMove) != null) {
                    if (board.getPiece(possibleMove).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        addValidPawnMove(validMoves, myPosition, possibleMove, promotionPieces);
                    }
                }
            }
            //checks to see if it can capture right diagonally
            possibleMove = new ChessPosition((myPosition.getRow()) - 1, (myPosition.getColumn()) + 1);
            if (myPosition.getColumn() != 8) {
                if (board.getPiece(possibleMove) != null) {
                    if (board.getPiece(possibleMove).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        addValidPawnMove(validMoves, myPosition, possibleMove, promotionPieces);
                    }
                }
            }
        }
        return validMoves;
    }
    /**
     * Adds a valid pawn move to the specified ArrayList of ChessMoves
     *
     * @param validMoves      ArrayList of valid ChessMoves
     * @param startPosition   Current position of the pawn
     * @param newPosition     Position of the pawn after the given move is executed
     * @param promotionPieces Array of the possible pieces the pawn can be promoted to
     */
    private void addValidPawnMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition,
                                  ChessPosition newPosition, ChessPiece.PieceType[] promotionPieces) {
        ChessMove validMove = new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), null);
        if (promotionPieces[0] == null) {
            validMoves.add(validMove);
        } else {
            for (ChessPiece.PieceType promotionPiece : promotionPieces) {
                validMove = new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), promotionPiece);
                validMoves.add(validMove);
            }
        }
    }
    public void addValidMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition, ChessPosition newPosition) {
        validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), null));
    }
}
