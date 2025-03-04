package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {
    @Override
    public void clear() {
        MemoryDatabase.clearGameData();
    }

    @Override
    public GameData createGame(String gameName){
        GameData newGame = new GameData(MemoryDatabase.getNextGameID(), null, null, gameName, new ChessGame());
        MemoryDatabase.addGameData(newGame);
        return newGame;
    }

    @Override
    public ArrayList<GameData> getGames(){
        return MemoryDatabase.getGameData();
    }

    @Override
    public GameData getGame(int gameID){
        return MemoryDatabase.getGame(gameID);
    }

    @Override
    public void joinGameAsColor(GameData game, ChessGame.TeamColor teamColor, String username){
        MemoryDatabase.setPlayer(game.gameID(), username, teamColor);
    }
}
