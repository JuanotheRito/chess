package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO {
    public void clear() throws DataAccessException {
        DatabaseManager.clearUserData();
    }

    public UserData getUser(String username){

    }

    public void createUser(UserData userData{

    }
}
