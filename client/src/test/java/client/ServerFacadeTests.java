package client;

import Requests.*;
import Results.*;
import Services.ClearService;
import clientComm.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        ClearService clearService = new ClearService();
        clearService.Execute();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(registerRequest);
        assertTrue(authData.isSuccess());
    }
    @Test
    void registerFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(registerRequest);
        RegisterRequest registerRequest2 = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(registerRequest2);
        assertFalse(authData.isSuccess());
    }
    @Test
    void login() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("player1", "password");
        LoginResult loginResult = facade.login(loginRequest);
        assertTrue(loginResult.isSuccess());
    }
    @Test
    void loginFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("player2", "password2");
        LoginResult loginResult = facade.login(loginRequest);
        assertFalse(loginResult.isSuccess());
    }
    @Test
    void logout() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.getAuthtoken());
        LogoutResult logoutResult = facade.logout(logoutRequest, registerResult.getAuthtoken());
        assertTrue(logoutResult.isSuccess());
    }
    @Test
    void logoutFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.getAuthtoken());
        LogoutResult logoutResult = facade.logout(logoutRequest, "notRealAuthtoken");
        assertFalse(logoutResult.isSuccess());
    }
    @Test
    void createGame() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("firstGame");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, registerResult.getAuthtoken());
        assertTrue(createGameResult.isSuccess());
    }
    @Test
    void createGameFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("firstGame");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, "notRealAuthtoken");
        assertFalse(createGameResult.isSuccess());
    }
    @Test
    void listGames() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        ListGamesResult listGamesResult = facade.listGames(registerResult.getAuthtoken());
        assertTrue(listGamesResult.isSuccess());
    }
    @Test
    void listGamesFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(registerRequest);
        ListGamesResult listGamesResult = facade.listGames("notRealAuthtoken");
        assertFalse(listGamesResult.isSuccess());
    }
    @Test
    void joinGame() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("matchGame");
        facade.createGame(createGameRequest, registerResult.getAuthtoken());
        CreateGameRequest createGameRequest2 = new CreateGameRequest("rematchGame");
        facade.createGame(createGameRequest2, registerResult.getAuthtoken());
        ListGamesResult listGamesResult = facade.listGames(registerResult.getAuthtoken());
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", listGamesResult.getGames().get(1).getGameID());
        JoinGameResult joinGameResult = facade.joinGame(joinGameRequest, registerResult.getAuthtoken());
        assertTrue(joinGameResult.isSuccess());
    }
    @Test
    void joinGameFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 99999);
        JoinGameResult joinGameResult = facade.joinGame(joinGameRequest, registerResult.getAuthtoken());
        assertFalse(joinGameResult.isSuccess());
    }


}
