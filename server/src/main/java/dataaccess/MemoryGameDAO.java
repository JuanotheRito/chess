package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {
    @Override
    public void clear() {
        MemoryDatabase.clearGameData();
    }
    public GameData createGame(String gameName){
        GameData newGame = new GameData(MemoryDatabase.getNextGameID(), null, null, gameName, new ChessGame());
        MemoryDatabase.addGameData(newGame);
        return newGame;
    }

    public ArrayList<GameData> getGames(){
        return MemoryDatabase.getGameData();
    }
}
