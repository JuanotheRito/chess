package service;

import dataaccess.DataAccessException;
import jdk.jshell.spi.ExecutionControl;

import javax.xml.crypto.Data;

record RegisterResult (String username,String authToken){}
public class UserService {
    public static RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        RegisterResult result;
        try{
            getUser(username);
        }
        catch (DataAccessException e){
            throw new RuntimeException(e);
        }
        return result;
    }
}
