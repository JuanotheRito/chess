package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    public void clear() throws DataAccessException;

    public GameData createGame(String gameName) throws DataAccessException;

    public ArrayList<GameData> getGames();

    public GameData getGame(int gameID);

    public void joinGameAsColor(GameData game, ChessGame.TeamColor teamColor, String username);
}
