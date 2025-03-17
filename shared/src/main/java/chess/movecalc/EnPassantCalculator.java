package chess.movecalc;

import chess.*;

import java.util.ArrayList;

public class EnPassantCalculator {
    public ArrayList<ChessMove> enPassant(ChessPiece piece, ChessPosition startPosition, ChessBoard board, ChessGame game) {
        boolean isValidMove;
        ArrayList<ChessMove> valid = new ArrayList<>();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && startPosition.getRow() == 5) {
            //Left En Passant
            ChessMove passant = new ChessMove(startPosition,
                    new ChessPosition((startPosition.getRow()) + 1,
                            (startPosition.getColumn() - 1)), null);
            ChessMove previousCheck = new ChessMove(
                    new ChessPosition(startPosition.getRow() + 2, startPosition.getColumn() - 1),
                    new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1),
                    null
            );

            if (startPosition.getColumn() != 1) {
                if (board.getPiece(passant.getEndPosition()) == null) {
                    ChessPiece enemyPawn = board.getPiece(new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1));
                    if (enemyPawn != null) {
                        if (
                                enemyPawn.getTeamColor() != ChessGame.TeamColor.WHITE &&
                                        game.getPrevious().equals(previousCheck) &&
                                        enemyPawn.getPieceType() == ChessPiece.PieceType.PAWN
                        ) {
                            isValidMove = game.testMove(passant);
                            if (isValidMove) {
                                passant.setEnPassant();
                                valid.add(passant);
                            }
                        }
                    }
                }
            }

            passant = new ChessMove(startPosition, new ChessPosition((startPosition.getRow()) + 1, (startPosition.getColumn() + 1)), null);
            previousCheck = new ChessMove(
                    new ChessPosition(startPosition.getRow() + 2, startPosition.getColumn() + 1),
                    new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1),
                    null
            );
            // Right En Passant
            if (startPosition.getColumn() != 8) {
                if (board.getPiece(passant.getEndPosition()) == null) {
                    ChessPiece enemyPawn = board.getPiece(new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1));
                    if (enemyPawn != null) {
                        if (
                                enemyPawn.getTeamColor() != ChessGame.TeamColor.WHITE &&
                                        game.getPrevious().equals(previousCheck) &&
                                        enemyPawn.getPieceType() == ChessPiece.PieceType.PAWN
                        ) {
                            isValidMove = game.testMove(passant);
                            if (isValidMove) {
                                passant.setEnPassant();
                                valid.add(passant);
                            }
                        }
                    }
                }
            }
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && startPosition.getRow() == 4) {
            //Left En Passant
            ChessMove passant = new ChessMove(startPosition, new ChessPosition((startPosition.getRow()) - 1,
                    (startPosition.getColumn() - 1)), null);
            ChessMove previousCheck = new ChessMove(
                    new ChessPosition(startPosition.getRow() - 2, startPosition.getColumn() - 1),
                    new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1),
                    null
            );

            if (startPosition.getColumn() != 1) {
                if (board.getPiece(passant.getEndPosition()) == null) {
                    ChessPiece enemyPawn = board.getPiece(new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1));
                    if (enemyPawn != null) {
                        if (
                                enemyPawn.getTeamColor() != ChessGame.TeamColor.BLACK &&
                                        game.getPrevious().equals(previousCheck) &&
                                        enemyPawn.getPieceType() == ChessPiece.PieceType.PAWN
                        ) {
                            isValidMove = game.testMove(passant);
                            if (isValidMove) {
                                passant.setEnPassant();
                                valid.add(passant);
                            }
                        }
                    }
                }
            }

            passant = new ChessMove(startPosition, new ChessPosition((startPosition.getRow()) - 1, (startPosition.getColumn() + 1)), null);
            previousCheck = new ChessMove(
                    new ChessPosition(startPosition.getRow() - 2, startPosition.getColumn() + 1),
                    new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1),
                    null
            );
            // Right En Passant
            if (startPosition.getColumn() != 8) {
                if (board.getPiece(passant.getEndPosition()) == null) {
                    ChessPiece enemyPawn = board.getPiece(new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1));
                    if (enemyPawn != null) {
                        if (
                                enemyPawn.getTeamColor() != ChessGame.TeamColor.BLACK &&
                                        game.getPrevious().equals(previousCheck) &&
                                        enemyPawn.getPieceType() == ChessPiece.PieceType.PAWN
                        ) {
                            isValidMove = game.testMove(passant);
                            if (isValidMove) {
                                passant.setEnPassant();
                                valid.add(passant);
                            }
                        }
                    }
                }
            }
        }
        return valid;
    }
}
