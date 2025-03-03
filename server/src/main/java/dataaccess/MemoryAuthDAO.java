package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{
     @Override
     public boolean deleteAll() {
        authDatabase = new ArrayList<AuthData>();
        if (authDatabase.size() == 0){
            return true;
        }
        else{
            return false;
        }
     }
 }
