package de.ur.mi.android.sportsfreund;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

// cf. https://www.simplifiedcoding.net/firebase-cloud-messaging-tutorial-android/
public class SportsfreundNotificationManager {

    private Context context;
    private static SportsfreundNotificationManager sportsfreundNotificationManager;

    private SportsfreundNotificationManager(Context context){
        this.context = context;
    }
    public static synchronized SportsfreundNotificationManager getInstance(Context context){
        if (sportsfreundNotificationManager == null){
            sportsfreundNotificationManager = new SportsfreundNotificationManager(context);
        }
        return sportsfreundNotificationManager;
    }
    public void displayNotification(String title, String body){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context,Constants.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_my_location_yellow_24dp)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setVibrate(new long[]{1000,1000});

        //using MainActivity just for testing
        Intent intent = new Intent(context,MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null){
            notificationManager.notify(1,mBuilder.build());
        }
    }
}
