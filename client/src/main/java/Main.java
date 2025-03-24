import chess.*;

import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.Scanner;
public class Main {
    public static final String  PRE_LOGIN_HELP = """
            register <USERNAME> <PASSWORD> <EMAIL> - create an account
            login <USERNAME> <PASSWORD> - login to play chess
            quit - close the chess client
            help - list possible commands
            """;
    public static final String LOGIN_HELP = """
            create <NAME> - create a game
            list - list current games
            join <ID> [WHITE|BLACK] - join a game as white or black
            observe <ID> - spectate a game
            logout - logout of your chess account
            quit - close the chess client
            help - list possible commands
            """;

    public static void main(String[] args) {
        boolean quit = false;
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        while (!quit){
            System.out.println("Welcome to Chess. Type \"help\" to get started.");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (Objects.equals(line, "help")){
                System.out.println(PRE_LOGIN_HELP);
            }
            if (Objects.equals(line, "quit")){
                quit = true;
            }
            if (Objects.equals(line, "login")){

            }
        }
    }
}