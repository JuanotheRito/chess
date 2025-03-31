package client;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class Repl {

    private final ChessClient client;

    public Repl(String serverUrl){
        client = new ChessClient(serverUrl);
    }

    public void run(){
        String result = "";
        System.out.println("♕ Welcome to Chess. Type \"help\" to get started.");

        Scanner scanner = new Scanner(System.in);
        while (!result.equals("quit")) {
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
}
