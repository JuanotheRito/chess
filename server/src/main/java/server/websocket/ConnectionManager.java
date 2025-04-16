package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, int gameID, Session session){
        var connection = new Connection(authToken, gameID, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String exclude, int gameID, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        if (exclude == null){
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (c.gameID == gameID) {
                        c.send(new Gson().toJson(message));
                        if (message instanceof LoadGameMessage loadGameMessage){
                            c.currentGame = loadGameMessage.getGame();
                        }
                    }
                } else {
                    removeList.add(c);
                }
            }
        }else {
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (!c.authToken.equals(exclude) && c.gameID == gameID) {
                        c.send(new Gson().toJson(message));
                        if (message instanceof LoadGameMessage loadGameMessage){
                            c.currentGame = loadGameMessage.getGame();
                        }
                    }
                } else {
                    removeList.add(c);
                }
            }
        }

        for (var c : removeList){
            connections.remove(c.authToken);
        }
    }
}
