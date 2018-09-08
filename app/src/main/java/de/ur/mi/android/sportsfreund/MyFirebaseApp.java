package de.ur.mi.android.sportsfreund;

import android.support.multidex.MultiDexApplication;
import com.google.firebase.database.FirebaseDatabase;

//adds offline persistence even if app is destroyed.
public class MyFirebaseApp extends MultiDexApplication {
    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
