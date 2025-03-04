package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import javax.xml.crypto.Data;

public class GameService {
    public static boolean isNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }
    public static GameListResult listGames(GameListRequest listRequest) throws DataAccessException {
        var authToken = listRequest.authToken();

        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null){
            throw new DataAccessException("Error: unauthorized");
        }

        return new GameListResult(gameDAO.getGames());
    }
    public static CreateResult createGame(CreateRequest createRequest) throws DataAccessException {
        var authToken = createRequest.authToken();
        String gameName = createRequest.gameName();

        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        if (isNull(authToken, gameName)){
            throw new EmptyFieldException("Error: bad request");
        }
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null){
            throw new DataAccessException("Error: unauthorized");
        }
        GameData game = gameDAO.createGame(gameName);
        return new CreateResult(game.gameID());
    }
    public static JoinResult joinGame(JoinRequest joinRequest) throws DataAccessException {
        var authToken = joinRequest.authToken();
        int gameID = joinRequest.gameID();
        ChessGame.TeamColor playerColor = joinRequest.playerColor();
        JoinResult result = null;
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        String username = authDAO.getAuth(authToken).username();

        GameData gameData = getGameData(authToken, gameID, playerColor);
        if (playerColor == ChessGame.TeamColor.WHITE){
            if(!isNull(gameData.whiteUsername())){
                throw new AlreadyTakenException("Error: already taken");
            }
        }
        if (playerColor == ChessGame.TeamColor.BLACK){
            if(!isNull(gameData.blackUsername())){
                throw new AlreadyTakenException("Error: already taken");
            }
        }
        gameDAO.joinGameAsColor(gameData, playerColor, username);
        result = new JoinResult(playerColor, gameID);

        return result;
    }

    private static GameData getGameData(String authToken, int gameID, ChessGame.TeamColor playerColor) throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        if (isNull(authToken, gameID, playerColor)){
            throw new EmptyFieldException("Error: bad request");
        }
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null){
            throw new DataAccessException("Error: unauthorized");
        }
        GameData gameData = gameDAO.getGame(gameID);
        if (isNull(gameData)){
            throw new EmptyFieldException("Error: bad request");
        }
        return gameData;
    }
}
