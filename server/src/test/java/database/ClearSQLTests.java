package database;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.RegisterRequest;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;


public class ClearSQLTests {
    public static RegisterRequest testRegister = new RegisterRequest("CosmoCougar", "GoCougs!", "cosmocougar@byu.edu");
    @BeforeEach
    public void createDatabase() throws DataAccessException{
        DatabaseManager.setupDatabase();
    }

    @Test
    public void clearSuccess() throws DataAccessException{
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();

        String testToken = UserService.register(testRegister).authToken();
        ClearService.clear();

        assertNull(userDAO.getUser(testRegister.username()));
        assertNull(authDAO.getAuth(testToken));
        assertTrue(gameDAO.getGames().isEmpty());
    }
}
