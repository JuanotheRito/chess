package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand gameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (gameCommand.getCommandType()){
            case CONNECT -> connect(gameCommand.getAuthToken(), session, gameCommand.getGameID());
            case MAKE_MOVE -> makeMove(gameCommand.getAuthToken());
            case LEAVE -> leave(gameCommand.getAuthToken());
            case RESIGN -> resign(gameCommand.getAuthToken());
        }
    }

    private void connect(String authToken, Session session, int gameID){

    }
}
