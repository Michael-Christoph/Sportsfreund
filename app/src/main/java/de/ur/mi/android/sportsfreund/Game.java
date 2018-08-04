package de.ur.mi.android.sportsfreund;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;


public class Game implements Serializable{

    private String gameName;
    private String gameTime;
    private String gameLocation;
    private String gameDate;

    //@JsonIgnore
    private String key;



    private ArrayList<String> participants = new ArrayList<>();

    // Default constructor required by Jackson/GSON for calls to
    // DataSnapshot.getValue(User.class)
    //cf. https://www.androidhive.info/2016/10/android-working-with-firebase-realtime-database/
    public Game(){

    }

    public Game(String gameName, String gameTime, String gameLocation, String uid){
        this.gameName = gameName;
        this.gameTime= gameTime;
        this.gameLocation = gameLocation;
        participants.add(uid);
        gameDate = "dummyDate";


    }

    public String getGameName(){
        return gameName;
    }
    public String getGameTime() {
        return gameTime;
    }
    public String getGameLocation(){return gameLocation;}
    public ArrayList<String> getParticipants() {
        return participants;
    }
    //nur ein Platzhalter für Max' Methode
    public double getProximity(int lastKnownLocation){
        String firstLatDigits = gameLocation.substring(5,20);
        return Double.parseDouble(firstLatDigits)-lastKnownLocation;
    }
    public void addParticipant(String uid){
        participants.add(uid);
    }
    public void removeParticipant(String uid){
        participants.remove(uid);
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValues(Game updatedGame) {
        this.gameLocation = updatedGame.gameLocation;
        this.gameName = updatedGame.gameName;
        this.gameTime = updatedGame.gameTime;
    }
    public String getDate() {
        return gameDate;
    }

    public void setDate(String date) {
        this.gameDate = date;
    }
}

