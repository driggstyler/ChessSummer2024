package handlers;

import requests.LoginRequest;
import results.LoginResult;
import services.LoginService;
import com.google.gson.Gson;
import spark.*;

public class LoginHandler implements Route {
    public Object handle(Request req, Response res) {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginService loginService = new LoginService();
        LoginResult loginResult = loginService.execute(loginRequest);
        if (loginResult.isSuccess()) {
            res.status(200);
        }
        else {
            if (loginResult.getMessage().equals("Error: Incorrect password.")) {
                res.status(401);
            }
            if (loginResult.getMessage().equals("Error: User not found in the database.")) {
                res.status(401);
            }
            else if (loginResult.getMessage().equals("Error in login.")) {
                res.status(500);
            }
        }
        return new Gson().toJson(loginResult);
    }
}
