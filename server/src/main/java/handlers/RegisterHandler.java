package handlers;

import requests.RegisterRequest;
import results.RegisterResult;
import services.RegisterService;
import com.google.gson.Gson;
import spark.*;

public class RegisterHandler implements Route {
    public Object handle(Request req, Response res) {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        RegisterResult registerResult = registerService.Execute(registerRequest);
        if (registerResult.isSuccess()) {
            res.status(200);
        }
        else {
            if (registerResult.getMessage().equals("Error: Missing information to register.")) {
                res.status(400);
            }
            else if (registerResult.getMessage().equals("Error: Username already taken.")) {
                res.status(403);
            }
            else if (registerResult.getMessage().equals("Error in registering.")) {
                res.status(500);
            }
        }
        return new Gson().toJson(registerResult);
    }
}
