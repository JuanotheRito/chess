package service;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import jdk.jshell.spi.ExecutionControl;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.UUID;

record RegisterResult (String username, String authToken){}
public class UserService {
    public static boolean isNull(Object... objects){
        for (Object object:objects){
            if (object == null){
                return true;
            }
        }
        return false;
    }

    public static RegisterResult register(RegisterRequest registerRequest, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException, AlreadyTakenException {
        var username = registerRequest.username();
        var password = registerRequest.password();
        var email = registerRequest.email();
        if (isNull(username, password, email)){
            throw new DataAccessException("Error: bad request");
        }
        RegisterResult result = null;
        UserData user = userDAO.getUser(username);
        if (user == null){
            user = new UserData(username, password, email);
            userDAO.createUser(user);
            AuthData authData = new AuthData(generateToken(), username);
            authDAO.createAuth(authData);
            result = new RegisterResult(authData.username(), authData.authToken());
        }
        else{
            throw new AlreadyTakenException("User already exists");
        }
        return result;
    }

    public static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
