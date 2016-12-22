package com.npincomplete.pragyanhackathonamb;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        GPSTracker tracker = new GPSTracker(this);


        Intent intent = new Intent(
                android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?" +
                        "saddr=" +
                        remoteMessage.getData().get("Vehicle_Lat") +
                        "," +
                        remoteMessage.getData().get("Vehicle_Long") +
                        "&daddr=" +
                        remoteMessage.getData().get("Lat") +
                        "," +
                        remoteMessage.getData().get("Long")
                ));

        String temp = "You are picking " + remoteMessage.getData().get("Name") + " for the cause of " +
                remoteMessage.getData().get("Updated_Description")
                + " with Phone number " + remoteMessage.getData().get("Phone");
        sendNotification(temp);

        //PHONE, NAME, TYPE, UDPATED_DESC
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        intent.putExtra("fromNotification", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_icon2)
                .setContentTitle("Details")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
               // .addAction(R.drawable.common_google_signin_btn_icon_dark, "Drop Patient", pIntent)
                ;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        //* ID of notification *//*, notificationBuilder.build());*/
    }
}
