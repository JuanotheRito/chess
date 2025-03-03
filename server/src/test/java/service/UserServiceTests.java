package service;

import jdk.jshell.spi.ExecutionControl;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class UserServiceTests {

    @Test
    void registerSuccess() throws ExecutionControl.NotImplementedException {
        RegisterRequest test = new RegisterRequest("CosmoCougar", "GoCougs", "cosmocougar@byu.edu");
        UserService testService = new UserService();
        RegisterResult result = UserService.register(test);
        RegisterResult expected = new RegisterResult("CosmoCougar", result.authToken());

        assertEquals(expected, result);
    }
}