package de.ur.mi.android.sportsfreund;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// cf. https://www.simplifiedcoding.net/firebase-cloud-messaging-tutorial-android/
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String LOG_TAG = "MyFirebaseMessa";

    public static boolean mostRecentTokenSavedInDatabase;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d(LOG_TAG,"entered onMessageReceived");
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0){
            Log.d(LOG_TAG,"message contains data");
        }

        String typeOfChange = remoteMessage.getData().get("typeOfChange");
        String gameName = remoteMessage.getData().get("gameName");
        String gameKey = remoteMessage.getData().get("gameKey");

        SFNotificationManager.getInstance(this).displayNotification(typeOfChange,gameName,gameKey);
    }

    //is called when firebase has built a new token
    @Override
    public void onNewToken(String token){
        Log.d(LOG_TAG,"New Token: " + token);
        GlobalVariables.mostRecentToken = token;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            Log.d(LOG_TAG, "going to send token to Database");
            sendTokenToDatabase(user);
        } else {
            mostRecentTokenSavedInDatabase = false;
            Log.d(LOG_TAG,"user is null");
            Intent switchToSignIn = new Intent(getApplicationContext(),SignInActivity.class);
            switchToSignIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(switchToSignIn);
        }
    }

    public static void sendTokenToDatabase(FirebaseUser user){
        final DatabaseReference firebaseUsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        final String userId = user.getUid();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()){
                            Log.d("FirebaseMessaging", task.getException().toString());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d("FirebaseMessaging", "Token: " + token);
                        firebaseUsersRef.child(userId).setValue(token);
                    }
                });
    }
}
