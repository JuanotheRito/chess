package chess;

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

    /**
     * Adds a valid piece move to the specified ArrayList of ChessMoves
     * @param validMoves ArrayList of valid ChessMoves
     * @param startPosition Current position of the piece
     * @param newPosition Position of the piece after the given move is executed
     */
    private void addValidMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition, ChessPosition newPosition){
        validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), null));
    }

    /**
     * Adds a valid pawn move to the specified ArrayList of ChessMoves
     * @param validMoves ArrayList of valid ChessMoves
     * @param startPosition Current position of the pawn
     * @param newPosition Position of the pawn after the given move is executed
     * @param promotionPieces Array of the possible pieces the pawn can be promoted to
     */
    private void addValidPawnMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition, ChessPosition newPosition, ChessPiece.PieceType[] promotionPieces) {
        if (promotionPieces[0] == null){
            validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), null));
        }
        else {
            for (PieceType promotionPiece : promotionPieces) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), promotionPiece));
            }
        }
    }

    /**
     * Calculates all the valid moves of a white pawn at the position indicated
     * @param board Chess board with pieces on it
     * @param myPosition Position of the pawn
     * @return ArrayList of all valid moves of the white pawn
     */
    public ArrayList<ChessMove> whitePawnMoveCalculator(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<ChessMove>();
        ChessPosition possibleMove = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        boolean hasMoved = false;
        PieceType[] promotionPieces = new PieceType[4];
        if (myPosition.getRow() == 7){
            promotionPieces[0] = PieceType.QUEEN;
            promotionPieces[1] = PieceType.KNIGHT;
            promotionPieces[2] = PieceType.ROOK;
            promotionPieces[3] = PieceType.BISHOP;
        }
        if (myPosition.getRow() != 8) {
            //checks to see if the Pawn has moved
            if (myPosition.getRow() != 2) {
                hasMoved = true;
            }
            //Checks to see if a double move is valid
            possibleMove = new ChessPosition((myPosition.getRow()) + 2, myPosition.getColumn());
            if (!hasMoved && board.getPiece(possibleMove) == null &&
                    board.getPiece(new ChessPosition(possibleMove.getRow() + 1, possibleMove.getColumn())) == null) {
                addValidPawnMove(validMoves, myPosition, possibleMove, promotionPieces);
            }
            //Checks to see if a single move is valid
            possibleMove = new ChessPosition((myPosition.getRow()) + 1, myPosition.getColumn());
            if (board.getPiece(possibleMove) == null) {
                addValidPawnMove(validMoves, myPosition, possibleMove, promotionPieces);
            }
            //checks to see if it can capture left diagonally
            possibleMove = new ChessPosition((myPosition.getRow()) + 1, (myPosition.getColumn() - 1));
            if (myPosition.getColumn() != 1) {
                if (board.getPiece(possibleMove) != null) {
                    if (board.getPiece(possibleMove).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        addValidPawnMove(validMoves, myPosition, possibleMove, promotionPieces);
                    }
                }
            }
            //checks to see if it can capture right diagonally
            possibleMove = new ChessPosition((myPosition.getRow()) + 1, (myPosition.getColumn()) + 1);
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
     * Calculates all the valid moves of a black pawn at the position indicated
     * @param board Current game of chess being played
     * @param myPosition Position of the pawn
     * @return ArrayList of all valid moves of the white pawn
     */
    public ArrayList<ChessMove> blackPawnMoveCalculator(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<ChessMove>();
        ChessPosition possibleMove = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        boolean hasMoved = false;
        PieceType[] promotionPieces = new PieceType[4];
        if (myPosition.getRow() == 2){
            promotionPieces[0] = PieceType.QUEEN;
            promotionPieces[1] = PieceType.KNIGHT;
            promotionPieces[2] = PieceType.ROOK;
            promotionPieces[3] = PieceType.BISHOP;
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
     * Calculates all the valid moves of a king at the position given
     * @param board Current game of chess being played
     * @param myPosition Position of the king
     * @return ArrayList of all valid moves of the king
     */
    public ArrayList<ChessMove> kingMoveCalculator(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        boolean moveUp = myPosition.getRow() != 8;
        boolean moveDown = myPosition.getRow() != 1;
        boolean moveRight = myPosition.getColumn() != 8;
        boolean moveLeft = myPosition.getColumn() != 1;
        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        ChessPiece boardSpace;
        ChessPiece king = board.getPiece(myPosition);
        //Upwards Move
        if (moveUp){
            newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
            boardSpace = board.getPiece(newPosition);
            if (boardSpace!=null){
                if (boardSpace.getTeamColor()!= king.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Downwards Move
        if (moveDown){
            newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
            boardSpace = board.getPiece(newPosition);
            if (boardSpace!=null){
                if (boardSpace.getTeamColor()!= king.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Left Move
        if (moveLeft){
            newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace!=null){
                if (boardSpace.getTeamColor()!= king.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Right Move
        if (moveRight){
            newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace!=null){
                if (boardSpace.getTeamColor()!= king.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Diagonal Up Left Move
        if (moveLeft && moveUp){
            newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace!=null){
                if (boardSpace.getTeamColor()!= king.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Diagonal Down Left Move
        if (moveLeft && moveDown){
            newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace!=null){
                if (boardSpace.getTeamColor()!= king.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Diagonal Up Right Move
        if (moveUp && moveRight){
            newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace!=null){
                if (boardSpace.getTeamColor()!= king.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        //Diagonal Down Right Move
        if (moveDown && moveRight){
            newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace!=null){
                if (boardSpace.getTeamColor()!= king.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        return validMoves;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition).getPieceType() == PieceType.PAWN){
            if(board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
                return whitePawnMoveCalculator(board, myPosition);
            }
            else{
                return blackPawnMoveCalculator(board, myPosition);
            }
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.KING){
            return kingMoveCalculator(board, myPosition);
        }
        throw new RuntimeException("Not implemented");
    }
}
