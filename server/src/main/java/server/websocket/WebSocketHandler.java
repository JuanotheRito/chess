package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server_facade.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException {
        UserGameCommand gameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (gameCommand.getCommandType()){
            case CONNECT -> connect(gameCommand.getAuthToken(), session, gameCommand.getGameID());
            case MAKE_MOVE -> makeMove(gameCommand.getAuthToken(), gameCommand);
            case LEAVE -> leave(gameCommand.getAuthToken());
            case RESIGN -> resign(gameCommand.getAuthToken());
        }
    }

    private void connect(String authToken, Session session, int gameID) throws IOException {
        connections.add(authToken, gameID, session);
        String username = getUsername(authToken);
        var message = String.format("%s has joined the game", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, gameID, notification);
        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, new SQLGameDAO().getGame(gameID).game());
        session.getRemote().sendString(new Gson().toJson(loadGame));
    }

    private void makeMove(String authToken, UserGameCommand command) throws ResponseException, IOException {
        Connection connection = connections.connections.get(authToken);
        String username = getUsername(authToken);
        if (command instanceof MakeMoveCommand moveCommand){
            ChessMove move = moveCommand.move();
            GameData gameData = new SQLGameDAO().getGame(connection.gameID);
            ChessGame game = gameData.game();
            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            try {
                if (username.equals(gameData.whiteUsername())){
                    if (!piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)){
                        throw new InvalidMoveException("Not your piece");
                    }
                }
                if (username.equals(gameData.blackUsername())){
                    if (!piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)){
                        throw new InvalidMoveException("Not your piece");
                    }
                }
                game.makeMove(move);
                new SQLGameDAO().updateGame(new SQLGameDAO().getGame(connection.gameID), game);
                ServerMessage message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                connections.broadcast(null, connection.gameID, message);
                String update = String.format("%s has moved %s to %s", piece.getTeamColor().toString(),
                        piece.getPieceType().toString(),
                        move.getEndPosition().toString()
                );
                message = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, update);
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
            } catch (InvalidMoveException e) {
                throw new ResponseException(400, e.getMessage());
            } catch (DataAccessException e){
                throw new ResponseException (500, "Unable to make move");
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
                throw new ResponseException(500, "Unable to successfully leave game");
            }
        } else if(username.equals(game.whiteUsername())){
            try {
                new SQLGameDAO().joinGameAsColor(game, ChessGame.TeamColor.WHITE, null);
            } catch (DataAccessException e) {
                throw new ResponseException(500, "Unable to successfully leave game");
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

    private String getUsername(String authToken){
        try {
            return new SQLAuthDAO().getAuth(authToken).username();
        } catch (DataAccessException e) {
            return "Unknown";
        }
    }
}
