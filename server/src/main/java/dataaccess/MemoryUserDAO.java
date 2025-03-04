package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) {
        UserData user = null;
        for (UserData userData : MemoryDatabase.getUserData()) {
            if (Objects.equals(userData.username(), username)) {
                user = userData;
                break;
            }
        }
        return user;
    }

    @Override
    public void createUser(UserData userData) {
        MemoryDatabase.addUserData(userData);
    }

    public void clear(){
        MemoryDatabase.clearUserData();
    }


}
