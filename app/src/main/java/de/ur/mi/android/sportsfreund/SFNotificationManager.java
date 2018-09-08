package de.ur.mi.android.sportsfreund;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

// cf. https://www.simplifiedcoding.net/firebase-cloud-messaging-tutorial-android/
public class SFNotificationManager {

    private static final String LOG_TAG = "SFNotificationManager";

    private static SFNotificationManager sportsfreundNotificationManager;

    private Context context;

    private SFNotificationManager(Context context){
        this.context = context;
    }
    public static synchronized SFNotificationManager getInstance(Context context){
        if (sportsfreundNotificationManager == null){
            sportsfreundNotificationManager = new SFNotificationManager(context);
        }
        return sportsfreundNotificationManager;
    }

    public void displayNotification(String typeOfUpdate, String gameName, String gameKey){
        Log.d(LOG_TAG,"entered displayNotification");
        String title = gameName;
        String body = "";
        if (typeOfUpdate.equals("participant left")){
            body += context.getString(R.string.notification_text_participant_left);
        } else {
            body += context.getString(R.string.notification_text_new_participant);
        }

        //intents for notification interaction
        Intent intent_participate = new Intent(context.getString(R.string.ACTION_PARTICIPATE));
        PendingIntent pendingIntentParticipate = PendingIntent.getBroadcast(context,12345,intent_participate,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent_unregister = new Intent(context.getString(R.string.ACTION_UNREGISTER_FROM_GAME));
        intent_unregister.putExtra(GlobalVariables.KEY_GAME_KEY,gameKey);
        PendingIntent pendingIntentUnregister = PendingIntent.getBroadcast(context,12345,intent_unregister,PendingIntent.FLAG_UPDATE_CURRENT);

        //cf. https://stackoverflow.com/questions/6357450/android-multiline-notifications-notifications-with-longer-text
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(body);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, GlobalVariables.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_zeichnungalpha)
                    .setStyle(bigTextStyle)
                    .addAction(new NotificationCompat.Action(R.drawable.ic_person_green_24dp,context.getString(R.string.notification_text_participate),pendingIntentParticipate))
                    .addAction(new NotificationCompat.Action(R.drawable.ic_person_red_24dp,context.getString(R.string.notification_text_unregister),pendingIntentUnregister))
                    .setVibrate(new long[]{0,1000})
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setChannelId(GlobalVariables.CHANNEL_ID);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null){
            //interactive notification on older android devices only works if there are no other notifications!
            notificationManager.cancelAll();
            //cf. https://developer.android.com/training/notify-user/channels
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                CharSequence name = GlobalVariables.CHANNEL_NAME;
                String description = GlobalVariables.CHANNEL_DESCRIPTION;
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(GlobalVariables.CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);

                context.registerReceiver(new SFNotificationInteractionReceiver(),
                        new IntentFilter(context.getString(R.string.ACTION_PARTICIPATE)));
                context.registerReceiver(new SFNotificationInteractionReceiver(),
                        new IntentFilter(context.getString(R.string.ACTION_UNREGISTER_FROM_GAME)));
            }
            notificationManager.notify(GlobalVariables.SPORTSFREUND_NOTIFICATION_ID,mBuilder.build());
        }
    }
}
