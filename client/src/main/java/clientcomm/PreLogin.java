package clientcomm;

import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.LogoutResult;

import java.util.Scanner;

public class PreLogin {
    int port;
    public PreLogin(int port) {
        this.port = port;
    }
    public void run() {
        Scanner scanner = new Scanner(System.in);
        PostLogin postLogin = new PostLogin();
        ServerFacade serverFacade = new ServerFacade(port);
        System.out.println("Welcome, to see commands type help.");
        while (true) {
            String input = scanner.nextLine();
            //Do command based on input
            if (input.equals("quit")) {
                break;
            }
            else if (input.equals("login")) {
                System.out.println("Please type your username.");
                String username = scanner.nextLine();
                System.out.println("Please type your password.");
                String password = scanner.nextLine();
                LoginRequest loginRequest = new LoginRequest(username, password);
                try {
                    LoginResult loginResult = serverFacade.login(loginRequest);
                    if (loginResult.isSuccess()) {
                        System.out.println("Login was successful");
                        postLogin.run(port, loginResult.getAuthtoken());
                    }
                    else {
                        System.out.println("Couldn't log in, username or password was incorrect.");
                    }
                } catch (Exception e) {
                    System.out.println("Login failed from PreLogin class.");
                }
            }
            else if (input.equals("register")) {
                System.out.println("Please type your new username.");
                String username = scanner.nextLine();
                System.out.println("Please type your new password.");
                String password = scanner.nextLine();
                System.out.println("Please type your email.");
                String email = scanner.nextLine();
                RegisterRequest registerRequest = new RegisterRequest(username, password, email);
                try {
                    LoginResult loginResult = serverFacade.register(registerRequest);
                    if (loginResult.isSuccess()) {
                        System.out.println("Register was successful.");
                        postLogin.run(port, loginResult.getAuthtoken());
                    }
                    else {
                        System.out.println("Couldn't register, username is already taken.");
                    }
                } catch (Exception e) {
                    System.out.println("Register failed from PreLogin class.");
                }
            }
            else if (input.equals("help")) {
                System.out.println("""
                        Options:
                        help - Shows the possible commands.
                        login - Logs you into the server by prompting you for your username and password
                        register - Registers you as a new user and automatically logs you in by prompting you
                            for a new username, password, and email
                        quit - Closes the program.""");
            }
            else {
                System.out.println("Invalid command. Type help to view valid commands.");
            }

        }
    }
}
