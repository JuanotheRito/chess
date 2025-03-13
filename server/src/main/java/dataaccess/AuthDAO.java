package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {
    public void clear() throws DataAccessException;

    public void createAuth(AuthData authData) throws DataAccessException;

    public AuthData getAuth(String authToken) throws DataAccessException;

    public void deleteAuth(AuthData authData);
}
