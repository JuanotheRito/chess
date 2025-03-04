package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class MemoryDatabase {
    private static MemoryDatabase instance = new MemoryDatabase();
    private static ArrayList<UserData> userDatabase= new ArrayList<>();
    private static ArrayList<GameData> gameDatabase = new ArrayList<>();
    private static ArrayList<AuthData> authDatabase = new ArrayList<>();

    private MemoryDatabase(){}

    public static MemoryDatabase getInstance(){
        return instance;
    }

    public static void clearUserData(){
        userDatabase = new ArrayList<>();
    }

    public static void clearGameData(){
        gameDatabase = new ArrayList<>();
    }

    public static void clearAuthData(){
        authDatabase = new ArrayList<>();
    }

    public static ArrayList<UserData> getUserData(){
        return userDatabase;
    }

    public static ArrayList<GameData> getGameData(){
        return gameDatabase;
    }

    public static ArrayList<AuthData> getAuthData(){
        return authDatabase;
    }

    public static void addUserData(UserData userData){
        userDatabase.add(userData);
    }
    public static void addGameData(GameData gameData){
        gameDatabase.add(gameData);
    }
    public static void addAuthData(AuthData authData){
        authDatabase.add(authData);
    }
}
