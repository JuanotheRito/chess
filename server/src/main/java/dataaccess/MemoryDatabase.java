package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryDatabase {
    private static MemoryDatabase instance = new MemoryDatabase();
    private static ArrayList<UserData> userDatabase= new ArrayList<>();
    private static ArrayList<GameData> gameDatabase = new ArrayList<>();
    private static ArrayList<AuthData> authDatabase = new ArrayList<>();
    private static int nextGameID = 1;

    private MemoryDatabase(){}

    public static MemoryDatabase getInstance(){
        return instance;
    }

    public static void clearUserData(){
        userDatabase = new ArrayList<>();
    }

    public static void clearGameData(){
        gameDatabase = new ArrayList<>();
        nextGameID = 1;
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

    public static int getNextGameID(){
        return nextGameID;
    }

    public static void addUserData(UserData userData){
        userDatabase.add(userData);
    }
    public static void addGameData(GameData gameData){
        gameDatabase.add(gameData);
        nextGameID++;
    }
    public static void addAuthData(AuthData authData){
        authDatabase.add(authData);
    }

    public static void deleteAuthData(AuthData delete){
        authDatabase.removeIf(authData -> Objects.equals(authData.authToken(), delete.authToken()));
    }
}
