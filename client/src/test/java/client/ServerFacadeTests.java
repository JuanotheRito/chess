package client;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serverfacade.GameInfo;
import serverfacade.ResponseException;
import server.Server;
import serverfacade.ServerFacade;
import service.ClearService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static final Logger LOG = LoggerFactory.getLogger(ServerFacadeTests.class);
    private static Server server;
    private final static UserData TEST_USER = new UserData("CosmoCougar", "GoCougs!", "cosmo@byu.edu");
    private static ServerFacade testServer;
    static String username = TEST_USER.username();
    static String password = TEST_USER.password();
    static String email = TEST_USER.email();
    private final static String GAME_NAME = "gg";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        testServer = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() throws DataAccessException {
        server.stop();
    }

    public static void registerSetup(){
        try {
            testServer.register(username, password, email);
            testServer.logout();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public static AuthData loginSetup(){
        registerSetup();
        try {
            return testServer.login(username, password);
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void resetData() throws DataAccessException {
        ClearService.clear();
    }

    @Test
    public void registerSuccess() {
        AuthData result;
        try {
             result = testServer.register(username, password, email);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        AuthData expected = new AuthData(result.authToken(), username);
        assertEquals(expected, result);

    }

    @Test
    public void badRegisterCommand(){
        try {
            testServer.register(username, password, email);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        assertThrows(ResponseException.class, () -> testServer.register(username, password, email));
    }

    @Test
    public void loginSuccess(){
        registerSetup();
        AuthData result;
        try {
            result = testServer.login(username, password);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        AuthData expected = new AuthData(result.authToken(), username);

        assertEquals(expected, result);
    }

    @Test
    public void wrongPassword(){
        registerSetup();

        assertThrows(ResponseException.class, () -> testServer.login(username, "GGGGG"));
    }

    @Test
    public void logoutSuccess(){
        String authToken = loginSetup().authToken();
        AuthDAO authDAO = new SQLAuthDAO();
        try {
            testServer.logout();
            assertNull(authDAO.getAuth(authToken));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void alreadyLoggedOut(){
        assertThrows(ResponseException.class, () -> testServer.logout());
    }

    @Test
    public void createSuccess(){
        loginSetup();
        int actual;
        try{
            actual = testServer.create(GAME_NAME);
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
        int expected = 1;
        assertEquals(expected, actual);
    }

    @Test
    public void nullNameCreateFail(){
        loginSetup();

        assertThrows(NullPointerException.class, () -> testServer.create(null));
    }

    @Test
    public void listSuccess(){
        loginSetup();
        GameInfo testGame;
        List<GameInfo> gameList;
        try{
            testServer.create(GAME_NAME);
            gameList = testServer.list();

            testGame = new GameInfo(1, "gg", null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(testGame, gameList.getFirst());
    }

    @Test
    public void noGames(){
        loginSetup();

        List<GameInfo> gameList;
        try{
            gameList = testServer.list();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        assertThrows(NoSuchElementException.class, gameList::getFirst);
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        loginSetup();
        GameInfo game;
        GameDAO gameDAO = new SQLGameDAO();
        try {
            testServer.create(GAME_NAME);
            testServer.list();
            testServer.join(ChessGame.TeamColor.WHITE, 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        GameInfo expected = new GameInfo(1, "gg", username, null);
        GameData gameData = gameDAO.getGame(1);
        game = new GameInfo(gameData.gameID(), gameData.gameName(), gameData.whiteUsername(), gameData.blackUsername());
        assertEquals(expected, game);
    }

    @Test
    public void alreadyJoined() {
        loginSetup();
        try {
            testServer.create(GAME_NAME);
            testServer.join(ChessGame.TeamColor.WHITE, 1);
        }  catch (Exception e){
            throw new RuntimeException(e);
        }
        assertThrows(ResponseException.class, () -> testServer.join(ChessGame.TeamColor.WHITE, 1));
    }

}
