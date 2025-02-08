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
     * Adds a valid piece move to the specified ArrayList of ChessMoves
     *
     * @param validMoves    ArrayList of valid ChessMoves
     * @param startPosition Current position of the piece
     * @param newPosition   Position of the piece after the given move is executed
     */
    private void addValidMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition, ChessPosition newPosition) {
        validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), null));
    }

    /**
     * Adds a valid pawn move to the specified ArrayList of ChessMoves
     *
     * @param validMoves      ArrayList of valid ChessMoves
     * @param startPosition   Current position of the pawn
     * @param newPosition     Position of the pawn after the given move is executed
     * @param promotionPieces Array of the possible pieces the pawn can be promoted to
     */
    private void addValidPawnMove(ArrayList<ChessMove> validMoves, ChessPosition startPosition, ChessPosition newPosition, ChessPiece.PieceType[] promotionPieces) {
        if (promotionPieces[0] == null) {
            validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), null));
        } else {
            for (PieceType promotionPiece : promotionPieces) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(newPosition.getRow(), newPosition.getColumn()), promotionPiece));
            }
        }
    }

    /**
     * Calculates all the valid moves of a white pawn at the position indicated
     *
     * @param board      Chess board with pieces on it
     * @param myPosition Position of the pawn
     * @return ArrayList of all valid moves of the white pawn
     */
    public ArrayList<ChessMove> whitePawnMoveCalculator(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPosition possibleMove;
        boolean hasMoved = false;
        PieceType[] promotionPieces = new PieceType[4];
        if (myPosition.getRow() == 7) {
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
     *
     * @param board      Current game of chess being played
     * @param myPosition Position of the pawn
     * @return ArrayList of all valid moves of the white pawn
     */
    public ArrayList<ChessMove> blackPawnMoveCalculator(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPosition possibleMove;
        boolean hasMoved = false;
        PieceType[] promotionPieces = new PieceType[4];
        if (myPosition.getRow() == 2) {
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
     * Calculates all the valid moves of a king at the given position
     *
     * @param board      Current game of chess being played
     * @param myPosition Position of the king
     * @return ArrayList of all valid moves of the king
     */
    public ArrayList<ChessMove> kingMoveCalculator(ChessBoard board, ChessPosition myPosition) {
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

    /**
     * Calculates all the valid moves of a queen at the given position
     *
     * @param board      Current game of chess being played
     * @param myPosition Position of the queen
     * @return ArrayList of all valid moves of the queen
     */
    public ArrayList<ChessMove> queenMoveCalculator(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        ChessPiece boardSpace;
        ChessPiece queen = board.getPiece(myPosition);

        //calculates if the piece is on the edge of the board and which edge they are on
        boolean up = myPosition.getRow() != 8;
        boolean down = myPosition.getRow() != 1;
        boolean right = myPosition.getColumn() != 8;
        boolean left = myPosition.getColumn() != 1;

        //Up
        while (up) {
            newPosition = new ChessPosition(newPosition.getRow()+1, newPosition.getColumn());
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != queen.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                    break;
                }
                else{
                    break;
                }
            }
            else if (newPosition.getRow() == 8){
                up = false;
                addValidMove(validMoves, myPosition, newPosition);
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        up = myPosition.getRow() != 8;

        //Down
        while (down) {
            newPosition = new ChessPosition(newPosition.getRow()-1, newPosition.getColumn());
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != queen.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                    break;
                }
                else{
                    break;
                }
            }
            else if (newPosition.getRow() == 1){
                down = false;
                addValidMove(validMoves, myPosition, newPosition);
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        down = myPosition.getRow() != 1;

        //Left
        while (left) {
            newPosition = new ChessPosition(newPosition.getRow(), newPosition.getColumn()-1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != queen.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                    break;
                }
                else{
                    break;
                }
            }
            else if (newPosition.getColumn() == 1){
                left = false;
                addValidMove(validMoves, myPosition, newPosition);
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        left = myPosition.getColumn() != 1;

        //Right
        while (right) {
            newPosition = new ChessPosition(newPosition.getRow(), newPosition.getColumn()+1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != queen.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
                break;
            }
            else if (newPosition.getColumn() == 8){
                right = false;
                addValidMove(validMoves, myPosition, newPosition);
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        right = myPosition.getColumn() != 8;

        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        //Up and Left Diagonal
        while (up && left){
            newPosition = new ChessPosition(newPosition.getRow()+1, newPosition.getColumn()-1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != queen.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
                break;
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
                if (boardSpace.getTeamColor() != queen.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
                break;
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
                if (boardSpace.getTeamColor() != queen.getTeamColor()) {
                    addValidMove(validMoves, myPosition, newPosition);
                }
                break;
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
                if (boardSpace.getTeamColor() != queen.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                }
                break;
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

    /**
     * Calculates all the valid moves of a rook at the given position
     *
     * @param board      Current game of chess being played
     * @param myPosition Position of the rook
     * @return ArrayList of all valid moves of the rook
     */
    public ArrayList<ChessMove> rookMoveCalculator(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        ChessPiece boardSpace;
        ChessPiece rook = board.getPiece(myPosition);

        //calculates if the piece is on the edge of the board and which edge they are on
        boolean up = myPosition.getRow() != 8;
        boolean down = myPosition.getRow() != 1;
        boolean right = myPosition.getColumn() != 8;
        boolean left = myPosition.getColumn() != 1;

        //Up
        while (up) {
            newPosition = new ChessPosition(newPosition.getRow()+1, newPosition.getColumn());
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != rook.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                    break;
                }
                else{
                    break;
                }
            }
            else if (newPosition.getRow() == 8){
                up = false;
                addValidMove(validMoves, myPosition, newPosition);
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        //Down
        while (down) {
            newPosition = new ChessPosition(newPosition.getRow()-1, newPosition.getColumn());
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != rook.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                    break;
                }
                else {
                    break;
                }
            }
            else if (newPosition.getRow() == 1){
                down = false;
                addValidMove(validMoves, myPosition, newPosition);
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        //Left
        while (left) {
            newPosition = new ChessPosition(newPosition.getRow(), newPosition.getColumn()-1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != rook.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                    break;
                }
                else{
                    break;
                }
            }
            else if (newPosition.getColumn() == 1){
                left = false;
                addValidMove(validMoves, myPosition, newPosition);
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }
        newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        //Right
        while (right) {
            newPosition = new ChessPosition(newPosition.getRow(), newPosition.getColumn()+1);
            boardSpace = board.getPiece(newPosition);
            if (boardSpace != null){
                if (boardSpace.getTeamColor() != rook.getTeamColor()){
                    addValidMove(validMoves, myPosition, newPosition);
                    break;
                }
                else{
                    break;
                }
            }
            else if (newPosition.getColumn() == 8){
                right = false;
                addValidMove(validMoves, myPosition, newPosition);
            }
            else{
                addValidMove(validMoves, myPosition, newPosition);
            }
        }

        return validMoves;
    }

    /**
     * Calculates all the valid moves of a bishop at the given position
     *
     * @param board      Current game of chess being played
     * @param myPosition Position of the bishop
     * @return ArrayList of all valid moves of the bishop
     */
    public ArrayList<ChessMove> bishopMoveCalculator(ChessBoard board, ChessPosition myPosition){
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

    /**
     * Calculates all the valid moves of a knight at the given position
     *
     * @param board      Current game of chess being played
     * @param myPosition Position of the knight
     * @return ArrayList of all valid moves of the knight
     */
    public ArrayList<ChessMove> knightMoveCalculator(ChessBoard board, ChessPosition myPosition) {
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


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition).getPieceType() == PieceType.PAWN) {
            if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
                return whitePawnMoveCalculator(board, myPosition);
            } else {
                return blackPawnMoveCalculator(board, myPosition);
            }
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.KING) {
            return kingMoveCalculator(board, myPosition);
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.QUEEN){
            return queenMoveCalculator(board, myPosition);
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.ROOK){
            return rookMoveCalculator(board, myPosition);
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.BISHOP){
            return bishopMoveCalculator(board, myPosition);
        }
        if (board.getPiece(myPosition).getPieceType() == PieceType.KNIGHT) {
            return knightMoveCalculator(board, myPosition);
        }
        throw new RuntimeException("Not implemented");
    }
}
