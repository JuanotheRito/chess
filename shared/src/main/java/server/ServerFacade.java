package server;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public AuthData register(String username, String password, String email) throws ResponseException{
        var path = "/user";
        UserData userData = new UserData(username, password, email);
        AuthData authData = this.makeRequest("POST", path, userData, AuthData.class, false);
        authToken = authData.authToken();
        return authData;
    }

    public AuthData login(String username, String password) throws ResponseException{
        var path = "/session";
        Map<String, String> loginData = Map.of("username", username, "password", password);
        AuthData authData = this.makeRequest("POST", path, loginData, AuthData.class, false);
        authToken = authData.authToken();
        return authData;
    }

    public void logout() throws ResponseException{
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, true);
    }

    public int create(String name) throws ResponseException {
        var path = "/game";
        Map gameData = Map.of("gameName", name);
        Map result = this.makeRequest("POST", path, gameData, Map.class, true);
        double ID = (double)result.get("gameID");
        return (int)ID;
    }

    public List<GameData> list() throws ResponseException {
        var path = "/game";
        record GameListResponse(List<GameData> games) {};
        GameListResponse response;
        response = makeRequest("GET", path, null, GameListResponse.class, true);
        return response.games();
    }

    private <T> T makeRequest(String method, String path, Object request, Type responseClass, boolean needsAuth) throws ResponseException {
        try{
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (needsAuth){
                http.addRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request!=null){
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException{
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody (HttpURLConnection http, Type responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0){
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    public static Gson gameSerializer(){
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(ChessGame.class, (JsonDeserializer<ChessGame>) (el, type, ctx) -> {
             ChessGame chessGame = null;
             if (el.isJsonObject()) {
                 JsonObject board = (JsonObject) el.getAsJsonObject().get("board");
                 JsonArray squares = board.getAsJsonArray("board");
                 ChessPiece piece;  
             }
        });
    }

    private boolean isSuccessful(int status){
        return status / 100 == 2;
    }
}
