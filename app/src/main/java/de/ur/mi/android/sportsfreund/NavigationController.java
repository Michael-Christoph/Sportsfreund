package de.ur.mi.android.sportsfreund;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

// Quelle: Dieser Code orientiert sich zum Teil an die Übungsaufgabe "SightSeer" aus dem Sommersemester 2018
public class NavigationController {

    private static NavigationController mInstance = null;
    private Context context;
    private static LocationManager locationManager;
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

    public boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private NavigationController(Context context) {
        this.context = context;
        refreshFields();
    }
    public void refreshFields(){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        setBestProvider();

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            lastKnownLocation = locationManager
                    .getLastKnownLocation(bestProvider);
            Log.d("NavigationController", "gps permission wurde erteilt");
        }
    }

    private void setBestProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        bestProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider == null) {
            Log.e("NavigationController", "no Provider set");
        }
    }

}

