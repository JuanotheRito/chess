package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    public void clear() throws DataAccessException;

    public UserData getUser(String username);

    public void createUser(UserData userData) throws DataAccessException;
}
