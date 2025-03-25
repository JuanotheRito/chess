package client;

import org.glassfish.grizzly.http.server.Response;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

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
                //case "quit" -> quit();
                case "register" -> register(params);
                case "logout" -> logout();
                //case "create" -> create(params);
                //case "list" -> list();
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
