package de.ur.mi.android.sportsfreund;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

// cf. https://www.simplifiedcoding.net/firebase-cloud-messaging-tutorial-android/
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    //cf. Firebase Cloud Messaging Android Studio Assistant
    //called when the token is generated
    @Override
    public void onTokenRefresh(){
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("MyFirebaseInstan", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
}
