package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {

    public void clear() {
        MemoryDatabase.clearAuthData();
    }

    public void createAuth(AuthData authData) {
        try {
            AuthData exists = getAuth(authData.authToken());
            if (exists == null) {
                MemoryDatabase.addAuthData(authData);
            } else {
                throw new DataAccessException("Error: User is already logged in");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthData getAuth(String authToken) {
        AuthData result = null;
        for (AuthData authData : MemoryDatabase.getAuthData()) {
            if (Objects.equals(authData.authToken(), authToken)) {
                result = authData;
            }
        }
        return result;
    }

    public void deleteAuth(AuthData authData){
        try{
            AuthData exists = getAuth(authData.authToken());
            if (exists == null) {
                throw new DataAccessException("Error: unauthorized");
            }
            else{
                MemoryDatabase.deleteAuthData(authData);
            }
        } catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }
}
