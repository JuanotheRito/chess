package server;

import dataaccess.*;
import jdk.jshell.spi.ExecutionControl;
import model.AuthData;
import service.ClearService;
import service.RegisterRequest;
import service.UserService;
import spark.*;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Server {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //Spark.delete("/db",(req, res) -> ClearHandler(req, res));
        Spark.post("/user", (this::RegisterHandler));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object ClearHandler(Request req, Response res){
        try{
            ClearService.clear(userDAO, gameDAO, authDAO);
        }
        catch (DataAccessException e){

        }
        return "{}";
    }
    public Object RegisterHandler(Request req, Response res) {
        var serializer = new Gson();
        Object result = null;
        try {
            var body = req.body();
            var newRegister = serializer.fromJson(body, RegisterRequest.class);
            result = UserService.register(newRegister, userDAO, authDAO);
        }
        catch (DataAccessException e){
            result = String.format("Error: %s", e.getMessage());
            res.status(400);
        }
        catch (AlreadyTakenException e){
            result = String.format("Error: %s", e.getMessage());
            res.status(403);
        }
        catch (Exception e){
            result = String.format("Error: %s", e.getMessage());
            res.status(500);
        }
        result = serializer.toJson(result);
        return result;
    }
}
