package database;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import dataaccess.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ClearService;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class GameSQLTests {
    static GameData testGame = new GameData(1, null, null, "gg", new ChessGame());

    @BeforeAll
    static void initiateDatabase() throws DataAccessException {
        DatabaseManager.setupDatabase();
    }

    @AfterEach
    void clear() throws DataAccessException{
        ClearService.clear();
    }

    @Test
    void createSuccess() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        var actual = gameDAO.createGame(testGame.gameName());
        assertEquals(testGame, actual);
    }

    @Test
    void nullNameCreateFail(){
        GameDAO gameDAO = new SQLGameDAO();
        assertThrows(DataAccessException.class, ()->gameDAO.createGame(null));
    }

    @Test
    void retriveListSuccess() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        var newGame = gameDAO.createGame(testGame.gameName());
        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(newGame);
        assertEquals(expected, gameDAO.getGames());
    }

    @Test
    void retrieveEmptyListSuccess() throws DataAccessException{
        GameDAO gameDAO = new SQLGameDAO();
        ArrayList<GameData> expected = new ArrayList<>();
        assertEquals(expected, gameDAO.getGames());
    }

    @Test
    void retrieveGameViaIDSuccess() throws DataAccessException{
        GameDAO gameDAO = new SQLGameDAO();
        var newGame = gameDAO.createGame(testGame.gameName());
        newGame = gameDAO.getGame(testGame.gameID());
        assertEquals(testGame, newGame);
    }

    @Test
    void gameDoesNotExist() throws DataAccessException{
        GameDAO gameDAO = new SQLGameDAO();
        assertNull(gameDAO.getGame(testGame.gameID()));
    }
}
