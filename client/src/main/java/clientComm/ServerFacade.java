package clientComm;

import Requests.*;
import Results.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ServerFacade {
    private int port;
    public ServerFacade(int port) {
        this.port = port;
    }
    private <T> T run(String method, String path, Object body, Class<T> Tclass, String authtoken) throws Exception {
        //if (args.length >= 2) {
            //var method = method;
            var url = "http://localhost:"+ port + path;
            //var path = args[1];
            //var body = args.length == 3 ? args[2] : "";
            //var body = body;

            HttpURLConnection http = sendRequest(url, method, body, authtoken);
            return receiveResponse(http, Tclass);
//        } else {
//            System.out.println("ClientCurlExample <method> <url> [<body>]");
//            return null;
//        }
    }
    public LoginResult login(LoginRequest loginRequest) throws Exception{
        return run("POST", "/session", loginRequest, LoginResult.class, "");
    }
    public RegisterResult register(RegisterRequest registerRequest) throws Exception {
        return run("POST", "/user", registerRequest, RegisterResult.class, "");
    }
    public LogoutResult logout(LogoutRequest logoutRequest, String authtoken) throws Exception{
        return run("DELETE", "/session", logoutRequest, LogoutResult.class, authtoken);
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authtoken) throws Exception {
        return run("POST", "/game", createGameRequest, CreateGameResult.class, authtoken);
    }
    public ListGamesResult listGames(String authtoken) throws Exception{
        return run("GET", "/game", null, ListGamesResult.class, authtoken);
    }
    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authtoken) throws Exception{
        return run("PUT", "/game", joinGameRequest, JoinGameResult.class, authtoken);
    }

    private static HttpURLConnection sendRequest(String url, String method, Object body, String authtoken) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        http.addRequestProperty("authorization", authtoken);
        writeRequestBody(body, http);
        http.connect();
        System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
        return http;
    }

    private static void writeRequestBody(Object body, HttpURLConnection http) throws IOException {
        if (body != null) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                String json= new Gson().toJson(body);
                outputStream.write(json.getBytes());
            }
        }
    }

    private <T> T receiveResponse(HttpURLConnection http, Class<T> Tclass) throws IOException {
        var statusCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();

        T responseBody = readResponseBody(http, Tclass);
        System.out.printf("= Response =========\n[%d] %s\n\n%s\n\n", statusCode, statusMessage, responseBody);
        //Do stuff based on response
        return responseBody;
    }

    private static <T> T readResponseBody(HttpURLConnection http, Class<T> Tclass) throws IOException {
        T responseBody = null;
        int status = http.getResponseCode();
        if (status == 200) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                responseBody = new Gson().fromJson(inputStreamReader, Tclass);
            }
        }
        else {
            try (InputStream respBody = http.getErrorStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                responseBody = new Gson().fromJson(inputStreamReader, Tclass);
            }
        }

        return responseBody;
    }
}
