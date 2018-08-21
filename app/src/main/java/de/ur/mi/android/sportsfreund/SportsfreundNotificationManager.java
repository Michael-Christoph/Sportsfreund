package de.ur.mi.android.sportsfreund;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

// cf. https://www.simplifiedcoding.net/firebase-cloud-messaging-tutorial-android/
public class SportsfreundNotificationManager {

    private static final String BODY_TEXT_PARTICPANT_LEFT = "1 Teilnehmer hat sich abgemeldet. Bist du weiterhin dabei?";
    private static final String BODY_TEXT_NEW_PARTICIPANT = "1 neuer Teilnehmer. Bist du auch weiterhin dabei?";
    private Context context;
    private static SportsfreundNotificationManager sportsfreundNotificationManager;
    private String textParticipate = "Ja";
    private String textUnregister = "Absagen";


    private SportsfreundNotificationManager(Context context){
        this.context = context;
    }
    public static synchronized SportsfreundNotificationManager getInstance(Context context){
        if (sportsfreundNotificationManager == null){
            sportsfreundNotificationManager = new SportsfreundNotificationManager(context);
        }
        return sportsfreundNotificationManager;
    }
    public void displayNotification(String typeOfUpdate, String gameName, String gameKey){
        Log.d("SportsfreundNoti","entered displayNotification");
        Constants.debugText += "|||||in SportsfreundNotificationManager, displayNotification: " +
                "typeOfUpdate: " + typeOfUpdate;
        String title = gameName;
        String body = "";
        if (typeOfUpdate.equals("participant left")){
            Constants.debugText += "|||||in SportsfreundNotificationManager: typeOfUpdate wurde als equals zu 'participant left' ausgewertet.";
            body += BODY_TEXT_PARTICPANT_LEFT;
        } else {
            Constants.debugText += "|||||in SportsfreundNotificationManager: typeOfUpdate wurde als nicht equals zu 'participant left' ausgewertet.";
            body += BODY_TEXT_NEW_PARTICIPANT;
        }

        //intents for notification interaction
        //Intent intent_participate = new Intent(context,NotificationInteractionReceiver.class);
        Intent intent_participate = new Intent(context.getString(R.string.ACTION_PARTICIPATE));
        Log.d("SportsfreundNoti","action: " + context.getString(R.string.ACTION_PARTICIPATE));
        //intent_participate.setAction(Constants.ACTION_PARTICIPATE);
        PendingIntent pendingIntentParticipate = PendingIntent.getBroadcast(context,12345,intent_participate,PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent intent_unregister = new Intent(context,NotificationInteractionReceiver.class);
        Intent intent_unregister = new Intent(context.getString(R.string.ACTION_UNREGISTER_FROM_GAME));
        Log.d("SportsfreundNoti","action: " + context.getString(R.string.ACTION_UNREGISTER_FROM_GAME));
        intent_unregister.putExtra("gameKey",gameKey);
        Log.d("SportsfreundNoti","gameKey: " + gameKey);
        //intent_unregister.setAction(Constants.ACTION_UNREGISTER);
        PendingIntent pendingIntentUnregister = PendingIntent.getBroadcast(context,12345,intent_unregister,PendingIntent.FLAG_UPDATE_CURRENT);

        //https://stackoverflow.com/questions/6357450/android-multiline-notifications-notifications-with-longer-text
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(body);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context,Constants.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_my_location_yellow_24dp)
                    //.setContentTitle(title)
                    //.setContentText(body)
                    .setStyle(bigTextStyle)
                    .addAction(new NotificationCompat.Action(R.drawable.ic_person_green_24dp,textParticipate,pendingIntentParticipate))
                    .addAction(new NotificationCompat.Action(R.drawable.ic_person_red_24dp,textUnregister,pendingIntentUnregister))
                    .setVibrate(new long[]{1000,1000})
                    .setAutoCancel(true)
                    .setOngoing(false);

        /*
        //using MainActivity just for testing
        Intent intent = new Intent(context,MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        */

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null){
            //interactive notification on android only works if there are no other notifications!
            notificationManager.cancelAll();
            notificationManager.notify(Constants.SPORTSFREUND_NOTIFICATION_ID,mBuilder.build());
        }
    }
}
