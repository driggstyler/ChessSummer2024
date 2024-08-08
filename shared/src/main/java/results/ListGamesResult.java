package results;

import models.Game;

import java.util.ArrayList;

public class ListGamesResult {
    private ArrayList<Game> games = new ArrayList<>();
    private String message;
    private boolean success;

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
