package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO{
    public void clear() throws DataAccessException {
        DatabaseManager.clearGameData();
    }

    public GameData createGame(String gameName){
        return null;
    }

    public ArrayList<GameData> getGames(){
        return null;
    }

    public GameData getGame(int gameID){
        return null;
    }

    public void joinGameAsColor(GameData game, ChessGame.TeamColor teamColor, String username){

    }
}
