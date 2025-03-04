package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Test;

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
        CreateResult expected = new CreateResult("GG");
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
    void invalidAuthorization(){
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
        CreateRequest test = new CreateRequest(1, "GG");
        assertThrows(DataAccessException.class, () -> GameService.createGame(test));

    }
    }
}