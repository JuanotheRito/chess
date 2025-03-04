package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    public static ArrayList<GameData> memoryData = new ArrayList<>();

    public void clear();

    public GameData createGame(String gameName);

    public ArrayList<GameData> getGames();

    public GameData getGame(int gameID);

    public void joinGameAsColor(GameData game, ChessGame.TeamColor teamColor, String username);
}
