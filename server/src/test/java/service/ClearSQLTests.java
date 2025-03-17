package service;

import dataaccess.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.RegisterRequest;
import service.UserService;


import static org.junit.jupiter.api.Assertions.*;


public class ClearSQLTests {
    public static RegisterRequest testRegister = new RegisterRequest("CosmoCougar", "GoCougs!", "cosmocougar@byu.edu");
    public String testAuth;

    @BeforeEach
    public void createDatabase() throws DataAccessException{
        DatabaseManager.setupDatabase();
    }

    @BeforeEach
    public void setTestRegister() throws DataAccessException {
       testAuth = UserService.register(testRegister).authToken();
    }

    @AfterEach
    public void clear() throws DataAccessException{
        ClearService.clear();
    }

    @Test
    public void clearUserSuccess() throws DataAccessException {
        UserDAO userDAO = new SQLUserDAO();
        userDAO.clear();
        assertNull(userDAO.getUser(testRegister.username()));
    }

    @Test
    public void clearAuthSuccess() throws DataAccessException{
        AuthDAO authDAO = new SQLAuthDAO();
        authDAO.clear();
        assertNull(authDAO.getAuth(testAuth));
    }

    @Test
    public void clearGameSuccess() throws DataAccessException{
        GameDAO gameDAO = new SQLGameDAO();
        int gameID = gameDAO.createGame("gg").gameID();
        gameDAO.clear();
        assertNull(gameDAO.getGame(gameID));
    }
}
