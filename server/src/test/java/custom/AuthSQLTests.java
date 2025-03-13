package custom;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class AuthSQLTests {
    static AuthData testAuth = new AuthData(UserService.generateToken(), "CosmoCougar");
    static AuthDAO authDAO = new SQLAuthDAO();
    @BeforeAll
    static void initiateDatabase() throws DataAccessException {
        DatabaseManager.setupDatabase();
    }

    @AfterEach
    void clear() throws DataAccessException{
        ClearService.clear();
    }

    @Test
    void createSuccess() throws DataAccessException {
        authDAO.createAuth(testAuth);
        assertEquals(testAuth, authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    void alreadyExists() throws DataAccessException{
        authDAO.createAuth(testAuth);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(testAuth));
    }

    @Test
    void nullAuthToken(){
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(new AuthData(null, "CosmoCougar")));
    }

    @Test
    void getAuthSuccess() throws DataAccessException{
        authDAO.createAuth(testAuth);
        var actual = authDAO.getAuth(testAuth.authToken());
        assertEquals(testAuth, actual);
    }

    @Test
    void authDoesNotExist() throws DataAccessException {
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    void authDeleteSuccess() throws DataAccessException{
        authDAO.createAuth(testAuth);
        authDAO.deleteAuth(testAuth);
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    void deleteTargetDoesNotExist(){
        assertThrows(DataAccessException.class, ()-> authDAO.deleteAuth(testAuth));
    }
}
