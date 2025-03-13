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

    @Test
    void joinGameSuccessfully() throws DataAccessException{
        GameDAO gameDAO = new SQLGameDAO();
        gameDAO.createGame(testGame.gameName());
        gameDAO.joinGameAsColor(testGame, ChessGame.TeamColor.WHITE, "CosmoCougar");
        var actual = gameDAO.getGame(testGame.gameID());
        var expected = new GameData(1, "CosmoCougar", null, "gg", new ChessGame());
        assertEquals(expected, actual);
        gameDAO.joinGameAsColor(testGame, ChessGame.TeamColor.BLACK, "UtahUtes");
        expected = new GameData(1, "CosmoCougar", "UtahUtes", "gg", new ChessGame());
        actual = gameDAO.getGame(testGame.gameID());
        assertEquals(expected, actual);
    }

    @Test
    void joinNonexistentGame(){
        GameDAO gameDAO = new SQLGameDAO();
        assertThrows(DataAccessException.class, () -> gameDAO.joinGameAsColor(testGame, ChessGame.TeamColor.WHITE, "CosmoCougar"));
    }
}
