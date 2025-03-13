package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO {
    public void clear() throws DataAccessException {
        DatabaseManager.clearUserData();
    }

    public UserData getUser(String username){
        try {
            return DatabaseManager.getUserData(username);
        } catch (DataAccessException e){
            return null;
        }
    }

    public void createUser(UserData userData){

    }
}
