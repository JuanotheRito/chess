package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    public static void setupDatabase() throws DataAccessException {
        createDatabase();
        initializeTables();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    static void newTable(String createTable) throws SQLException, DataAccessException {
        try (var conn = getConnection()) {
            try (var createTableStatement = conn.prepareStatement(createTable)) {
                createTableStatement.executeUpdate();
            }
        }
    }

    static void initializeTables() throws DataAccessException {
        try{
            var createTable = """
                    CREATE TABLE IF NOT EXISTS authData (
                        authToken VARCHAR(255) NOT NULL,
                        username VARCHAR(255) NOT NULL
                    )""";
            newTable(createTable);
            createTable = """
                CREATE TABLE IF NOT EXISTS gameData (
                    id INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255) NOT NULL,
                    game TEXT NOT NULL,
                    PRIMARY KEY (id)
                )""";
            newTable(createTable);
            createTable = """
                CREATE TABLE IF NOT EXISTS userData (
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL
                )""";
            newTable(createTable);
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void clearAuthData() throws DataAccessException {
        try (var conn = getConnection()){
            try(var preparedStatement = conn.prepareStatement("TRUNCATE TABLE authData;")){
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void clearGameData() throws DataAccessException {
        try (var conn = getConnection()){
            try(var preparedStatement = conn.prepareStatement("TRUNCATE TABLE gameData;")){
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void clearUserData() throws DataAccessException {
        try (var conn = getConnection()){
            try(var preparedStatement = conn.prepareStatement("TRUNCATE TABLE userData;")){
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static UserData getUserData(String findUser) throws DataAccessException {
        try (var conn = getConnection()){
            try(var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM userData WHERE username=?")) {
                preparedStatement.setString(1, findUser);
                try(var rs = preparedStatement.executeQuery()){
                    if (rs.next()) {
                        var username = rs.getString("username");
                        var password = rs.getString("password");
                        var email = rs.getString("email");

                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return null;
    }

    public static void addUser(UserData userData) throws DataAccessException {
        try (var conn = getConnection()){
            String username = userData.username();
            String password = userData.password();
            String email = userData.email();
            try (var preparedStatement = conn.prepareStatement("INSERT INTO userData (username, password, email) VALUES (?, ?, ?);")){
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e){
            throw new DataAccessException((e.getMessage()));
        }
    }

    public static void addAuthData(AuthData authData) throws DataAccessException {
        try (var conn = getConnection()){
            String authToken = authData.authToken();
            String username = authData.username();
            try (var preparedStatement = conn.prepareStatement("INSERT INTO authData (authToken, username) VALUES (?, ?);")){
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);

                preparedStatement.executeUpdate();
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public static AuthData getAuthData(String findAuth) throws DataAccessException {
        try (var conn = getConnection()){
            try(var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM authData WHERE authToken=?;")) {
                preparedStatement.setString(1, findAuth);
                try(var rs = preparedStatement.executeQuery()){
                    if (rs.next()) {
                        var authToken = rs.getString("authToken");
                        var username = rs.getString("username");

                        return new AuthData(authToken, username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return null;
    }

    public static void deleteAuthData(AuthData authData) throws DataAccessException{
        try (var conn = getConnection()){
            try(var preparedStatement = conn.prepareStatement("DELETE FROM authData WHERE authToken=?;")){
                preparedStatement.setString(1, authData.authToken());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public static GameData createGame(String gameName) throws DataAccessException {
        try (var conn = getConnection()){
            try(var preparedStatement = conn.prepareStatement("INSERT INTO gameData (whiteUsername, blackUsername, gameName, game) VALUES(null, null, ?, ?)", Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setString(1, gameName);
                Gson serializer = new Gson();
                var json = serializer.toJson(new ChessGame());
                preparedStatement.setString(2, json);

                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                var ID = 0;
                if (resultSet.next()){
                    ID = resultSet.getInt(1);
                }

                return new GameData(ID, null, null, gameName, serializer.fromJson(json, ChessGame.class));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static ArrayList<GameData> getGameList() throws DataAccessException{
        try (var conn = getConnection()){
            ArrayList<GameData> gameList = new ArrayList<>();
            try(var preparedStatement = conn.prepareStatement("SELECT id, whiteUsername, blackUsername, gameName, game FROM gameData;")){
                try (var rs = preparedStatement.executeQuery()) {
                    Gson serializer = new Gson();
                    while (rs.next()) {
                        var id = rs.getInt("id");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var json = rs.getString("game");
                        var game = serializer.fromJson(json, ChessGame.class);

                        GameData readGame = new GameData(id, whiteUsername, blackUsername, gameName, game);
                        gameList.add(readGame);
                    }
                }
            }
            return gameList;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static GameData getGameData(int gameId) throws DataAccessException{
        try (var conn = getConnection()){
            try(var preparedStatement = conn.prepareStatement("SELECT id, whiteUsername, blackUsername, gameName, game FROM gameData WHERE id=?")) {
                preparedStatement.setInt(1, gameId);
                try(var rs = preparedStatement.executeQuery()){
                    Gson serializer = new Gson();
                    if (rs.next()) {
                        var id = rs.getInt("id");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var json = rs.getString("game");
                        var game = serializer.fromJson(json, ChessGame.class);

                        return new GameData(id, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return null;
    }

    public static void setPlayer(int id, ChessGame.TeamColor teamColor, String username) throws DataAccessException {
        try (var conn = getConnection()) {
            if (teamColor == ChessGame.TeamColor.WHITE) {
                try(var preparedStatement = conn.prepareStatement("UPDATE gameData SET whiteUsername = ? WHERE id = ?")){
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, id);

                    preparedStatement.executeUpdate();
                }
            }
            if (teamColor == ChessGame.TeamColor.BLACK) {
                try(var preparedStatement = conn.prepareStatement("UPDATE gameData SET blackUsername = ? WHERE id = ?")){
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, id);

                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void updateGame(int id, ChessGame game) throws DataAccessException {
        try (var conn = getConnection()) {
            try(var preparedStatement = conn.prepareStatement("UPDATE gameData SET game = ? WHERE id = ?")){
                Gson serializer = new Gson();
                var json = serializer.toJson(game);
                preparedStatement.setString(1, json);
                preparedStatement.setInt(2, id);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
}
