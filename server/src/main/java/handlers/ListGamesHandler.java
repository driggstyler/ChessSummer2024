package handlers;

import results.ListGamesResult;
import services.ListGamesService;
import com.google.gson.Gson;
import spark.*;

public class ListGamesHandler implements Route {
    public Object handle(Request req, Response res) {
        ListGamesService listGamesService = new ListGamesService();
        ListGamesResult listGamesResult = listGamesService.Execute(req.headers("Authorization"));
        if (listGamesResult.isSuccess()) {
            res.status(200);
        }
        else {
            if (listGamesResult.getMessage().equals("Error: Unauthorized")) {
                res.status(401);
            }
            else if(listGamesResult.getMessage().equals("Error in listing games.")) {
                res.status(500);
            }
        }
        return new Gson().toJson(listGamesResult);
    }
}
