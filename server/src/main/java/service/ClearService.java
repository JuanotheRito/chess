package service;

import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

public class ClearService {
    public static void clear(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();

    }
}
