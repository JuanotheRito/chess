package database;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserSQLTests {
    public static String username = "CosmoCougar";
    public static String password = "GoCougs!";
    public static String email = "cosmocougar@byu.edu";
    public static RegisterRequest testRegister = new RegisterRequest(username, password, email);
    public static LoginRequest testLogin = new LoginRequest(username, password);

    @BeforeAll
    public static void setupDatabase() throws DataAccessException {
        DatabaseManager.setupDatabase();
    }

    @AfterEach
    public void reset() throws DataAccessException {
        ClearService.clear();
    }

    @Test
    public void registerSuccess() throws DataAccessException{
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();

        RegisterResult result = UserService.register(testRegister);
        UserData actual = userDAO.getUser(username);
        UserData testUser = new UserData(username, password, email);

        AuthData testAuth = new AuthData(username, result.authToken());
        AuthData actualAuth = authDAO.getAuth(result.authToken());

        assertEquals(testUser, actual);
        assertEquals(testAuth, actualAuth);
    }

    @Test
    public void duplicateRegister() {
        try {
            UserService.register(testRegister);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertThrows(AlreadyTakenException.class, () -> UserService.register(testRegister));
    }

    @Test
    void badRegisterRequest() throws DataAccessException {
        RegisterRequest test = new RegisterRequest("CosmoCougar", null, "cosmocougar@byu.edu");
        assertThrows(EmptyFieldException.class, () -> UserService.register(test));
    }

}
