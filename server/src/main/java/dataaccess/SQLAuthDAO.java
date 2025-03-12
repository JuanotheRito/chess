package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{

    public void clear() throws DataAccessException {
        DatabaseManager.clearAuthData();
    }

    public void createAuth(AuthData authData){

    }

    public AuthData getAuth(String authToken) {

    }
    public void deleteAuth(AuthData authData){

    }

}
