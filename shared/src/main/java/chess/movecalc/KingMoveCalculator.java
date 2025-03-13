package chess.movecalc;

import chess.*;

import java.util.ArrayList;

public class KingMoveCalculator implements MoveCalculator {
    /**
     * Calculates all the valid moves of a king at the given position
     *
     * @param board      Current game of chess being played
     * @param myPosition Position of the king
     * @return ArrayList of all valid moves of the king
     */
    public ArrayList<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        boolean moveUp = myPosition.getRow() != 8;
        boolean moveDown = myPosition.getRow() != 1;
        boolean moveRight = myPosition.getColumn() != 8;
        boolean moveLeft = myPosition.getColumn() != 1;
        ChessPosition newPosition;
        ChessPiece boardSpace;
        ChessPiece king = board.getPiece(myPosition);
        //Upwards Move
        if (moveUp) {
            newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != king.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Downwards Move
        if (moveDown) {
            newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != king.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Left Move
        if (moveLeft) {
            newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != king.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Right Move
        if (moveRight) {
            newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != king.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Diagonal Up Left Move
        if (moveLeft && moveUp) {
            newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != king.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Diagonal Down Left Move
        if (moveLeft && moveDown) {
            newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != king.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Diagonal Up Right Move
        if (moveUp && moveRight) {
            newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != king.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Diagonal Down Right Move
        if (moveDown && moveRight) {
            newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != king.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Castling
        if (king.getTeamColor() == ChessGame.TeamColor.BLACK){
            if (myPosition.getRow()!=8 || myPosition.getColumn()!=5){
                king.pieceMoved();
            }
        }
        if (king.getTeamColor() == ChessGame.TeamColor.WHITE){
            if (myPosition.getRow()!=1 || myPosition.getColumn()!=5){
                king.pieceMoved();
            }
        }

        return validMoves;
    }
    public void addValidMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition, ChessPosition newPosition) {
        validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), null));
    }
}
