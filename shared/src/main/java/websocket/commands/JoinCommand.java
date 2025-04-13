package websocket.commands;

import chess.ChessGame;

public class JoinCommand extends UserGameCommand{
    public ChessGame.TeamColor teamColor;
    public JoinCommand(CommandType commandType, String authToken, int gameID, ChessGame.TeamColor teamColor) {
        super(commandType, authToken, gameID);
        this.teamColor = teamColor;
    }
}
