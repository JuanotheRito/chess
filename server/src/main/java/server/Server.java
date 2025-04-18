package server;

import dataaccess.*;
import spark.*;
import server.websocket.WebSocketHandler;

public class Server {
    private final WebSocketHandler webSocketHandler = new WebSocketHandler();
    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        try {
            DatabaseManager.setupDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        Spark.webSocket("/ws", webSocketHandler);
        // Register your endpoints and handle exceptions here.

        Spark.post("/user", Handler::registerHandler);
        Spark.delete("/db", Handler::clearHandler);
        Spark.post("/session", Handler::loginHandler);
        Spark.delete("/session", Handler::logoutHandler);
        Spark.post("/game", Handler::createHandler);
        Spark.get("/game", Handler::listHandler);
        Spark.put("/game", Handler::joinHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
