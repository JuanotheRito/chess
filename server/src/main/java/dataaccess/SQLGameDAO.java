package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO{
    public void clear() throws DataAccessException {
        DatabaseManager.clearGameData();
    }

    public GameData createGame(String gameName) throws DataAccessException {
        return DatabaseManager.createGame(gameName);
    }

    public ArrayList<GameData> getGames() throws DataAccessException {
        return DatabaseManager.getGameList();
    }

    public GameData getGame(int gameID){
      try {
          return DatabaseManager.getGameData(gameID);
      } catch (DataAccessException e) {
          return null;
      }
    }

    public void joinGameAsColor(GameData game, ChessGame.TeamColor teamColor, String username) throws DataAccessException {
        if(getGame(game.gameID()) == null){
            throw new DataAccessException("Game does not exist");
        }
        DatabaseManager.setPlayer(game.gameID(), teamColor, username);
    }
}
