package service;

import dataaccess.*;

import java.sql.SQLException;

public class ClearService {
    public static void clear() throws DataAccessException {
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();

        AuthDAO sqlAuthDAO = new SQLAuthDAO();
        GameDAO sqlGameDAO = new SQLGameDAO();
        UserDAO sqlUserDAO = new SQLUserDAO();

        memoryAuthDAO.clear();
        memoryGameDAO.clear();
        memoryUserDAO.clear();

        sqlAuthDAO.clear();
        sqlGameDAO.clear();
        sqlUserDAO.clear();
    }
}
