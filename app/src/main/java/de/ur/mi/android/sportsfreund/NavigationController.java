package de.ur.mi.android.sportsfreund;

// Quelle: Dieser Code orientiert sich zum Teil an die Übungsaufgabe "SightSeer" aus dem Sommersemester 2018

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class NavigationController {

    private static NavigationController mInstance;
    private Context context;
    private LocationManager locationManger;
    private Location lastKnownLocation;
    private String bestProvider;

    public static NavigationController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NavigationController(context);
        }
        return mInstance;
    }

    public Location returnLastKnownLocation (){
        return lastKnownLocation;
    }


    private NavigationController(Context context) {
        this.context = context.getApplicationContext();
        locationManger = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        setBestProvider();
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastKnownLocation = locationManger
                    .getLastKnownLocation(bestProvider);
        }

    }

    private void setBestProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setBearingRequired(true);
        bestProvider = locationManger.getBestProvider(criteria, true);
        if (bestProvider == null) {
            Log.e("setbestprovider", "no Provider set");
        }
    }

}

