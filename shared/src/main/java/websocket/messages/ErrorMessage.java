package websocket.messages;

public class ErrorMessage extends ServerMessage {

    private String errorMessage;

    public ErrorMessage(ServerMessageType type) {
        super(type);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
