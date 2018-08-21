package de.ur.mi.android.sportsfreund;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

// cf. https://stackoverflow.com/questions/15350998/determine-addaction-click-for-android-notifications
public class NotificationInteractionReceiver extends BroadcastReceiver {
    FirebaseAuth auth;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("Receiver","action: " + action);
        if (action.equals(context.getString(R.string.ACTION_PARTICIPATE))){
            Log.d("Receiver","pressed participate");
            Toast.makeText(context,R.string.toastThanksForFeedback,Toast.LENGTH_SHORT).show();
        } else if (action.equals(context.getString(R.string.ACTION_UNREGISTER_FROM_GAME))){
            Log.d("Receiver","pressed unregister");
            Constants.debugText += "||| in Receiver: unregister pressed registered";

            String gameKey = intent.getStringExtra("gameKey");
            Log.d("Receiver","gameKey: " + gameKey);
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null){
                MainActivity.getItemAdapter().removeParticipantFromGameViaKey(gameKey,auth.getCurrentUser().getUid());
            } else {
                Log.d("Receiver","user is null");
                //Alert, dass leider keine Abmeldung möglich; User möge sich bitte zuerst anmelden
                // und dann manuell vom Spiel abmelden.
            }


        } else {
            Log.d("Receiver","Hm, we should never have got to this place.");
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null){

            notificationManager.cancel(Constants.SPORTSFREUND_NOTIFICATION_ID);
        }
    }
}
