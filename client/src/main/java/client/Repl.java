package client;
import chess.ChessGame;
import client.websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {

    private final ChessClient client;

    public Repl(String serverUrl){
        client = new ChessClient(serverUrl, this);
    }

    public void run(){
        String result = "";
        System.out.println("♕ Welcome to Chess. Type \"help\" to get started.");

        Scanner scanner = new Scanner(System.in);
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result+"\n");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage notification){
        if (notification instanceof LoadGameMessage){
            client.setCurrentGame(notification.getGame());
            client.printBoard();
        }
        else if (notification instanceof ErrorMessage){
            System.out.println(SET_TEXT_COLOR_RED + ((ErrorMessage) notification).errorMessage());
        }
        else{
            System.out.println(SET_TEXT_COLOR_RED + notification.message());
        }
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
