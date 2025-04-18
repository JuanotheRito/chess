package dataaccess;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {

    @Test
    void registerSuccess() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        RegisterRequest test = new RegisterRequest("CosmoCougar", "GoCougs", "cosmocougar@byu.edu");
        RegisterResult result = null;
        try {
            result = UserService.register(test);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        RegisterResult expected = new RegisterResult("CosmoCougar", result.authToken());

        assertEquals(expected, result);
        ClearService.clear();
    }

    @Test
    void registerDuplicate() throws AlreadyTakenException, DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        RegisterRequest test = new RegisterRequest("CosmoCougar", "GoCougs", "cosmocougar@byu.edu");
        RegisterResult result = null;
        try {
            result = UserService.register(test);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertThrows(AlreadyTakenException.class, () -> UserService.register(test));
        ClearService.clear();
    }

    @Test
    void badRegisterRequest() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        RegisterRequest test = new RegisterRequest("CosmoCougar", null, "cosmocougar@byu.edu");
        RegisterResult result = null;
        assertThrows(EmptyFieldException.class, () -> UserService.register(test));
        ClearService.clear();
    }

    @Test
    void loginSuccessful() throws DataAccessException {
        try {
            UserService.register(new RegisterRequest("CosmoCougar", "GoCougars!", "cosmo@byu.edu"));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        LoginResult actual = null;
        LoginRequest test = new LoginRequest("CosmoCougar", "GoCougars!");
        try {
            actual = UserService.login(test);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        LoginResult expected = new LoginResult("CosmoCougar", actual.authToken());

        assertEquals(expected, actual);
        ClearService.clear();
    }
    @Test
    void incorrectPassword() throws DataAccessException {
        try {
            UserService.register(new RegisterRequest("CosmoCougar", "GoCougars!", "cosmo@byu.edu"));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        LoginResult actual = null;
        LoginRequest test = new LoginRequest("CosmoCougar", "GoCougas!");
        assertThrows(DataAccessException.class, () -> UserService.login(test));
        ClearService.clear();
    }
    @Test
    void notRegistered() throws DataAccessException {
        LoginResult actual = null;
        LoginRequest test = new LoginRequest("CosmoCougar", "GoCougars!");
        assertThrows(DataAccessException.class, () -> UserService.login(test));
        ClearService.clear();
    }

    @Test
    void logoutSuccessful() throws DataAccessException {
        String authToken = null;
        RegisterResult register = null;
        try {
             register = UserService.register(new RegisterRequest("CosmoCougar", "GoCougars!", "cosmo@byu.edu"));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        authToken = register.authToken();
        try {
            UserService.logout(new LogoutRequest(authToken));
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        LoginRequest login = new LoginRequest("CosmoCougar", "GoCougars!");
        try {
            authToken = UserService.login(login).authToken();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        LogoutRequest test = new LogoutRequest(authToken);
        LogoutResult expected = new LogoutResult(true);
        LogoutResult actual = null;
        try{
            actual = UserService.logout(test);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        assertEquals(actual, expected);
        assertEquals(0, MemoryDatabase.getAuthData().size());
        ClearService.clear();
    }

    @Test
    void notLoggedIn() throws DataAccessException {
        String authToken = null;
        RegisterResult register = null;
        try {
            register = UserService.register(new RegisterRequest("CosmoCougar", "GoCougars!", "cosmo@byu.edu"));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        authToken = register.authToken();
        try{
            UserService.logout(new LogoutRequest(authToken));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        LogoutRequest test = new LogoutRequest(authToken);
        assertThrows(DataAccessException.class, () -> UserService.logout(test));
        ClearService.clear();
    }
}