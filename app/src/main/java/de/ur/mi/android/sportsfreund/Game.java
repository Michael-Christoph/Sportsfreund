package de.ur.mi.android.sportsfreund;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;

import java.sql.Date;
import java.sql.Time;


@Entity(tableName = "games")
public class Game {




    @PrimaryKey
    private int key;

    @ColumnInfo (name = "location")
    private Location location;

    @ColumnInfo (name = "title")
    private String title;

    @ColumnInfo (name = "description")
    private String description;

    @ColumnInfo (name = "time")
    private Time time;

    @ColumnInfo (name = "date")
    private Date date;

    public void setKey (int key)  {
        this.key = key;
    }

    public int getKey()  {
        return key;
    }

    public void setLocation (Location location)  {
        this.location = location;
    }

    public Location getLocation ()  {
        return location;
    }

    public void setTitle (String title)  {
        this.title = title;
    }

    public String getTitle ()  {
        return title;
    }

    public void setDescription (String description)  {
        this.description = description;
    }

    public String getDescription ()  {
        return description;
    }

    public void setTime (Time time)  {
        this.time = time;
    }

    public Time getTime ()  {
        return time;
    }

    public void setDate (Date date)  {
        this.date = date;
    }

    private Date getDate()  {
        return date;
    }


}
