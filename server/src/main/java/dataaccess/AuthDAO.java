package dataaccess;

import model.AuthData;

public interface AuthDAO {
    boolean deleteAll();
    public void createAuth(AuthData authData);
    public AuthData getAuth(String authToken);
}
