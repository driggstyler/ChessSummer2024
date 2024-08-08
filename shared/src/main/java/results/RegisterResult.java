package results;

public class RegisterResult {
    private String username;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private String authToken;
    private String message;
    private boolean success;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthtoken() {
        return authToken;
    }

    public void setAuthtoken(String authtoken) {
        this.authToken = authtoken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
