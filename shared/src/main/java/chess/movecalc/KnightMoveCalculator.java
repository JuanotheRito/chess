package chess.movecalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;

public class KnightMoveCalculator implements MoveCalculator{
    public ArrayList<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPosition newPosition;
        ChessPiece boardSpace;
        ChessPiece knight = board.getPiece(myPosition);

        //determines if the knight is too close to the edge of the board to execute certain moves
        boolean doubleDown = myPosition.getRow() > 2;
        boolean doubleUp = myPosition.getRow() < 7;
        boolean doubleLeft = myPosition.getColumn() > 2;
        boolean doubleRight = myPosition.getColumn() < 7;
        boolean down = myPosition.getRow() != 1;
        boolean up = myPosition.getRow() != 8;
        boolean left = myPosition.getColumn() != 1;
        boolean right = myPosition.getColumn() !=8;

        //Down and Left
        if (doubleDown && left) {
            newPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != knight.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Down and Right
        if (doubleDown && right) {
            newPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != knight.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Left and Down
        if (doubleLeft && down) {
            newPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != knight.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Left and Up
        if (doubleLeft && up) {
            newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != knight.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Right and Down
        if (doubleRight && down) {
            newPosition = new ChessPosition(myPosition.getRow() -1, myPosition.getColumn() + 2);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != knight.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Right and Up
        if (doubleRight && up) {
            newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != knight.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Up and Left
        if (doubleUp && left) {
            newPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != knight.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Up and Right
        if (doubleUp && right) {
            newPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null) {
                if (boardSpace.getTeamColor() != knight.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
            } else {
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        return validMoves;
    }

    public void addValidMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition, ChessPosition newPosition) {
        validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), null));
    }
}
