package models;

/**
 * The User object containing the username, password, and email of the given user.
 */
public class User {
    private String username;
    private String password;
    private String email;

    /**
     * Constructor for the User
     * @param username Username of the user.
     * @param password Password of the user.
     * @param email Email of the user.
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
