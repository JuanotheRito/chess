package server.websocket;

import chess.ChessGame;
import dataaccess.SQLGameDAO;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public int gameID;
    public Session session;
    public ChessGame currentGame;

    public Connection(String authToken, int gameID, Session session) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
