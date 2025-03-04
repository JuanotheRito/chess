package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.EmptyFieldException;
import model.GameData;
import service.*;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class Handler {
    public static Object clearHandler(Request req, Response res) {
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

    public static Object registerHandler(Request req, Response res) {
        var serializer = new Gson();
        Object result;
        record ErrorMessage(String message){}
        try {
            var body = req.body();
            var newRegister = serializer.fromJson(body, RegisterRequest.class);
            result = UserService.register(newRegister);
        } catch (DataAccessException | EmptyFieldException e) {
            result = new ErrorMessage(e.getMessage());
            res.status(400);
        } catch (AlreadyTakenException e) {
            result = new ErrorMessage(e.getMessage());
            res.status(403);
        }
        return serializer.toJson(result);
    }

    public static Object loginHandler(Request req, Response res){
        var serializer = new Gson();
        Object result;
        record ErrorMessage(String message){}
        try{
            var body = req.body();
            var newLogin = serializer.fromJson(body, LoginRequest.class);
            result = UserService.login(newLogin);
        } catch (DataAccessException e) {
            result = new ErrorMessage((e.getMessage()));
            res.status(401);
        }
        catch (Exception e){
            result = new ErrorMessage((e.getMessage()));
            res.status(500);
        }
        return serializer.toJson(result);
    }

    public static Object logoutHandler(Request req, Response res){
        var serializer = new Gson();
        Object result;
        record ErrorMessage(String message){}
        try{
            var header = req.headers("Authorization");
            var newLogout = new LogoutRequest(header);
            result = UserService.logout(newLogout);
        } catch (DataAccessException e){
            result = new ErrorMessage((e.getMessage()));
            res.status(401);
        }
        return serializer.toJson(result);
    }

    public static Object createHandler(Request req, Response res){
        var serializer = new Gson();
        Object result;
        record ErrorMessage(String message){}
        record GameName(String gameName){}
        try{
            var header = req.headers("Authorization");
            GameName body = serializer.fromJson(req.body(), GameName.class);
            var newCreate = new CreateRequest(header, body.gameName);
            result = GameService.createGame(newCreate);
        } catch (DataAccessException e){
            result = new ErrorMessage((e.getMessage()));
            res.status(401);
        } catch (EmptyFieldException e){
            result = new ErrorMessage((e.getMessage()));
            res.status(400);
        } catch (Exception e){
            result = new ErrorMessage((e.getMessage()));
            res.status(500);
        }
        return serializer.toJson(result);
    }

    public static Object listHandler(Request req, Response res){
        var serializer = new Gson();
        Object result;
        record ErrorMessage(String message){}
        record GameInfo(int gameID, String whiteUsername, String blackUsername, String gameName){}
        record GameInfoList(ArrayList<GameInfo> games){}
        ArrayList<GameInfo> infoList= new ArrayList<>();
        try{
            var header = req.headers("Authorization");
            var newList = new GameListRequest(header);
            result = GameService.listGames(newList);
            for (GameData gameData: ((GameListResult) result).games()){
                infoList.add(new GameInfo(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName()));
            }
        } catch (DataAccessException e){
            result = new ErrorMessage((e.getMessage()));
            res.status(401);
            return serializer.toJson(result);
        } catch (Exception e){
            result = new ErrorMessage((e.getMessage()));
            res.status(500);
            return serializer.toJson(result);
        }
        return serializer.toJson(new GameInfoList(infoList));
    }

    public static Object joinHandler(Request req, Response res){
        var serializer = new Gson();
        Object result = null;
        record ErrorMessage(String message){}
        record JoinInfo(ChessGame.TeamColor playerColor, int gameID){}
        try {
            var header = req.headers("Authorization");
            var body = serializer.fromJson(req.body(), JoinInfo.class);
            var newJoin = new JoinRequest(header, body.playerColor, body.gameID);
            GameService.joinGame(newJoin);
        } catch (DataAccessException e){
            result = new ErrorMessage((e.getMessage()));
            res.status(401);
        } catch (EmptyFieldException e){
            result = new ErrorMessage ((e.getMessage()));
            res.status(400);
        } catch (AlreadyTakenException e){
            result = new ErrorMessage ((e.getMessage()));
            res.status(403);
        } catch (Exception e){
            result = new ErrorMessage ((e.getMessage()));
            res.status(500);
        }
        return serializer.toJson(result);
    }
}
