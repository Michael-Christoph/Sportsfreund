package de.ur.mi.android.sportsfreund;

import java.util.Calendar;

public class SportsfreundHelper {

    //returns a string yyyy-mm-dd
    public static String getCurrentDateAsString(){
        final Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        String currentMonthString = Integer.toString(currentMonth);
        if (currentMonth < 10){
            currentMonthString = "0" + currentMonthString;
        }
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        String currentDayString = Integer.toString(currentDay);
        if (currentDay < 10){
            currentDayString = "0" + currentDayString;
        }
        String currentDate = c.get(Calendar.YEAR) + "-" + currentMonthString + "-" + currentDayString;
        return currentDate;
    }
    public static String getCurrentTimeAsString(){
        final Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);
        String hourString = Integer.toString(currentHour);
        if (currentHour < 10){
            hourString = "0" + hourString;
        }
        String minuteString = Integer.toString(currentMinute);
        if (currentMinute < 10){
            minuteString = "0" + minuteString;
        }
        String currentTimeString = hourString + ":" + minuteString;
        return currentTimeString;
    }
}
