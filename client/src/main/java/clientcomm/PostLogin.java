package clientcomm;

import chess.ChessGame;
import models.Game;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LogoutRequest;
import results.CreateGameResult;
import results.JoinGameResult;
import results.ListGamesResult;
import results.LogoutResult;
import chess.ChessBoard;
import ui.ChessboardUI;
import websocket.commands.UserGameCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class PostLogin {
    public void run(int port, String authtoken) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Game> games = new ArrayList<>();
        ServerFacade serverFacade = new ServerFacade(port);
        GamePlayUI gamePlayUI = new GamePlayUI();
        try {
            ListGamesResult listGamesResult = serverFacade.listGames(authtoken);
            games = listGamesResult.getGames();
        } catch (Exception e) {System.out.println("Exception thrown while getting a list of games in PostLogin class (top of run method).");}
        System.out.println("Logged in, to see commands type help.");
        while (true) {
            String input = scanner.nextLine();
            //Now can check command
            if (input.equals("help")) {
                System.out.println("""
                    Options:
                    help - Shows the commands while you are logged in.
                    logout - Logs you out of the server.
                    create game - Creates a new playable game.
                    list games - Lists all of the ongoing games.
                    play game - Join a game you can play.
                    observe game - Observe a game being played.""");
            }
            else if (input.equals("logout")) {
                try {
                    LogoutResult logoutResult = serverFacade.logout(new LogoutRequest(authtoken), authtoken);
                    if (logoutResult.isSuccess()) {
                        System.out.println("Logout was successful");
                        break;
                    } else {System.out.println("Error: Failed to logout.");}
                } catch (Exception e) {System.out.println("Logout failed in PostLogin class due to an exception thrown.");}
            }
            else if (input.equals("create game")) {
                System.out.println("Please type the name of the new game.");
                String gameName = scanner.nextLine();
                try {
                    CreateGameResult createGameResult = serverFacade.createGame(new CreateGameRequest(gameName), authtoken);
                    if (createGameResult.isSuccess()) {
                        ListGamesResult updatedListGamesResult = serverFacade.listGames(authtoken);
                        games = updatedListGamesResult.getGames();
                        System.out.println("The " + gameName + " game has been successfully added.");
                    } else {System.out.println("Game was not successfully added.");}
                } catch (Exception e) {System.out.println("Exception thrown while creating a game in PostLogin class.");}
            }
            else if (input.equals("list games")) {
                try {
                    ListGamesResult listGamesResult = serverFacade.listGames(authtoken);
                    if (listGamesResult.isSuccess()) {
                        games = listGamesResult.getGames();
                        printGames(games);
                    } else {System.out.println("Failed to retrieve list of games.");}
                } catch (Exception e) {
                    System.out.println("Exception thrown while getting a list of games in PostLogin class.");
                }
            }
            else if (input.equals("play game")) {
                int gameNum = -1;
                while (gameNum > games.size() || gameNum < 0) {
                    System.out.println("Please pick a number within the list of games. If stuck, type 0 to go back.");
                    try {
                        String number = scanner.nextLine();
                        gameNum = Integer.parseInt(number);
                    } catch (NumberFormatException ignored){}
                }
                if (gameNum == 0) {continue;}
                String playerColor = "";
                while (!playerColor.equals("WHITE") && ! playerColor.equals("BLACK")) {
                    System.out.println("Please choose your color by typing in all caps WHITE or BLACK.");
                    try {
                        playerColor = scanner.nextLine();
                    } catch (NoSuchElementException ignored) {}
                }
                JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, games.get(gameNum - 1).getGameID());
                try {
                    JoinGameResult joinGameResult = serverFacade.joinGame(joinGameRequest, authtoken);
                    if (joinGameResult.isSuccess()) {
                        gamePlayUI.run(port, authtoken, games.get(gameNum - 1).getGameID(), games.get(gameNum - 1), scanner, playerColor);
                        //ChessboardUI.run(new ChessGame().getBoard(), ChessGame.TeamColor.valueOf(playerColor), new HashSet<>());
                    } else if (joinGameResult.getMessage().equals("Error: Already taken.")) {
                        System.out.println("Sorry, that player position is already taken.");}
                    else {System.out.println("Failed to join game.");}
                } catch (Exception e) {
                    System.out.println("Exception thrown while trying to join game in the PostLogin Class.");
                }
            }
            else if (input.equals("observe game")) {
                int gameNum = -1;
                while (gameNum > games.size() + 1 || gameNum < 0) {
                    System.out.println("Please pick a number within the list of games. If stuck, type 0 to go back.");
                    try {
                        String number = scanner.nextLine();
                        gameNum = Integer.parseInt(number);
                    } catch (NumberFormatException ignored){}
                }
                if (gameNum == 0) {continue;}
                        gamePlayUI.run(port, authtoken, games.get(gameNum - 1).getGameID(), games.get(gameNum - 1), scanner, null);
            } else {System.out.println("Invalid command. Type help to view valid commands.");}
        }
    }
    public void printGames(ArrayList<Game> games) {
        System.out.println("List of games: \n");
        for (int i = 0; i < games.size(); i++) {
            int n = i + 1;
            Game game = games.get(i);
            System.out.println(n + ": " + game.getGameName());
            if (game.getWhiteUsername() != null) {
                System.out.println(game.getWhiteUsername() + " is playing as white");
            } else {
                System.out.println("No one is playing as white");

            }
            if (game.getBlackUsername() != null) {
                System.out.println(game.getBlackUsername() + " is playing as black");
            } else {
                System.out.println("No one is playing as black");

            }
        }
    }
}
