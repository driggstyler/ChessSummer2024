package handlers;

import results.LogoutResult;
import services.LogoutService;
import com.google.gson.Gson;
import spark.*;

public class LogoutHandler implements Route {
    public Object handle(Request req, Response res) {
        LogoutService logoutService = new LogoutService();
        LogoutResult logoutResult = logoutService.execute(req.headers("Authorization"));
        if (logoutResult.isSuccess()) {
            res.status(200);
        }
        else {
            if (logoutResult.getMessage().equals("Error: Unauthorized")) {
                res.status(401);
            }
            else if (logoutResult.getMessage().equals("Error in logging out.")) {
                res.status(500);
            }
        }
        return new Gson().toJson(logoutResult);
    }
}
