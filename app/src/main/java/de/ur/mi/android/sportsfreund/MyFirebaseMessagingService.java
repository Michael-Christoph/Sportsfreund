package de.ur.mi.android.sportsfreund;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
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

    public static boolean mostRecentTokenSavedInDatabase;
    private static final String titelFehlermeldung = "WICHTIGER HINWEIS";
    private static final String textFehlermeldung = "Damit du über neue Infos zu deinen Spielen " +
            "werden kannst, musst du dich anmelden.";
    private static final String textPositiveButton = "Zum Anmeldebildschirm";
    private static final String textNegativeButton = "Jetzt nicht";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d("FirebaseMessaging","entered onMessageReceived");
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0){
            //read data from message (key-value-pairs)
        }
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        //use title and body to build a notification

        SportsfreundNotificationManager.getInstance(this).displayNotification(title,body);


    }
    @Override
    public void onNewToken(String token){
        Log.d("FirebaseMessaging","New Token: " + token);
        Constants.mostRecentToken = token;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            Log.d("FirebaseMessaging: ", "going to send token to Database");
            sendTokenToDatabase(user);
        } else {
            mostRecentTokenSavedInDatabase = false;
            Log.d("FirebaseMessaging","user is null");
            //showLoginRecommendationDialog();
            Intent switchToSignIn = new Intent(getApplicationContext(),SignInActivity.class);
            switchToSignIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(switchToSignIn);
        }

        /*
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("MyFirebaseInstan", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
        */
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
                        mostRecentTokenSavedInDatabase = true;
                    }
                });


    }
    private void showLoginRecommendationDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(titelFehlermeldung);
        dialogBuilder.setMessage(textFehlermeldung);
        dialogBuilder.setPositiveButton(textPositiveButton,new Dialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                Intent startLoginActivity = new Intent(MyFirebaseMessagingService.this,SignInActivity.class);
                startActivity(startLoginActivity);
            }
        });
        dialogBuilder.setNegativeButton(textNegativeButton,new Dialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                Toast.makeText(getApplicationContext(),R.string.toastReminder,Toast.LENGTH_SHORT).show();
            }
        });

    }
}
