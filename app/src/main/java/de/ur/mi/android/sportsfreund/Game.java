package de.ur.mi.android.sportsfreund;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.location.Location;


@Entity(tableName = "games")

public class Game {

    private String gameName;
    private String gameTime;

    private double gameLat;
    private double gameLong;
    private Context context;

    private Location gameLocation;

    private double gameProximity;


    @PrimaryKey
    private int key;

    public Game(String gameName, String gameTime, double gameLat, double gameLong){
        this.gameName = gameName;
        this.gameTime = gameTime;
        this.gameLat = gameLat;
        this.gameLong = gameLong;
        gameLocation.setLatitude( gameLat );
        gameLocation.setLongitude( gameLong );
    }

    public float distanceToGame (Location gameLocation){
        Location lastKnownLocation = NavigationController.getInstance( context ).returnLastKnownLocation();

        return lastKnownLocation.distanceTo( gameLocation );
    }

    public String getGameName(){
        return gameName;
    }
    public String getGameTime() {
        return gameTime;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }


    public double getGameLong() {
        return gameLong;
    }

    public void setGameLong(double gameLong) {
        this.gameLong = gameLong;
    }

    public double getGameLat() {
        return gameLat;
    }

    public void setGameLat(double gameLat) {
        this.gameLat = gameLat;
    }

    public double getGameProximity() {
        return gameProximity;
    }

}

