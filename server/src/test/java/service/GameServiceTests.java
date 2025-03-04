package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests {
    @Test
    void createGameSuccess(){
        try {
            UserService.register(new RegisterRequest("CosmoCougar", "GoCougars!", "cosmo@byu.edu"));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        LoginResult loginResult = null;
        LoginRequest login = new LoginRequest("CosmoCougar", "GoCougars!");
        try {
            loginResult = UserService.login(login);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        CreateRequest test = new CreateRequest(loginResult.authToken(), "GG");
        CreateResult expected = new CreateResult(1);
        CreateResult actual = null;
        try {
            actual = GameService.createGame(test);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        assertEquals(expected, actual);
        ClearService.clear();
    }
    @Test
    void invalidCreateAuthorization(){
        try {
            UserService.register(new RegisterRequest("CosmoCougar", "GoCougars!", "cosmo@byu.edu"));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        LoginResult loginResult = null;
        LoginRequest login = new LoginRequest("CosmoCougar", "GoCougars!");
        try {
            loginResult = UserService.login(login);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        CreateRequest test = new CreateRequest("1", "GG");
        assertThrows(DataAccessException.class, () -> GameService.createGame(test));
        ClearService.clear();
    }

    void listGamesSuccessfully(){
        try {
            UserService.register(new RegisterRequest("CosmoCougar", "GoCougars!", "cosmo@byu.edu"));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        LoginResult loginResult = null;
        LoginRequest login = new LoginRequest("CosmoCougar", "GoCougars!");
        try {
            loginResult = UserService.login(login);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        CreateRequest create = new CreateRequest(loginResult.authToken(), "GG");
        try {
            GameService.createGame(create);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        GameListRequest test = new GameListRequest(loginResult.authToken());
        ArrayList<GameData> expectedList = new ArrayList<>();
        expectedList.add(new GameData(1, null, null, "GG", new ChessGame()));
        GameListResult expected = new GameListResult(expectedList);
        GameListResult actual = null;
        try{
            actual = GameService.listGames(test);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        assertEquals(expected, actual);
        ClearService.clear();
    }
}
