package server;

import dataaccess.DataAccessException;
import jdk.jshell.spi.ExecutionControl;
import model.AuthData;
import service.RegisterRequest;
import service.UserService;
import spark.*;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //Spark.delete("/db",(req, res) -> ClearHandler(req, res));
        Spark.post("/user", ((req, res) -> RegisterHandler(req, res)));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    /*(public Object ClearHandler(Request req, Response res){
        ClearService service = new ClearService();
        try{
            ClearService.clear();
        }
        catch (DataAccessException e){

        }
        return "{}";
    }*/
    public Object RegisterHandler(Request req, Response res){
        var serializer = new Gson();

        var body = req.body();
        var newRegister = serializer.fromJson(body, RegisterRequest.class);
        Object result;
        var service = new UserService();
        try {
            result = service.register(newRegister);
        }
       // catch (DataAccessException e){

        /*}*/ catch (ExecutionControl.NotImplementedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
