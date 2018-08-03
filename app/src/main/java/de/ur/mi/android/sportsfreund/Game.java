package de.ur.mi.android.sportsfreund;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Entity(tableName = "games")

public class Game {

    private String gameName;
    private String gameTime;
    private String gameLocation;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    //cf. https://www.androidhive.info/2016/10/android-working-with-firebase-realtime-database/
    public Game(){

    }

    public Game(String gameName, String gameTime, String gameLocation){
        this.gameName = gameName;
        this.gameTime= gameTime;
        this.gameLocation = gameLocation;

    }

    public String getGameName(){
        return gameName;
    }
    public String getGameTime() {
        return gameTime;
    }
    public String getGameLocation(){return gameLocation;}

    //nur ein Platzhalter für Max' Methode
    public double getProximity(int lastKnownLocation){
        String firstLatDigits = gameLocation.substring(5,20);
        return Double.parseDouble(firstLatDigits)-lastKnownLocation;
    }

}

