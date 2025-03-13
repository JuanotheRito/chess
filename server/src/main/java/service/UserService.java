package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    public static boolean isNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    public static RegisterResult register(RegisterRequest registerRequest) throws DataAccessException, AlreadyTakenException {
        var username = registerRequest.username();
        var password = registerRequest.password();
        var email = registerRequest.email();

        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();

        if (isNull(username, password, email)) {
            throw new EmptyFieldException("Error: bad request");
        }

        RegisterResult result = null;
        UserData user = userDAO.getUser(username);

        if (user == null) {
            user = new UserData(username, password, email);
            userDAO.createUser(user);
            AuthData authData = new AuthData(generateToken(), username);
            authDAO.createAuth(authData);
            result = new RegisterResult(authData.username(), authData.authToken());
        } else {
            throw new AlreadyTakenException("Error: already taken");
        }
        return result;
    }

    public static LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        var username = loginRequest.username();
        var password = loginRequest.password();

        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();

        LoginResult result = null;
        UserData user = userDAO.getUser(username);
        if (user == null){
            throw new DataAccessException("Error: unauthorized");
        }
        if (!verifyUser(password, user.password())){
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData authData = new AuthData(generateToken(), username);
        authDAO.createAuth(authData);
        result = new LoginResult(username, authData.authToken());
        return result;
    }

    static boolean verifyUser(String password, String hashedPw){
        return BCrypt.checkpw(password, hashedPw);
    }

    public static LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException{
        var authToken = logoutRequest.authToken();

        AuthDAO authDAO = new MemoryAuthDAO();

        LogoutResult result = new LogoutResult(false);
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(authData);
        result = new LogoutResult(true);
        return result;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
