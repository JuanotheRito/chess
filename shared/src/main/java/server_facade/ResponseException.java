package server_facade;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
    private final int statusCode;

    public ResponseException(int statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson(){
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static ResponseException fromJson(InputStream stream){
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        String message = map.get("message").toString();
        return new ResponseException(400, message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
