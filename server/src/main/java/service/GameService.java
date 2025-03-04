package service;

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
}
