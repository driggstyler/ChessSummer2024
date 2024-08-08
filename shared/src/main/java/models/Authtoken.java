package models;

/**
 * The authtoken object containing both the username and the actual authtoken string.
 */
public class Authtoken {
    private String authtoken;
    private String username;

    /**
     * Constructor for the Authtoken
     * @param authtoken Authtoken string (actual authtoken)
     * @param username Username assigned to the authtoken string.
     */
    public Authtoken(String authtoken, String username) {
        this.authtoken = authtoken;
        this.username = username;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
