package com.cynerds.cyburger.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.helpers.LogHelper;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fabri on 04/11/2017.
 */
public class FirebaseNotificationService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static List<String> receivedNotifications = new ArrayList<>();

    public FirebaseNotificationService() {


    }


    @Override

    public void onMessageReceived(RemoteMessage remoteMessage) {


        Map<String, String> data = remoteMessage.getData();
        if (data != null) {
            String notificationId = data.get("notificationId");
            if (notificationId != null) {
                if (!notificationId.isEmpty()) {
                    if (receivedNotifications.contains(notificationId)) {
                        return;

                    } else {
                        receivedNotifications.add(notificationId);
                    }
                }
            }
        }

        if (remoteMessage.getFrom().equals("/topics/" + CyburgerApplication.getUserTopicName())) {
            if (remoteMessage.getNotification() != null) {

                String title = remoteMessage.getNotification().getTitle(); //get title
                String message = remoteMessage.getNotification().getBody(); //get message


                LogHelper.log("Message Notification Title: " + title);
                LogHelper.log("Message Notification Body: " + message);

                sendNotification(title, message);


            }


        }

    }


    @Override
    public void onDeletedMessages() {


    }


    private void sendNotification(String title, String messageBody) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,

                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)

                .setSmallIcon(R.drawable.cyburger_logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0 /* ID of SubscribeNotification */, notificationBuilder.build());

    }

}