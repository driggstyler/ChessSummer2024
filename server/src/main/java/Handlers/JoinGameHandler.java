package Handlers;

import Requests.JoinGameRequest;
import Results.JoinGameResult;
import Services.JoinGameService;
import com.google.gson.Gson;
import spark.*;

public class JoinGameHandler implements Route {
    public Object handle (Request req, Response res) {
        JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        JoinGameService joinGameService = new JoinGameService();
        JoinGameResult joinGameResult = joinGameService.Execute(joinGameRequest, req.headers("Authorization"));
        if (joinGameResult.isSuccess()) {
            res.status(200);
        }
        else {
            if (joinGameResult.getMessage().equals("Error in joining game.")) {
                res.status(500);
            }
            else if (joinGameResult.getMessage().equals("Error: Bad request.")) {
                res.status(400);
            }
            else if (joinGameResult.getMessage().equals("Error: Unauthorized.")) {
                res.status(401);
            }
            else if (joinGameResult.getMessage().equals("Error: Already taken.")) {
                res.status(403);
            }
        }
        return new Gson().toJson(joinGameResult);
    }
}
