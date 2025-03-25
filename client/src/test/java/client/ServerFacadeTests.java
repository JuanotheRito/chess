package client;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static UserData testUser = new UserData("CosmoCougar", "GoCougs!", "cosmo@byu.edu");
    private static ServerFacade testServer;
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

    public static void registerSetup() {

    }


    @Test
    public void registerSuccess() {
        String username = testUser.username();
        String password = testUser.password();
        String email = testUser.email();
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
        String username = testUser.username();
        String password = testUser.password();
        String email = testUser.email();

        try {
            testServer.register(username, password, email);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        assertThrows(ResponseException.class, () -> testServer.register(username, password, email));
    }

}
