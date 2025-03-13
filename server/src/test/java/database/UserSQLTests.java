package database;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

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
        UserData testUser = new UserData(username, actual.password(), email);
        AuthData testAuth = new AuthData(result.authToken(), username);
        AuthData actualAuth = authDAO.getAuth(result.authToken());

        assertTrue(BCrypt.checkpw(password, actual.password()));
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

    @Test
    void loginSuccessful() throws DataAccessException {
        try {
            UserService.register(testRegister);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        LoginResult actual = null;
        try {
            actual = UserService.login(testLogin);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        LoginResult expected = new LoginResult("CosmoCougar", actual.authToken());

        assertEquals(expected, actual);
    }
    @Test
    void incorrectPassword(){
        try {
            UserService.register(testRegister);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        LoginRequest test = new LoginRequest("CosmoCougar", "GoCougas!");
        assertThrows(DataAccessException.class, () -> UserService.login(test));
    }
    @Test
    void notRegistered(){
        assertThrows(DataAccessException.class, () -> UserService.login(testLogin));
    }

}
