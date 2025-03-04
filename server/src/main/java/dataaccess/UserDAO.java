package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    public static ArrayList<UserData> memoryData = new ArrayList<>();

    public void clear();

    public UserData getUser(String username);

    public void createUser(UserData userData);
}
