package server;

import dataaccess.*;
import spark.*;

public class Server {
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", Handler::RegisterHandler);
        Spark.delete("/db", Handler::ClearHandler);
        Spark.post("/session", Handler::LoginHandler);
        Spark.delete("/session", Handler::LogoutHandler);
        Spark.post("/game", Handler::CreateHandler);
        Spark.get("/game", Handler::ListHandler);
        Spark.put("/game", Handler::JoinHandler);

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
