package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDatabase;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    @Test
    public void clearSuccessful() throws DataAccessException {
        UserData user = new UserData("Cosmo Cougar", "Go Cougs!", "cosmo@byu.edu");
        MemoryDatabase.addUserData(user);
        MemoryDatabase.addAuthData(new AuthData("12345", "Cosmo Cougar"));
        MemoryDatabase.addGameData(new GameData(123, "ABC", "DEF", "CoolGame", new ChessGame()));

        ClearService.clear();

        assertEquals(0, MemoryDatabase.getAuthData().size());
        assertEquals(0, MemoryDatabase.getUserData().size());
        assertEquals(0, MemoryDatabase.getGameData().size());
    }
}