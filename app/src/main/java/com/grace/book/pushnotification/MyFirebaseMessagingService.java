package com.grace.book.pushnotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grace.book.R;
import com.grace.book.SplashActivity;
import com.grace.book.utils.Logger;


import org.json.JSONObject;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {

            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            sendNotificationForcard("", object.toString());


        } catch (Exception ex) {
            Logger.debugLog("Exception", ex.getMessage());

        }


    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotificationForcard(String message, String title) {
        String channelId = "Mzadcom";
        String channelName = "Mzadcom";
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.putExtra("push_message", message);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_stat_tab_app_icon)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.ic_stat_tab_app_icon);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_stat_tab_app_icon);
        }
        Notification notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(0, notification);
    }


}