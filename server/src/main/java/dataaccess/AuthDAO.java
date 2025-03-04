package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {
    public void clear();

    public void createAuth(AuthData authData);

    public AuthData getAuth(String authToken);
}
