package client;

import server.GameInfo;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.List;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private List<GameInfo> gameList;

    public ChessClient (String serverUrl, Repl repl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input){
        try{
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd){
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "quit" -> "quit";
                //case "join" -> join(params);
                default -> help();
            };
        } catch (ResponseException ex){
            return ex.getMessage();
        }
    }
    public String register(String... params) throws ResponseException{
        if (params.length >= 3){
            var username = params[0];
            var password = params[1];
            var email = params[2];
            server.register(username, password, email);
            state = State.SIGNEDIN;
            return ("Welcome " + username + "! Type \"help\" to get a list of commands.");
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (state == State.SIGNEDOUT) {
            if (params.length >= 2) {
                var username = params[0];
                var password = params[1];

                server.login(username, password);
                state = State.SIGNEDIN;
                return ("Welcome " + username + "! Type \"help\" to get a list of commands.");
            }
            throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
        }
        throw new ResponseException(400, "Already signed in");
    }

    public String logout() throws ResponseException{
        if (state == State.SIGNEDIN){
            server.logout();
            state = State.SIGNEDOUT;
            return ("You have successfully signed out. So long!");
        }
        throw new ResponseException(400, "Already signed out");
    }

    public String create(String... params) throws ResponseException{
        if (state == State.SIGNEDIN){
            if (params.length >= 1){
                var name = params[0];

                server.create(name);
                return ("Created a game with the name \"" + name+ "\".");
            }
            throw new ResponseException(400, "Expected: <NAME>");
        }
        throw new ResponseException(400, "You need to be signed in to create a game.");
    }

    public String list() throws ResponseException{
        String response = "";
        if (state == State.SIGNEDIN) {
            gameList = server.list();
            String[] headers = {"#", "Name", "White Player", "Black Player"};
            int[] columnWidths = formatList(gameList, headers);
            for (int i =0; i < headers.length; i++){
                response = response + String.format("%-" + columnWidths[i] + "s", headers[i]);
                response = response + "     ";
            }
            response += "\n";
            for (int i = 0 ; i < gameList.size(); i++){
                response += printGame(gameList.get(i), i+1, columnWidths);
                response = response + "\n";
            }
        }
        return response;
    }

    public String printGame (GameInfo game, int gameNumber, int[] columnWidths){
        String output = "";
        output = output + String.format("%-" + columnWidths[0] + "s", gameNumber);
        output = output + String.format("%-" + columnWidths[1] + "s", game.gameName());
        if (game.whiteUsername() == null){
            output = output + String.format("%-" + columnWidths[2] + "s", "Empty");
        } else{
            output = output + String.format("%-" + columnWidths[2] + "s", game.whiteUsername());
        }
        if (game.blackUsername() == null){
            output = output + String.format("%-" + columnWidths[3] + "s", "Empty");
        } else{
            output = output + String.format("%-" + columnWidths[3] + "s", game.blackUsername());
        }
        return output;
    }

    public String isBigger(String leftHand, String rightHand){
        if (rightHand.length() > leftHand.length()){
            return rightHand;
        } else{
            return leftHand;
        }
    }

    public int[] formatList(List<GameInfo> gameList, String[] headers){
        int [] columnWidths = new int[4];
        for (int i = 0; i < columnWidths.length; i++){
            columnWidths[i] = headers[i].length();
        }
        for (int i = 0; i < 4; i++){
            String longest = "";
            switch (i) {
                case 0 -> {
                    int biggestNumber = gameList.size();
                    longest = String.valueOf(biggestNumber);
                }
                case 1 -> {
                    longest = "";
                    for (GameInfo gameInfo : gameList) {
                        String current = gameInfo.gameName();
                        longest = isBigger(longest, current);
                    }
                }
                case 2 -> {
                    longest = "";
                    for (GameInfo gameInfo : gameList) {
                        String current = gameInfo.whiteUsername();
                        longest = isBigger(longest, current);
                    }
                }
                case 3 ->{
                    longest = "";
                    for (GameInfo gameInfo : gameList) {
                        String current = gameInfo.blackUsername();
                        longest = isBigger(longest, current);
                    }
                }
            }
            if (columnWidths[i] < longest.length()){
                columnWidths[i] = longest.length();
            }
        }
        return columnWidths;
    }
    public String help(){
        if (state == State.SIGNEDOUT){
            return"""
            \u001b[32mregister <USERNAME> <PASSWORD> <EMAIL> - \u001b[34mcreate an account
            \u001b[32mlogin <USERNAME> <PASSWORD> - \u001b[34mlogin to play chess
            \u001b[32mquit - \u001b[34mclose the chess client
            \u001b[32mhelp - \u001b[34mlist possible commands
            """;
        }
        return """
            \u001b[32mcreate <NAME> - \u001b[34mcreate a game
            \u001b[32mlist - \u001b[34mlist current games
            \u001b[32mjoin <ID> [WHITE|BLACK] - \u001b[34mjoin a game as white or black
            \u001b[32mobserve <ID> - \u001b[34mspectate a game
            \u001b[32mlogout - \u001b[34mlogout of your chess account
            \u001b[32mquit - \u001b[34mclose the chess client
            \u001b[32mhelp - \u001b[34mlist possible commands
            """;
    }
}
