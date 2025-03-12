package database;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLUserDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.RegisterRequest;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ClearSQLTests {
    public static RegisterRequest testRegister = new RegisterRequest("CosmoCougar", "GoCougs!", "cosmocougar@byu.edu");
    @BeforeEach
    public void createDatabase() throws DataAccessException{
        DatabaseManager.setupDatabase();
    }

    @Test
    public void clearSuccess() throws DataAccessException{
        UserDAO userDAO = new SQLUserDAO();
        UserService.register(testRegister);
        ClearService.clear();
        assertThrows(DataAccessException.class, () -> userDAO.getUser(testRegister.username()));
    }
}
