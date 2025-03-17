package chess;

import chess.movecalc.EnPassantCalculator;
import chess.movecalc.MoveCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board = new ChessBoard();
    private ChessMove previous;
    public ChessGame() {
        board.resetBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && Objects.equals(previous, chessGame.previous) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, previous, turn);
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

    public ChessMove getPrevious(){
        return this.previous;
    }

    public void clearPrevious(){
        this.previous = null;
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
        ArrayList<ChessMove> moves = (ArrayList<ChessMove>) piece.pieceMoves(this.board, startPosition);
        ArrayList<ChessMove> valid = new ArrayList<>();
        boolean isValidMove;
        for (ChessMove move : moves) {
            isValidMove = testMove(move);
            if (isValidMove) {
                valid.add(move);
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING&&!this.isInCheck(piece.getTeamColor())) {
            if (piece.hasMoved()) {
                ChessMove castle;
                int[] rookColumns = {1, 8};
                for (int column : rookColumns) {
                    ChessPosition rookPosition = new ChessPosition(startPosition.getRow(), column);
                    ChessPiece rook = board.getPiece(rookPosition);
                    if (rook != null) {
                        boolean pieceBetween = false;
                        ChessPosition tempPosition;
                        if (rook.getPieceType() == ChessPiece.PieceType.ROOK && rook.hasMoved()) {
                            if (column == 1) {
                                for (int i = 2; i < startPosition.getColumn(); i++) {
                                    tempPosition = new ChessPosition(startPosition.getRow(), i);
                                    if (board.getPiece(tempPosition) != null || !testMove(new ChessMove(startPosition, tempPosition, null))) {
                                        pieceBetween = true;
                                    }
                                }
                            }
                            if (column == 8) {
                                for (int i = 7; i > startPosition.getColumn(); i--) {
                                    tempPosition = new ChessPosition(startPosition.getRow(), i);
                                    if (board.getPiece(tempPosition) != null || !testMove(new ChessMove(startPosition, tempPosition, null))){
                                        pieceBetween = true;
                                    }
                                }
                            }
                            if (!pieceBetween) {
                                if (!isInCheck(piece.getTeamColor())) {
                                    if (column == 1) {
                                        castle = new ChessMove(startPosition, new ChessPosition(
                                                startPosition.getRow(), startPosition.getColumn() - 2), null
                                        );
                                        isValidMove = testMove(castle);
                                        if (isValidMove) {
                                            castle.setCastle();
                                            valid.add(castle);
                                        }
                                    }
                                    if (column == 8) {
                                        castle = new ChessMove(startPosition, new ChessPosition(
                                                startPosition.getRow(), startPosition.getColumn() + 2), null
                                        );
                                        isValidMove = testMove(castle);
                                        if (isValidMove) {
                                            castle.setCastle();
                                            valid.add(castle);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
            EnPassantCalculator calc = new EnPassantCalculator();
            ArrayList<ChessMove> enPassant = calc.enPassant(piece, startPosition, this.board, this);
            valid.addAll(enPassant);
        }
        return valid;
    }


    public boolean testMove(ChessMove move) {
        boolean validMove = true;
        ChessPiece enemyPiece = board.getPiece(move.getEndPosition());
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.removePiece(move.getStartPosition());
        board.removePiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), piece);
        if (isInCheck(piece.getTeamColor())) {
            validMove = false;
        }
        board.removePiece(move.getEndPosition());
        board.addPiece(move.getStartPosition(), piece);
        if (enemyPiece != null) {
            board.addPiece(move.getEndPosition(), enemyPiece);
        }
        return validMove;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessMove targetMove = null;
        ChessPiece piece = board.getPiece(move.getStartPosition());
        String message;
        if (piece == null) {
            throw new InvalidMoveException("There is no piece to move");
        }
        if (piece.getTeamColor() != this.turn) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                message = "It is not white's turn.";
            } else {
                message = "It is not black's turn.";
            }
            throw new InvalidMoveException(message);
        }
        ArrayList<ChessMove> validMoves = (ArrayList<ChessMove>) validMoves(move.getStartPosition());
        InvalidMoveException invalidMove;
        if (!validMoves.contains(move)) {
            invalidMove = new InvalidMoveException("Attempted move is not legal");
            throw invalidMove;
        } else {
            for (ChessMove validMove : validMoves) {
                if (validMove.equals(move)) {
                    targetMove = validMove;
                }
            }
            board.removePiece(move.getStartPosition());
            if (move.getPromotionPiece() != null) {
                piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getEndPosition(), piece);
            this.clearPrevious();
            this.previous = move;
            piece.pieceMoved();
            if (targetMove != null) {
                if (targetMove.getCastle()) {
                    if (move.getEndPosition().getColumn() < move.getStartPosition().getColumn()) {
                        ChessMove rookMove = new ChessMove(
                                new ChessPosition(move.getStartPosition().getRow(), 1),
                                new ChessPosition(move.getStartPosition().getRow(), 4), null
                        );
                        piece = board.getPiece(rookMove.getStartPosition());
                        board.removePiece(rookMove.getStartPosition());
                        board.addPiece(rookMove.getEndPosition(), piece);
                    }
                    if (move.getEndPosition().getColumn() > move.getStartPosition().getColumn()) {
                        ChessMove rookMove = new ChessMove(
                                new ChessPosition(move.getStartPosition().getRow(), 8),
                                new ChessPosition(move.getStartPosition().getRow(), 6), null
                        );
                        piece = board.getPiece(rookMove.getStartPosition());
                        board.removePiece(rookMove.getStartPosition());
                        board.addPiece(rookMove.getEndPosition(), piece);
                    }
                }

                if (targetMove.getEnPassant()){
                    if (piece.getTeamColor() == TeamColor.WHITE) {
                        board.removePiece(new ChessPosition(targetMove.getEndPosition().getRow() - 1, targetMove.getEndPosition().getColumn()));
                    }
                    if (piece.getTeamColor() == TeamColor.BLACK){
                        board.removePiece(new ChessPosition(targetMove.getEndPosition().getRow() + 1, targetMove.getEndPosition().getColumn()));
                    }
                }
            }
            if (turn == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
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
        ChessPosition kingPosition = new ChessPosition(1, 1);
        boolean check = false;
        boolean kingFound = false;

        //finds the king of teamColor
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                kingPosition = new ChessPosition(x, y);
                piece = board.getPiece(kingPosition);
                if (piece == null) {
                    continue;
                }
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (piece.getTeamColor() == teamColor) {
                        kingFound = true;
                        break;
                    }
                }
            }
            if (kingFound) {
                break;
            }
        }

        //checks moves for each enemy piece, ignoring peaces of the color teamColor
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                position = new ChessPosition(row, col);
                piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }
                if (piece.getTeamColor() != teamColor) {
                    pieceMoves = (ArrayList<ChessMove>) piece.pieceMoves(this.board, position);
                    for (ChessMove validMove : pieceMoves) {
                        ChessPosition endPosition = validMove.getEndPosition();
                        if (endPosition.equals(kingPosition)) {
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
        ChessPosition position;
        ChessPiece piece;
        boolean checkmate = false;
        ArrayList<ChessMove> validMoves;
        if (isInCheck(teamColor)) {
            checkmate = true;
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    position = new ChessPosition(row, col);
                    piece = board.getPiece(position);
                    if (piece == null || piece.getTeamColor() != teamColor) {
                        continue;
                    }
                    if (piece.getTeamColor() == teamColor) {
                        validMoves = (ArrayList<ChessMove>) this.validMoves(position);
                        if (!validMoves.isEmpty()) {
                            checkmate = false;
                        }
                    }
                }
            }
        }
        return checkmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition position;
        ChessPiece piece;
        boolean stalemate = false;
        ArrayList<ChessMove> validMoves;
        if (!isInCheck(teamColor)) {
            stalemate = true;
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    position = new ChessPosition(row, col);
                    piece = board.getPiece(position);
                    if (piece == null || piece.getTeamColor() != teamColor) {
                        continue;
                    }
                    if (piece.getTeamColor() == teamColor) {
                        validMoves = (ArrayList<ChessMove>) this.validMoves(position);
                        if (!validMoves.isEmpty()) {
                            stalemate = false;
                        }
                    }
                }
            }
        }
        return stalemate;
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
