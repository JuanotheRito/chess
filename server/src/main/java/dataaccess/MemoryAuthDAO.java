package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO{
    ArrayList<AuthData> memoryDatabase = new ArrayList<>();
    @Override
     public boolean deleteAll() {
        memoryDatabase = new ArrayList<AuthData>();
        return memoryDatabase.isEmpty();
     }
     public void createAuth(AuthData authData){
        try {
            AuthData exists = getAuth(authData.authToken());
            if (exists == null) {
                memoryDatabase.add(authData);
            }
            else{
                throw new DataAccessException("User is already logged in");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
     }
     public AuthData getAuth(String authToken){
        AuthData result = null;
        for (AuthData authData:memoryDatabase){
            if (Objects.equals(authData.authToken(), authToken)){
                result = authData;
            }
        }
        return result;
     }
 }
