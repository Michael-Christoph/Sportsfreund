package de.ur.mi.android.sportsfreund;

public class Game {

    private String gameName;
    private String gameTime;

    public Game(String gameName, String gameTime){
        this.gameName = gameName;
        this.gameTime= gameTime;

    }

    public String getGameName(){
        return gameName;
    }
    public String getGameTime() {
        return gameTime;
    }
}
