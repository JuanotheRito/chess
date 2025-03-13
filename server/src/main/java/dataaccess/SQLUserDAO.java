package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO implements UserDAO {
    public void clear() throws DataAccessException {
        DatabaseManager.clearUserData();
    }

    public UserData getUser(String username){
        try {
            return DatabaseManager.getUserData(username);
        } catch (DataAccessException e){
            return null;
        }
    }

    public void createUser(UserData userData) throws DataAccessException {
        String username = userData.username();
        String password = userData.password();
        String email = userData.email();
        String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData encryptedUser = new UserData(username, hashedPw, email);
        DatabaseManager.addUser(encryptedUser);
    }
}
