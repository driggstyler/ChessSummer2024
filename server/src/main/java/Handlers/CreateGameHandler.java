package Handlers;

import Requests.CreateGameRequest;
import Results.CreateGameResult;
import Services.CreateGameService;
import com.google.gson.Gson;
import spark.*;

public class CreateGameHandler implements Route {
    public Object handle(Request req, Response res) {
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameService createGameService = new CreateGameService();
        CreateGameResult createGameResult = createGameService.Execute(createGameRequest, req.headers("Authorization"));
        if (createGameResult.isSuccess()) {
            res.status(200);
        }
        else {
            if (createGameResult.getMessage().equals("Error in creating a new game.")) {
                res.status(500);
            }
            else if (createGameResult.getMessage().equals("Error: Bad request.")) {
                res.status(400);
            }
            else if (createGameResult.getMessage().equals("Error: Unauthorized")) {
                res.status(401);
            }
        }
        return new Gson().toJson(createGameResult);
    }
}
