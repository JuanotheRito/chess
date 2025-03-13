package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
     void clear() throws DataAccessException;

     GameData createGame(String gameName) throws DataAccessException;

     ArrayList<GameData> getGames() throws DataAccessException;

     GameData getGame(int gameID) throws DataAccessException;

     void joinGameAsColor(GameData game, ChessGame.TeamColor teamColor, String username) throws DataAccessException;

     void updateGame(GameData game, ChessGame chessGame) throws DataAccessException;
}
