package client;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static UserData testUser = new UserData("CosmoCougar", "GoCougs!", "cosmo@byu.edu");
    private static ServerFacade testServer;
    static String username = testUser.username();
    static String password = testUser.password();
    static String email = testUser.email();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        testServer = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() throws DataAccessException {
        ClearService.clear();
        server.stop();
    }

    public static void registerSetup(){
        try {
            String authToken = testServer.register(username, password, email).authToken();
            testServer.logout(authToken);
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
            testServer.logout(authToken);
            assertNull(authDAO.getAuth(authToken));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void alreadyLoggedOut(){
        String authToken = loginSetup().authToken();
        testServer.logout(authToken);
        assertThrows(ResponseException.class, () -> testServer.logout(authToken));
    }

}
