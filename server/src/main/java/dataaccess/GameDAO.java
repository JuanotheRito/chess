package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    public static ArrayList<GameData> memoryData = new ArrayList<>();

    public void clear();

    public GameData createGame(String gameName);

    public ArrayList<GameData> getGames();
}
