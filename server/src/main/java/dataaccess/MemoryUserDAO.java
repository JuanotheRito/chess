package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO{
    ArrayList<UserData> memoryDatabase = new ArrayList<>();

    @Override
    public UserData getUser(String username){
        UserData user = null;
        for(UserData userData:memoryDatabase){
            if (Objects.equals(userData.username(), username)){
                user = userData;
                break;
            }
        }
        return user;
    }

    @Override
    public void createUser(UserData userData){

        this.memoryDatabase.add(userData);
    }


}
