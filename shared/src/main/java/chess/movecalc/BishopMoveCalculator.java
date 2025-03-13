package chess.movecalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;

public class BishopMoveCalculator implements MoveCalculator{
    /**
     * Calculates all the valid moves of a bishop at the given position
     *
     * @param board      Current game of chess being played
     * @param myPosition Position of the bishop
     * @return ArrayList of all valid moves of the bishop
     */
    public ArrayList<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        ChessPiece boardSpace;
        ChessPiece bishop = board.getPiece(myPosition);

        //calculates if the piece is on the edge of the board and which edge they are on
        boolean up = myPosition.getRow() != 8;
        boolean down = myPosition.getRow() != 1;
        boolean right = myPosition.getColumn() != 8;
        boolean left = myPosition.getColumn() != 1;

        //Up and Left Diagonal
        while (up && left){
            newPosition = new ChessPosition(newPosition.getRow()+1, newPosition.getColumn()-1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != bishop.getTeamColor()){
                    up = false;
                    left = false;
                    addValidMove(validMoves, myPosition, newPosition);
                }
                else{
                    break;
                }
            }
            else if (newPosition.getColumn() == 1){
                addValidMove(validMoves, myPosition, newPosition);
                break;
            }
            else if (newPosition.getRow() == 8){
                addValidMove(validMoves, myPosition, newPosition);
                break;
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }

        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        up = myPosition.getRow() != 8;
        left = myPosition.getColumn() != 1;

        //Up and Right Diagonal
        while (up && right){
            newPosition = new ChessPosition(newPosition.getRow()+1, newPosition.getColumn()+1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != bishop.getTeamColor()){
                    up = false;
                    right = false;
                    addValidMove(validMoves, myPosition, newPosition);
                }
                else{
                    break;
                }
            }
            else if (newPosition.getColumn() == 8){
                addValidMove(validMoves, myPosition, newPosition);
                break;
            }
            else if (newPosition.getRow() == 8){
                addValidMove(validMoves, myPosition, newPosition);
                break;
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        right = myPosition.getColumn() != 8;
        //Down and Left Diagonal
        while (down && left){
            newPosition = new ChessPosition(newPosition.getRow()-1, newPosition.getColumn()-1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != bishop.getTeamColor()){
                    down = false;
                    left = false;
                    addValidMove(validMoves, myPosition, newPosition);
                }
                else{
                    break;
                }
            }
            else if (newPosition.getColumn() == 1){
                addValidMove(validMoves, myPosition, newPosition);
                break;
            }
            else if (newPosition.getRow() == 1){
                addValidMove(validMoves, myPosition, newPosition);
                break;
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        down = myPosition.getRow() != 1;

        //Down and Right Diagonal
        while (down && right){
            newPosition = new ChessPosition(newPosition.getRow()-1, newPosition.getColumn()+1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != bishop.getTeamColor()){
                    down = false;
                    right = false;
                    addValidMove(validMoves, myPosition, newPosition);
                }
                else{
                    break;
                }
            }
            else if (newPosition.getColumn() == 8){
                addValidMove(validMoves, myPosition, newPosition);
                break;
            }
            else if (newPosition.getRow() == 1){
                addValidMove(validMoves, myPosition, newPosition);
                break;
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        return validMoves;
    }

    public void addValidMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition, ChessPosition newPosition) {
        validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), null));
    }
}
