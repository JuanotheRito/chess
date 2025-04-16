package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server_facade.ResponseException;
import websocket.commands.JoinCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand gameCommand = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (gameCommand.getCommandType()) {
                case CONNECT -> connect(gameCommand, session);
                case MAKE_MOVE -> makeMove(gameCommand.getAuthToken(), gameCommand);
                case LEAVE -> leave(gameCommand.getAuthToken());
                case RESIGN -> resign(gameCommand.getAuthToken());
            }
        } catch (ResponseException e){
            String authToken = gameCommand.getAuthToken();
            String update = "Error: " + e.getMessage();
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, update);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void connect(UserGameCommand gameCommand, Session session) throws IOException, ResponseException {
        String authToken = gameCommand.getAuthToken();
        int gameID = gameCommand.getGameID();
        connections.add(authToken, gameID, session);
        String username = getUsername(authToken);
        String message;
        GameData game = new SQLGameDAO().getGame(gameID);
        LoadGameMessage loadGame;
        if(!(game == null)){
            ChessGame chessGame = game.game();
            loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
            session.getRemote().sendString(new Gson().toJson(loadGame));
        }
        else {
            throw new ResponseException(400, "That game does not exist");
        }
        if (gameCommand instanceof JoinCommand){
            ChessGame.TeamColor teamColor = ((JoinCommand) gameCommand).teamColor;
            message = String.format("%s has joined the game as the color %s", username, teamColor.toString());
        }
        else {
            message = String.format("%s is now observing the game", username);
        }
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, gameID, notification);
    }

    private void makeMove(String authToken, UserGameCommand command) throws ResponseException, IOException {
        Connection connection = connections.connections.get(authToken);
        String username = getUsername(authToken);
        ChessMove move = command.getMove();
        if (!(move == null)){
            GameData gameData = new SQLGameDAO().getGame(connection.gameID);
            ChessGame game = connection.currentGame;
            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            try {
                if (piece == null) {
                    throw new InvalidMoveException("No piece at given position");
                }
                if (username.equals(gameData.whiteUsername())){
                    if (!piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)){
                        throw new InvalidMoveException("Not your piece");
                    }
                }
                else if (username.equals(gameData.blackUsername())){
                    if (!piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)){
                        throw new InvalidMoveException("Not your piece");
                    }
                } else {
                    throw new InvalidMoveException("You aren't playing, you cannot move pieces");
                }
                if (game.gameOver){
                    throw new ResponseException(400, "Game is over");
                }
                else {
                    try {
                        game.makeMove(move);
                        connection.currentGame = game;
                    } catch (InvalidMoveException e) {
                        System.out.println(">> MAKE MOVE FAILED");
                        System.out.println("Move: " + move);
                        System.out.println("Start: " + move.getStartPosition());
                        System.out.println("End: " + move.getEndPosition());
                        System.out.println("Start Piece: " + game.getBoard().getPiece(move.getStartPosition()));
                        System.out.println("Turn: " + game.getTeamTurn());
                        throw e; // rethrow so test still fails
                    }
                }

                boolean isCheckmate = game.isInCheckmate(game.getTeamTurn());
                boolean isStalemate = game.isInStalemate(game.getTeamTurn());

                if (isCheckmate || isStalemate){
                    game.gameOver = true;
                }

                new SQLGameDAO().updateGame(gameData, game);
                String update = String.format("%s has moved %s to %s", piece.getTeamColor().toString(),
                        piece.getPieceType().toString(),
                        move.getEndPosition().toString()
                );
                ServerMessage message = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, update);
                connections.broadcast(authToken, connection.gameID, message);
                if (game.isInCheck(game.getTeamTurn())){
                    update = String.format("%s is in check!", game.getTeamTurn().toString());
                    message = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, update);
                    connections.broadcast(null, connection.gameID, message);
                }
                if (game.isInCheckmate(game.getTeamTurn())){
                    update = String.format("%s is in checkmate! Game over! %s wins!",
                            game.getTeamTurn().toString(),
                            piece.getTeamColor().toString()
                    );
                    message = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, update);
                    connections.broadcast(null, connection.gameID, message);
                }
                if (game.isInStalemate(game.getTeamTurn())){
                    update = String.format("%s is in stalemate! Game over! The game ends in a draw!",
                            game.getTeamTurn().toString()
                    );
                    message = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, update);
                    connections.broadcast(null, connection.gameID, message);
                }
                message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                connections.broadcast(null, connection.gameID, message);
            } catch (InvalidMoveException e) {
                throw new ResponseException(400, e.getMessage());
            } catch (DataAccessException e){
                throw new ResponseException (500, "Unable to make move for unknown reason");
            }
        } else {
            throw new ResponseException(500, "Received bad command");
        }


    }

    private void leave(String authToken) throws IOException, ResponseException {
        int gameID = connections.connections.get(authToken).gameID;
        String username = getUsername(authToken);
        GameData game = new SQLGameDAO().getGame(gameID);
        if(username.equals(game.blackUsername())){
            try {
                new SQLGameDAO().joinGameAsColor(game, ChessGame.TeamColor.BLACK, null);
            } catch (DataAccessException e) {
                throw new ResponseException(500, "Unable to successfully leave game for unknown reason");
            }
        } else if(username.equals(game.whiteUsername())){
            try {
                new SQLGameDAO().joinGameAsColor(game, ChessGame.TeamColor.WHITE, null);
            } catch (DataAccessException e) {
                throw new ResponseException(500, "Unable to successfully leave game for unknown reason");
            }
        }
        connections.remove(authToken);
        var message = String.format("%s has left the game", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, gameID, notification);
    }

    private void resign(String authToken) throws IOException, ResponseException {
        Connection connection = connections.connections.get(authToken);
        int gameID = connection.gameID;
        String username = getUsername(authToken);
        GameData gameData = new SQLGameDAO().getGame(connection.gameID);
        if (!Objects.equals(username, gameData.blackUsername()) || !Objects.equals(username, gameData.whiteUsername())){
            throw new ResponseException(400, "You are observing, you cannot resign");
        }
        ChessGame game = gameData.game();
        game.gameOver = true;
        try {
            new SQLGameDAO().updateGame(gameData, game);
        } catch (DataAccessException e) {
            String error = "Unable to end game because" + e.getMessage();
            throw new ResponseException(500, error);
        }
        String update = String.format("%s has resigned.",
                username
        );
        ServerMessage message = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, update);
        connections.broadcast(null, gameID, message);
    }

    private String getUsername(String authToken) throws ResponseException {
        try {
            AuthData authData = new SQLAuthDAO().getAuth(authToken);
            if (!(authData == null)){
                return authData.username();
            }
            else {
                throw new DataAccessException("Cannot find specified authData");
            }
        } catch (DataAccessException e) {
            throw new ResponseException(400, "not authorized");
        }
    }
}
