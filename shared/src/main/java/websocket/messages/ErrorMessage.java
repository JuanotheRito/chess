package websocket.messages;

public class ErrorMessage extends ServerMessage{

    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.message = errorMessage;
    }
}
