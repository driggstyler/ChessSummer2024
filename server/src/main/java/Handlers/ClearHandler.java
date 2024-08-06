package Handlers;

import Results.ClearResult;

import java.io.IOException;

import Services.ClearService;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.ietf.jgss.GSSContext;
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
