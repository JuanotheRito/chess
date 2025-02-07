package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board = new ChessBoard();
    public ChessGame() {

    }
    TeamColor turn = TeamColor.WHITE;
    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
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
        ChessPiece piece = board.getPiece(startPosition);
        ArrayList <ChessMove> moves = (ArrayList<ChessMove>) piece.pieceMoves(this.board, startPosition);
        ArrayList <ChessMove> valid = new ArrayList<>();
        boolean isValidMove;
        for (ChessMove move: moves){
            isValidMove = testMove(move);
            if (isValidMove){
                valid.add(move);
            }
        }
        return valid;
    }

    /** Tests to see if a move is valid or not without changing the state of the actual board
     *
     * @param move Move to be tested
     * @return True if the move is valid, false if it is invalid
     */
    public boolean testMove(ChessMove move){
        boolean validMove = true;
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.removePiece(move.getStartPosition());
        if (isInCheck(piece.getTeamColor())){
            validMove = false;
        }
        board.addPiece(move.getStartPosition(), piece);
        return validMove;
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        String message;
        if(piece == null){
            throw new InvalidMoveException("There is no piece to move");
        }
        if(piece.getTeamColor() != this.turn){
            if (piece.getTeamColor() == TeamColor.WHITE){
                message = "It is not white's turn.";
            }
            else{
                message = "It is not black's turn.";
            }
            throw new InvalidMoveException(message);
        }
        ArrayList <ChessMove> validMoves = (ArrayList<ChessMove>) validMoves(move.getStartPosition());
        InvalidMoveException InvalidMove;
        if (!validMoves.contains(move)){
            InvalidMove = new InvalidMoveException("Attempted move is not legal");
            throw InvalidMove;
        }
        else{
            board.removePiece(move.getStartPosition());
            if (move.getPromotionPiece() != null){
                piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getEndPosition(), piece);
            if (turn == TeamColor.WHITE){
                setTeamTurn(TeamColor.BLACK);
            }
            else{
                setTeamTurn(TeamColor.WHITE);
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition position;
        ChessPiece piece;
        ArrayList<ChessMove> pieceMoves;
        ChessPosition kingPosition = new ChessPosition(1,1);
        boolean check = false;
        boolean kingFound = false;

        //finds the king of teamColor
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                kingPosition = new ChessPosition(x, y);
                piece = board.getPiece(kingPosition);
                if(piece == null){
                    continue;
                }
                else if(piece.getPieceType() != ChessPiece.PieceType.KING){
                    if(piece.getTeamColor() != teamColor){
                        kingFound = true;
                        break;
                    }
                }
            }
            if (kingFound){
                break;
            }
        }

        //checks moves for each enemy piece, ignoring peaces of the color teamColor
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++ ){
                position = new ChessPosition(row, col);
                piece = board.getPiece(position);
                if(piece == null){
                    continue;
                }
                else if(piece.getTeamColor() != teamColor){
                    pieceMoves = (ArrayList<ChessMove>) piece.pieceMoves(this.board, position);
                    for(ChessMove validMove: pieceMoves){
                        if (validMove.getEndPosition() == kingPosition){
                            check = true;
                            return check;
                        }
                    }
                }
            }
        }
        return check;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
        return this.board;
    }
}
