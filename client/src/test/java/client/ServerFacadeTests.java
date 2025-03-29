package client;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import service.ClearService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static final Logger log = LoggerFactory.getLogger(ServerFacadeTests.class);
    private static Server server;
    private final static UserData testUser = new UserData("CosmoCougar", "GoCougs!", "cosmo@byu.edu");
    private static ServerFacade testServer;
    static String username = testUser.username();
    static String password = testUser.password();
    static String email = testUser.email();
    private final static String gameName = "gg";

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
            actual = testServer.create(gameName);
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
        GameData testGame;
        GameDAO gameDAO = new SQLGameDAO();
        List<GameData> gameList;
        try{
            testServer.create(gameName);
            gameList = testServer.list();

            testGame = gameDAO.getGame(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(testGame, gameList.getFirst());
    }

    @Test
    public void noGames(){
        loginSetup();

        List<GameData> gameList;
        try{
            gameList = testServer.list();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        assertNull(gameList.getFirst());
    }

}
