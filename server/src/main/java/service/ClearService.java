package service;

import dataaccess.*;

public class ClearService {
    public static void clear() {
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();

        memoryAuthDAO.clear();
        memoryGameDAO.clear();
        memoryUserDAO.clear();
    }
}
