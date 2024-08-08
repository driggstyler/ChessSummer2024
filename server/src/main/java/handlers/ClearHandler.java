package handlers;

import results.ClearResult;

import services.ClearService;
import com.google.gson.Gson;
import spark.*;

public class ClearHandler implements Route {
    public Object handle(Request req, Response res ) {
        ClearService clearService = new ClearService();
        ClearResult clearResult = clearService.Execute();
        if (clearResult.isSuccess()) {
            res.status(200);
        }
        else {
            res.status(500);
        }
        return new Gson().toJson(clearResult);
    }
}
