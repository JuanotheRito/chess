package chess.movecalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public interface MoveCalculator{
    void addValidMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition, ChessPosition newPosition);
    ArrayList<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition);
}
