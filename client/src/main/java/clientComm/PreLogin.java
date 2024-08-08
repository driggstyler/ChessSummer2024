package clientComm;

import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.LoginResult;
import Results.RegisterResult;
import com.google.gson.Gson;

import java.util.ArrayList;
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
                    RegisterResult registerResult = serverFacade.register(registerRequest);
                    if (registerResult.isSuccess()) {
                        System.out.println("Register was successful.");
                        postLogin.run(port, registerResult.getAuthtoken());
                    }
                } catch (Exception e) {
                    System.out.println("Register failed from PreLogin class.");
                }
            }
            else if (input.equals("help")) {
                System.out.println("Options: \nlogin\nregister\nquit");
            }
            else {
                System.out.println("Invalid command. Type help to view valid commands.");
            }

        }
    }
}
