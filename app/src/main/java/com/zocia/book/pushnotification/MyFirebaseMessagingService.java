package com.zocia.book.pushnotification;

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
import com.zocia.book.R;
import com.zocia.book.SplashActivity;
import com.zocia.book.chatmodel.MessageList;
import com.zocia.book.myapplication.Myapplication;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;


import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static org.chat21.android.utils.DebugConstants.DEBUG_NOTIFICATION;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(DEBUG_NOTIFICATION, "From: " + remoteMessage.getFrom());

        try {

            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            int notificaiton_type = Integer.parseInt(object.getString("notification_type"));

            if (notificaiton_type == 0) {
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

                if (topActivity.equalsIgnoreCase("com.zocia.book.ChatActivity")) {
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

                String text = "New message";
                String Message = object.getString("details");
                sendNotificationForcard(Message, text);

            }


        } catch (Exception ex) {
            Logger.debugLog("Exception", ex.getMessage());

        }


    }


//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//        PersistentUser.setPushToken(getApplicationContext(),token);
//        sendRegistrationToServer(token);
//    }

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
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.logo);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.drawable.logo);
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