package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import service.ClearService;
import service.RegisterRequest;
import service.UserService;
import spark.Request;
import spark.Response;

public class Handler {
    public static Object ClearHandler(Request req, Response res) {
        var serializer = new Gson();
        Object result  = null;
        try {
            ClearService.clear();
        } catch (Exception e) {
            result = String.format("Error: %s", e.getMessage());
            res.status(500);
        }
        return serializer.toJson(result);
    }

    public static Object RegisterHandler(Request req, Response res) {
        var serializer = new Gson();
        Object result = null;
        record ErrorMessage(String message){}
        try {
            var body = req.body();
            var newRegister = serializer.fromJson(body, RegisterRequest.class);
            result = UserService.register(newRegister);
        } catch (DataAccessException e) {
            result = new ErrorMessage(e.getMessage());
            res.status(400);
        } catch (AlreadyTakenException e) {
            result = new ErrorMessage(e.getMessage());
            res.status(403);
        } catch (Exception e) {
            result = new ErrorMessage(e.getMessage());
            res.status(500);
        }
        return serializer.toJson(result);
    }
}
