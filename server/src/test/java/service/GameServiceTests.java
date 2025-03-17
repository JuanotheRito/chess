package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.EmptyFieldException;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import service.*;

import java.util.ArrayList;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests {

    @AfterEach
    void clear() throws DataAccessException {
        ClearService.clear();
    }

    @Test
    void createGameSuccess() throws DataAccessException {
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
    }

    @Test
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
        GameListResult actual = null;
        try{
            actual = GameService.listGames(test);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        expectedList.add(new GameData(1, null, null, "GG", actual.games().getFirst().game()));
        GameListResult expected = new GameListResult(expectedList);
        assertEquals(expected, actual);
    }

    @Test
    void joinGameSuccessfully(){
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
        String authToken = loginResult.authToken();
        CreateRequest create = new CreateRequest(authToken, "GG");
        try {
            GameService.createGame(create);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        JoinRequest test = new JoinRequest(authToken, WHITE, 1);
        JoinResult actual = null;
        JoinResult expected = new JoinResult(WHITE, 1);
        try{
           actual=GameService.joinGame(test);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        assertEquals(expected, actual);
    }
    @Test
    void colorAlreadyTaken(){
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
        String authToken = loginResult.authToken();
        CreateRequest create = new CreateRequest(authToken, "GG");
        try {
            GameService.createGame(create);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        JoinRequest test = new JoinRequest(authToken, WHITE, 1);
        try{
            GameService.joinGame(test);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        assertThrows(AlreadyTakenException.class, () -> GameService.joinGame(test));
    }
    @Test
    void invalidGame(){
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
        String authToken = loginResult.authToken();
        CreateRequest create = new CreateRequest(authToken, "GG");
        try {
            GameService.createGame(create);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        JoinRequest first = new JoinRequest(authToken, WHITE, 1);
        try{
            GameService.joinGame(first);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        JoinRequest test = new JoinRequest(authToken, BLACK, 2);
        assertThrows(EmptyFieldException.class, () -> GameService.joinGame(test));
    }
}
