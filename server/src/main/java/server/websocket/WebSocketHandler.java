package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
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
        String username;

        try {
            username = new SQLAuthDAO().getAuth(authToken).username();
        } catch (DataAccessException e) {
            username = "Unknown";
        }

        var message = String.format("%s has joined the game", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, gameID, notification);
        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, new SQLGameDAO().getGame(gameID).game());
        session.getRemote().sendString(new Gson().toJson(loadGame));
    }

    private void makeMove(String authToken, UserGameCommand command) throws ResponseException, IOException {
        Connection connection = connections.connections.get(authToken);
        String username;

        try {
            username = new SQLAuthDAO().getAuth(authToken).username();
        } catch (DataAccessException e) {
            username = "Unknown";
        }

        if (command instanceof MakeMoveCommand moveCommand){
            ChessMove move = moveCommand.move();
            ChessGame game = new SQLGameDAO().getGame(connection.gameID).game();
            try {
                game.makeMove(move);
                new SQLGameDAO().updateGame(new SQLGameDAO().getGame(connection.gameID), game);
                LoadGameMessage message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                connections.broadcast(null, connection.gameID, message);
            } catch (InvalidMoveException e) {
                throw new ResponseException(400, "That move is illegal");
            } catch (DataAccessException e){
                throw new ResponseException (500, "Unable to make move");
            }
        } else {
            throw new ResponseException(500, "Received bad command");
        }


    }

    private void leave(String authToken) throws IOException, ResponseException {
        int gameID = connections.connections.get(authToken).gameID;
        String username;

        try {
            username = new SQLAuthDAO().getAuth(authToken).username();
        } catch (DataAccessException e) {
            username = "Unknown";
        }
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
}
