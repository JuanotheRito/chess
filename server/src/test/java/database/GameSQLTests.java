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

import static org.junit.jupiter.api.Assertions.assertEquals;


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
}
