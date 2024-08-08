package handlers;

import requests.RegisterRequest;
import results.LoginResult;
import services.RegisterService;
import com.google.gson.Gson;
import spark.*;

public class RegisterHandler implements Route {
    public Object handle(Request req, Response res) {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        LoginResult loginResult = registerService.execute(registerRequest);
        if (loginResult.isSuccess()) {
            res.status(200);
        }
        else {
            if (loginResult.getMessage().equals("Error: Missing information to register.")) {
                res.status(400);
            }
            else if (loginResult.getMessage().equals("Error: Username already taken.")) {
                res.status(403);
            }
            else if (loginResult.getMessage().equals("Error in registering.")) {
                res.status(500);
            }
        }
        return new Gson().toJson(loginResult);
    }
}
