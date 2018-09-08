package de.ur.mi.android.sportsfreund;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

// cf. https://stackoverflow.com/questions/15350998/determine-addaction-click-for-android-notifications
public class SFNotificationInteractionReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "NotifInteractionRec";
    FirebaseAuth auth;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(LOG_TAG,"action: " + action);
        if (action.equals(context.getString(R.string.ACTION_PARTICIPATE))){
            Log.d(LOG_TAG,"pressed buttonParticipate");
            Toast.makeText(context,R.string.toastThanksForFeedback,Toast.LENGTH_SHORT).show();
        } else if (action.equals(context.getString(R.string.ACTION_UNREGISTER_FROM_GAME))){
            Log.d(LOG_TAG,"pressed unregister");

            String gameKey = intent.getStringExtra(GlobalVariables.KEY_GAME_KEY);
            Log.d(LOG_TAG,"gameKey: " + gameKey);
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null){
                ItemAdapter itemAdapter;
                try {
                    itemAdapter = MainActivity.getItemAdapter();
                    itemAdapter.removeParticipantFromGameViaKey(gameKey,auth.getCurrentUser().getUid(),context,
                            context.getString(R.string.toast_removedYou));
                } catch (Exception e){
                    Intent i = new Intent(context,MainActivity.class);
                    i.putExtra(GlobalVariables.KEY_MOVE_TASK_TO_BACK,true);
                    i.putExtra(GlobalVariables.KEY_GAME_TO_DELETE,gameKey);
                    i.putExtra(GlobalVariables.KEY_USER_ID,auth.getCurrentUser().getUid());
                    context.startActivity(i);
                }
            } else {
                Log.d(LOG_TAG,"user is null");
            }
        } else {
            Log.d(LOG_TAG,"Hm, we should never have got to this place.");
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null){
            notificationManager.cancel(GlobalVariables.SPORTSFREUND_NOTIFICATION_ID);
        }
    }
}
