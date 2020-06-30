package com.grace.book.pushnotification;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.R;
import com.grace.book.SplashActivity;
import com.grace.book.chatmodel.MessageList;
import com.grace.book.myapplication.Myapplication;
import com.grace.book.utils.Logger;


import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {

            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            GsonBuilder builder = new GsonBuilder();
            Gson mGson = builder.create();
            MessageList mServermessage = (MessageList) mGson.fromJson(object.toString(), MessageList.class);
            ActivityManager am = (ActivityManager) getApplicationContext()
                    .getSystemService(Activity.ACTIVITY_SERVICE);
            String packageName = am.getRunningTasks(1).get(0).topActivity
                    .getPackageName();
            List<ActivityManager.RunningTaskInfo> taskInfo = am
                    .getRunningTasks(1);
            String topActivity = taskInfo.get(0).topActivity
                    .getClassName();

            Intent intent = new Intent(Myapplication.NEW_MESSAGE_ACTION);
            Bundle extra = new Bundle();
            extra.putSerializable("objects", mServermessage);
            intent.putExtra("extra", extra);
            sendBroadcast(intent);

            if (mServermessage.notificaiton_type.equalsIgnoreCase("0")) {
                if (topActivity.equalsIgnoreCase("com.grace.book.ChatActivity")) {
                    Logger.debugLog("ChatActivity", "OPEN");

                } else {
                    Logger.debugLog("ChatActivity", "NO");
                    String type = mServermessage.getType();
                    String text = "New message";
                    String message = "";
                    if (type.equalsIgnoreCase("0"))
                        message = mServermessage.getMessage();
                    else
                        message = "Send a media";
                    sendNotificationForcard(text, message);

                }
            } else {
                Logger.debugLog("ChatActivity", "ChatActivity");
                String text = "New message";
                sendNotificationForcard(text, mServermessage.getMessage());

            }


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
        String channelId = "Gracebook";
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