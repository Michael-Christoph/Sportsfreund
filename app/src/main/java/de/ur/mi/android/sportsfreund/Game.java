package de.ur.mi.android.sportsfreund;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.Exclude;
import java.util.ArrayList;

public class Game implements Parcelable{

    //cf. http://www.vogella.com/tutorials/AndroidParcelable/article.html#using-auto-value-to-generate-parcelable-implementations
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    private String gameName;
    private String gameTime;
    private String gameDate;
    private double gameLat;
    private double gameLong;
    private ArrayList<String> participants = new ArrayList<>();

    //signalizes Firebase/GSON that field can be ignored for game representation in database
    @Exclude
    private String key;

    // Default constructor required by Jackson/GSON for calls to
    // DataSnapshot.getValue(User.class)
    //cf. https://www.androidhive.info/2016/10/android-working-with-firebase-realtime-database/
    public Game(){

    }

    public Game(Context context, String gameName,String gameDate, String gameTime, double locLat, double locLong, String uid) {
        this.gameName = gameName;
        this.gameDate = gameDate;
        this.gameTime = gameTime;
        this.gameLat = locLat;
        this.gameLong = locLong;
        participants.add(uid);
    }

    public Float distanceToGame(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                gpsIsEnabled(context)){
            NavigationController.getInstance(context).refreshFields();
            Location lastKnownLocation = NavigationController.getInstance(context).returnLastKnownLocation();
            if (lastKnownLocation != null){
                Location gameLocation = new Location( "" );
                gameLocation.setLatitude(this.gameLat);
                gameLocation.setLongitude(this.gameLong);
                float distance = lastKnownLocation.distanceTo(gameLocation);
                return distance;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private boolean gpsIsEnabled(Context context) {
        boolean gpsEnabled= NavigationController.getInstance(context).isGpsEnabled();
        return gpsEnabled;
    }

    public String getGameName(){
        return gameName;
    }
    public String getGameDate() {
        return gameDate;
    }
    public String getGameTime() {
        return gameTime;
    }
    public double getGameLat(){
        return gameLat;
    }
    public double getGameLong(){
        return gameLong;
    }
    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void addParticipant(String uid, Context context){
        if (participants.contains(uid)){
            Toast.makeText(context,R.string.participant_already_added,Toast.LENGTH_LONG).show();
        } else {
            participants.add(uid);
        }
    }
    public void removeParticipant(String uid, Context context){
        if (participants.contains(uid)){
            participants.remove(uid);
        } else {
            Toast.makeText(context,R.string.participant_not_there,Toast.LENGTH_LONG).show();
        }
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValues(Game updatedGame) {
        this.gameName = updatedGame.gameName;
        this.gameDate = updatedGame.gameDate;
        this.gameTime = updatedGame.gameTime;
        this.gameLat = updatedGame.gameLat;
        this.gameLong = updatedGame.gameLong;
        this.participants = updatedGame.participants;
    }

    //http://www.vogella.com/tutorials/AndroidParcelable/article.html#using-auto-value-to-generate-parcelable-implementations
    public Game(Parcel in){
        this.gameName = in.readString();
        this.gameDate =  in.readString();
        this.gameTime = in.readString();
        this.gameLat = in.readDouble();
        this.gameLong = in.readDouble();
        this.participants = in.readArrayList(ArrayList.class.getClassLoader());
        this.key = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.gameName);
        parcel.writeString(this.gameDate);
        parcel.writeString(this.gameTime);
        parcel.writeDouble(this.gameLat);
        parcel.writeDouble(this.gameLong);
        parcel.writeList(this.participants);
        parcel.writeString(this.key);
    }
}

