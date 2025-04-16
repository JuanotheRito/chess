package serverfacade;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import com.google.gson.*;
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
import java.util.List;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        UserData userData = new UserData(username, password, email);
        AuthData authData = this.makeRequest("POST", path, userData, AuthData.class, false);
        authToken = authData.authToken();
        return authData;
    }

    public AuthData login(String username, String password) throws ResponseException {
        var path = "/session";
        Map<String, String> loginData = Map.of("username", username, "password", password);
        AuthData authData = this.makeRequest("POST", path, loginData, AuthData.class, false);
        authToken = authData.authToken();
        return authData;
    }

    public void logout() throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, true);
    }

    public int create(String name) throws ResponseException {
        var path = "/game";
        Map gameData = Map.of("gameName", name);
        Map result = this.makeRequest("POST", path, gameData, Map.class, true);
        double id = (double) result.get("gameID");
        return (int) id;
    }

    public List<GameInfo> list() throws ResponseException {
        var path = "/game";
        record GameListResponse(List<GameInfo> games) {}
        GameListResponse response;
        response = makeRequest("GET", path, null, GameListResponse.class, true);
        return response.games();
    }

    public void join(ChessGame.TeamColor playerColor, int gameID) throws ResponseException {
        var path = "/game";
        record JoinRequest(ChessGame.TeamColor playerColor, int gameID){}
        JoinRequest request = new JoinRequest(playerColor, gameID);
        makeRequest("PUT", path, request, null, true);
        wsRequest();
    }

    public void wsRequest(){
        var path = "/ws";
    }

    private <T> T makeRequest(String method, String path, Object request, Type responseClass, boolean needsAuth) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (needsAuth) {
                http.addRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
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

    private static <T> T readBody(HttpURLConnection http, Type responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = gameSerializer().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    public static Gson gameSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(GameData.class, (JsonDeserializer<GameData>) (el, type, ctx) -> {
            ChessGame chessGame = new ChessGame();
            GameData gameData = null;
            if (el.isJsonObject()) {
                int gameID;
                JsonObject obj = el.getAsJsonObject();
                String whiteUsername = obj.has("whiteUsername") && !obj.get("whiteUsername").isJsonNull()
                        ? obj.get("whiteUsername").getAsString() : null;
                String blackUsername = obj.has("blackUsername") && !obj.get("blackUsername").isJsonNull()
                        ? obj.get("blackUsername").getAsString() : null;
                String gameName = el.getAsJsonObject().get("gameName").getAsString();
                ChessGame game;
                JsonElement id = el.getAsJsonObject().get("gameID");
                gameID = ctx.deserialize(id, int.class);
                System.out.println("Deserializing Game");
                ChessPiece[][] pieceArray = new ChessPiece[8][8];
                ChessBoard chessBoard = new ChessBoard();
                JsonObject board = (JsonObject) el.getAsJsonObject().get("board");
                JsonArray squares = board.getAsJsonArray("board");
                int rows = 8;
                int columns = 8;
                for (int i = 0; i < rows; i++) {
                    JsonArray row = squares.get(i).getAsJsonArray();
                    for (int j = 0; j < columns; j++) {
                        JsonElement pieceElement = row.get(j);
                        pieceArray[i][j] = ctx.deserialize(pieceElement, ChessPiece.class);
                    }
                }
                chessBoard.board = pieceArray;
                JsonElement chessTurn = el.getAsJsonObject().get("turn");
                ChessGame.TeamColor turn = ctx.deserialize(chessTurn, ChessGame.TeamColor.class);

                ChessMove previous;
                JsonElement previousMove = el.getAsJsonObject().get("previous");
                previous = ctx.deserialize(previousMove, ChessMove.class);
                chessGame.setBoard(chessBoard);
                chessGame.setTeamTurn(turn);
                chessGame.previous = previous;
                gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
            }
            return gameData;
        });
        return gsonBuilder.create();
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public String authToken() {
        return authToken;
    }
}
