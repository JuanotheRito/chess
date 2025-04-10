package websocket.messages;

import com.google.gson.Gson;

public class Notification extends ServerMessage{
    public Notification(ServerMessageType serverMessageType, String message){
        super(serverMessageType);
        this.message = message;
    }
    public String toString() {
        return new Gson().toJson(this);
    }
}
