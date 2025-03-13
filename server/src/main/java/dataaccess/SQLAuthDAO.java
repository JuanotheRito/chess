package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO{

    public void clear() throws DataAccessException {
        DatabaseManager.clearAuthData();
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        if (!(getAuth(authData.authToken()) == null)){
            throw new DataAccessException("Already exists");
        }
        DatabaseManager.addAuthData(authData);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return DatabaseManager.getAuthData(authToken);
    }
    public void deleteAuth(AuthData authData) throws DataAccessException {
        if (((getAuth(authData.authToken())) == null)){
            throw new DataAccessException("Does not exist");
        }
        DatabaseManager.deleteAuthData(authData);
    }

}
