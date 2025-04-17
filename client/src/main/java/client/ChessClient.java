package client;

import chess.*;
import client.websocket.NotificationHandler;
import serverfacade.GameInfo;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;
import client.websocket.WebSocketFacade;

import java.sql.Array;
import java.sql.SQLPermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private List<GameInfo> gameList;
    private boolean resign = false;
    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;
    private ChessGame currentGame = null;
    private ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
    private int currentGameID;

    public ChessClient (String serverUrl, NotificationHandler notificationHandler){
        this.notificationHandler = notificationHandler;
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input){
        try{
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (resign){
                switch (cmd){
                    case "y", "yes" -> {return resign();}
                    case "n", "no" -> {
                        resign = false;
                        return "";
                    }
                }
            }
            return switch (cmd){
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "quit" -> "quit";
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "move" -> move(params);
                case "resign" -> resign();
                case "legal" -> legal(params);
                default -> help();
            };
        } catch (ResponseException ex){
            return ex.getMessage();
        }
    }
    public String register(String... params) throws ResponseException{
        if (params.length == 3){
            var username = params[0];
            var password = params[1];
            var email = params[2];
            try {
                server.register(username, password, email);
                state = State.SIGNEDIN;
                return ("Welcome " + username + "! Type \"help\" to get a list of commands.");
            } catch (ResponseException e){
                throw new ResponseException(400, "This user already exists");
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (state == State.SIGNEDOUT) {
            if (params.length == 2) {
                var username = params[0];
                var password = params[1];
                try {
                    server.login(username, password);
                    state = State.SIGNEDIN;
                    return ("Welcome " + username + "! Type \"help\" to get a list of commands.");
                } catch (Exception e) {
                    throw new ResponseException(400, "Incorrect Login info. Please try again");
                }
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
        else if (state == State.INGAME || state == State.SPECTATE){
            throw new ResponseException(400, "You must leave the game you are currently playing/spectating first.");
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
        output = output + String.format("%-" + columnWidths[0] + "s", gameNumber) + "     ";
        output = output + String.format("%-" + columnWidths[1] + "s", game.gameName()) + "     ";
        if (game.whiteUsername() == null){
            output = output + String.format("%-" + columnWidths[2] + "s", "Empty")+ "     ";
        } else{
            output = output + String.format("%-" + columnWidths[2] + "s", game.whiteUsername())+"     ";
        }
        if (game.blackUsername() == null){
            output = output + String.format("%-" + columnWidths[3] + "s", "Empty")+"     ";
        } else{
            output = output + String.format("%-" + columnWidths[3] + "s", game.blackUsername())+"     ";
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
                        if (!(current == null)){
                            longest = isBigger(longest, current);
                        }
                    }
                }
                case 3 ->{
                    longest = "";
                    for (GameInfo gameInfo : gameList) {
                        String current = gameInfo.blackUsername();
                        if (!(current == null)){
                            longest = isBigger(longest, current);
                        }
                    }
                }
            }
            if (columnWidths[i] < longest.length()){
                columnWidths[i] = longest.length();
            }
        }
        return columnWidths;
    }

    public String join(String ... params) throws ResponseException {
        String result;
        if (state == State.SIGNEDIN) {
            if (params.length == 2) {
                if (gameList == null){
                    throw new ResponseException(400, "Please type \"list\" to get a list of games before attempting to join one.");
                }
                int id;
                if (isInteger(params[0])){
                    try {
                        id = Integer.parseInt(params[0]);
                    } catch (NumberFormatException e){
                        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
                    }
                    try{
                    switch (params[1].toUpperCase()){
                        case "WHITE" -> {
                            try {
                                server.join(ChessGame.TeamColor.WHITE, gameList.get(id - 1).gameID());
                                ws = new WebSocketFacade(serverUrl, notificationHandler);
                                ws.join(server.authToken(), gameList.get(id - 1).gameID(), color);
                                result = "Successfully joined the game \"" + gameList.get(id - 1).gameName() + "\" as WHITE." +
                                        " Have fun!";

                                state = State.INGAME;
                                currentGameID = gameList.get(id - 1).gameID();
                                color = ChessGame.TeamColor.WHITE;
                                return result;
                            } catch (ResponseException e) {
                                throw new ResponseException(400, "That spot is either taken or doesn't exist");
                            }
                        }
                        case "BLACK" -> {
                            try {
                                server.join(ChessGame.TeamColor.BLACK, gameList.get(id - 1).gameID());
                                ws = new WebSocketFacade(serverUrl, notificationHandler);
                                ws.join(server.authToken(), gameList.get(id - 1).gameID(), ChessGame.TeamColor.BLACK);
                                result = "Successfully joined the game \"" + gameList.get(id - 1).gameName() + "\" as BLACK." +
                                        " Have fun!";
                                state = State.INGAME;
                                currentGameID = gameList.get(id - 1).gameID();
                                color = ChessGame.TeamColor.BLACK;
                                return result;
                            } catch (ResponseException e) {
                                throw new ResponseException(400, "That spot is either taken or doesn't exist");
                            }
                        }
                    }
                    } catch (IndexOutOfBoundsException e){
                        throw new ResponseException(400, "That game does not exist");
                    }
                }
            }throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
        } throw new ResponseException(400, "You need to be signed in to join a game.");
    }

    public boolean isInteger (String str){
        if (str == null || str.isEmpty()){
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public String observe(String... params) throws ResponseException {
        if (state == State.SIGNEDIN){
            if (params.length == 1){
                if (gameList == null){
                    throw new ResponseException(400, "Please type \"list\" to get a list of games before attempting to observe one.");
                }
                int id;
                if (isInteger(params[0])) {
                    try {
                        id = Integer.parseInt(params[0]);
                        currentGameID = gameList.get(id - 1).gameID();
                        state = State.SPECTATE;
                        ws = new WebSocketFacade(serverUrl, notificationHandler);
                        ws.observe(server.authToken(), currentGameID);
                        color = ChessGame.TeamColor.WHITE;
                        return printBoard(null);
                    } catch (NumberFormatException e) {
                        throw new ResponseException(400, "Expected: <ID>");
                    } catch (Exception e){
                        throw new ResponseException(400, "That game does not exist");
                    }
                }
            }throw new ResponseException(400, "Expected: <ID>");
        } throw new ResponseException(400, "You need to be signed in to spectate a game.");
    }

    public String leave() throws ResponseException {
        if (state == State.INGAME || state == State.SPECTATE){
            state = State.SIGNEDIN;
            ws.leave(server.authToken(), currentGameID);
            return "You have left the game.";
        }
        throw new ResponseException(400, "You are not currently in a game");
    }

    public String redraw() throws ResponseException{
        return printBoard(null);
    }

    public String move(String... params) throws ResponseException {
        if (state == State.INGAME){
            if (params.length == 2){
                String arg1 = params[0];
                String arg2 = params[1];

                ChessPosition startPosition = evalPosition(arg1);
                ChessPosition endPosition = evalPosition(arg2);
                ChessPiece piece = currentGame.getBoard().getPiece(startPosition);

                ChessPiece.PieceType promotion = null;
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
                    int promotionRow = 0;
                    if (color == ChessGame.TeamColor.WHITE){
                        promotionRow = 8;
                    }
                    if (color == ChessGame.TeamColor.BLACK){
                        promotionRow = 1;
                    }
                    if (endPosition.getRow() == promotionRow){
                        System.out.print("What piece would you like to promote your pawn to? >>> ");
                        Scanner scanner = new Scanner(System.in);
                        String line = scanner.nextLine().toUpperCase();
                        switch (line){
                            case "QUEEN" -> promotion = ChessPiece.PieceType.QUEEN;
                            case "KNIGHT" -> promotion = ChessPiece.PieceType.KNIGHT;
                            case "BISHOP" -> promotion = ChessPiece.PieceType.BISHOP;
                            case "ROOK" -> promotion = ChessPiece.PieceType.ROOK;
                            default -> throw new ResponseException(400, "That is not a valid piece");
                        }
                        System.out.print("\n");
                    }
                }
                ChessMove move = new ChessMove(startPosition, endPosition, promotion);
                ws.move(server.authToken(), currentGameID, move);
                List<ChessMove> validMoves = (List<ChessMove>) currentGame.validMoves(startPosition);
                if (validMoves.contains(move)){
                    return (String.format("Moved piece at %s to %s", startPosition, endPosition));
                }
                else {return ("");}
            } else{
                throw new ResponseException(400, "Expected [POSITION1] [POSITION2]");
            }
        } throw new ResponseException(400, "You are not currently playing a game");
    }

    public ChessPosition evalPosition(String input) throws ResponseException {
        int col;
        int row;
        switch (input.charAt(0)){
            case 'a' -> col = 1;
            case 'b' -> col = 2;
            case 'c' -> col = 3;
            case 'd' -> col = 4;
            case 'e' -> col = 5;
            case 'f' -> col = 6;
            case 'g' -> col = 7;
            case 'h' -> col = 8;
            default -> throw new ResponseException(400, "Not a valid position(s)");
        }
        try {
            row = Integer.parseInt(input.substring(1, 2));
            if (row > 8 || row < 1){
                throw new ResponseException(400, "Not a valid position(s)");
            }
        } catch (NumberFormatException e){
            throw new ResponseException(400, "Not a valid position(s)");
        }
        return new ChessPosition(row, col);
    }

    public String resign() throws ResponseException {
        if (state == State.INGAME) {
            if (!resign) {
                resign = true;
                return ("Are you sure? Resigning will cause a loss!");
            } else {
                resign = false;
                ws.resign(server.authToken(), currentGameID);
                state = State.SIGNEDIN;
                return ("You have resigned.");
            }
        } throw new ResponseException(400, "You are not currently in a game");
    };

    public String legal(String... params) throws ResponseException{
        if (params.length == 1){
            if (state == State.INGAME || state == State.SPECTATE) {
                ChessPosition position = evalPosition(params[0]);
                return printBoard(position);
            }
            throw new ResponseException(400, "You aren't watching or playing a game");
        }
        throw new ResponseException(400, "Expected <POSITION>");
    }

    public String printBoard (ChessPosition position){
        ChessBoard chessBoard = currentGame.getBoard();
        String board = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +  "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n";
        boolean white = true;
        for (int i = 8; i > 0; i--){
            board += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + i + " ";
            for (int f = 0; f < 8; f++){
                ChessPosition testPosition = new ChessPosition(i, f+1);
                ArrayList<ChessPosition> validMove = new ArrayList<>();
                boolean yellow = false;
                if (position != null) {
                    if (currentGame.getBoard().getPiece(position) != null) {
                        for (ChessMove move : currentGame.validMoves(position)) {
                            validMove.add(move.getEndPosition());
                        }

                        if (testPosition.equals(position)) {
                            board += SET_BG_COLOR_YELLOW;
                            yellow = true;
                        }
                    }
                }
                if (white && !yellow) {
                    if (validMove.contains(testPosition)) {
                        board += SET_BG_COLOR_GREEN;
                    } else {
                    board += SET_BG_COLOR_WHITE;
                    }
                } else if (!yellow) {
                    if (validMove.contains(testPosition)) {
                        board += SET_BG_COLOR_DARK_GREEN;
                    } else {
                        board += SET_BG_COLOR_DARK_GREY;
                    }
                }
                white = !white;
                ChessPiece piece = chessBoard.getBoard()[i-1][f];
                if (piece != null) {
                    switch (piece.getTeamColor()) {
                        case WHITE -> board += pieceType(ChessGame.TeamColor.WHITE, piece.getPieceType());
                        case BLACK -> board += pieceType(ChessGame.TeamColor.BLACK, piece.getPieceType());
                    }
                }
                else {
                    board += "   ";
                }
            }
            white = !white;
            board += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + i + " ";
            board += RESET_BG_COLOR + "\n";
        }
        board += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +"    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n";
        if (color == ChessGame.TeamColor.BLACK){
            board = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR +"\n";
            white = true;
            for (int x = 1; x < 9; x++){
                board += SET_BG_COLOR_LIGHT_GREY + " " + x + " ";
                for (int y = 7; y >= 0; y--){
                    ChessPosition testPosition = new ChessPosition(x, y+1);
                    ArrayList<ChessPosition> validMove = new ArrayList<>();
                    boolean yellow = false;
                    if (position != null) {
                        if (currentGame.getBoard().getPiece(position) != null) {
                            for (ChessMove move : currentGame.validMoves(position)) {
                                validMove.add(move.getEndPosition());
                            }

                            if (testPosition.equals(position)) {
                                board += SET_BG_COLOR_YELLOW;
                                yellow = true;
                            }
                        }
                    }
                    if (white && !yellow) {
                        if (validMove.contains(testPosition)) {
                            board += SET_BG_COLOR_GREEN;
                        } else {
                            board += SET_BG_COLOR_WHITE;
                        }
                    } else if (!yellow) {
                        if (validMove.contains(testPosition)) {
                            board += SET_BG_COLOR_DARK_GREEN;
                        } else {
                            board += SET_BG_COLOR_DARK_GREY;
                        }
                    }
                    white = !white;
                    ChessPiece piece = chessBoard.getBoard()[x-1][y];
                    if (piece != null) {
                        switch (piece.getTeamColor()) {
                            case WHITE -> board += pieceType(ChessGame.TeamColor.WHITE, piece.getPieceType());
                            case BLACK -> board += pieceType(ChessGame.TeamColor.BLACK, piece.getPieceType());
                        }
                    }
                    else {
                        board += "   ";
                    }
                }
                white = !white;
                board += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + x + " ";
                board += RESET_BG_COLOR + "\n";
            }
            board += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR +"\n" + RESET_TEXT_COLOR;
        }
        return board;
    }

    public String pieceType(ChessGame.TeamColor color, ChessPiece.PieceType type){
        if (color == ChessGame.TeamColor.WHITE){
            return switch(type){
                case PAWN ->  SET_TEXT_COLOR_RED + " P ";
                case ROOK -> SET_TEXT_COLOR_RED + " R ";
                case BISHOP ->    SET_TEXT_COLOR_RED + " B ";
                case KNIGHT ->    SET_TEXT_COLOR_RED + " N ";
                case KING ->    SET_TEXT_COLOR_RED + " K ";
                case QUEEN ->   SET_TEXT_COLOR_RED + " Q ";
            };
        }
        if (color == ChessGame.TeamColor.BLACK) {
            return switch (type) {
                case PAWN ->   SET_TEXT_COLOR_BLUE + " P ";
                case ROOK ->   SET_TEXT_COLOR_BLUE + " R ";
                case BISHOP ->   SET_TEXT_COLOR_BLUE + " B ";
                case KNIGHT ->   SET_TEXT_COLOR_BLUE + " N ";
                case KING ->   SET_TEXT_COLOR_BLUE + " K ";
                case QUEEN ->  SET_TEXT_COLOR_BLUE + " Q ";
            };
        }
        return "";
    }

    public void setCurrentGame(ChessGame currentGame) {
        this.currentGame = currentGame;
    }

    public String help(){
        String res = "";
        switch (state) {
            case State.SIGNEDOUT -> res = """
                    \u001b[32mregister <USERNAME> <PASSWORD> <EMAIL> - \u001b[34mcreate an account
                    \u001b[32mlogin <USERNAME> <PASSWORD> - \u001b[34mlogin to play chess
                    \u001b[32mquit - \u001b[34mclose the chess client
                    \u001b[32mhelp - \u001b[34mlist possible commands
                    """;

            case State.SIGNEDIN -> res = """
                    \u001b[32mcreate <NAME> - \u001b[34mcreate a game
                    \u001b[32mlist - \u001b[34mlist current games
                    \u001b[32mjoin <ID> [WHITE|BLACK] - \u001b[34mjoin a game as white or black
                    \u001b[32mobserve <ID> - \u001b[34mspectate a game
                    \u001b[32mlogout - \u001b[34mlogout of your chess account
                    \u001b[32mquit - \u001b[34mclose the chess client
                    \u001b[32mhelp - \u001b[34mlist possible commands
                    """;

            case State.SPECTATE ->  res = """
                    \u001b[32mleave - \u001b[34mleave the game you are currently spectating
                    \u001b[32mhelp - \u001b[34mlist possible commands
                    """;

            case State.INGAME -> res = """
                    \u001b[32mredraw - \u001b[34mredraw the current chess board
                    \u001b[32mleave - \u001b[34mleave the game you are currently playing
                    \u001b[32mmove <SPACE1> <SPACE2> [WHITE|BLACK] - \u001b[34mmove the piece at Space 1 to Space 2
                    \u001b[32mresign - \u001b[34mforfeit the current game
                    \u001b[32mlegal <SPACE> - \u001b[34mhighlight the legal moves of the piece at the given space
                    """;
        }
        return res;
    }
}
