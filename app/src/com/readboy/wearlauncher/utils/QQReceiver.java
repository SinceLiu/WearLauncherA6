package com.readboy.wearlauncher.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.readboy.wearlauncher.R;

public class QQReceiver extends BroadcastReceiver {
    public static final String TAG = "QQReceiver";
    public static final String QQ_ACTION_NEW_MESSAGE = "com.tencent.qqlite.watch.conversation";
    public static final String NOTIFICATION_TYPE_ADDFRIEND = "1";
    public static final String NOTIFICATION_TYPE_MESSAGE = "2";
    public static final String NOTIFICATION_TYPE_ADDFRIEND_RESULT = "3";
    public static final int MESSAGE_NOTIFY_ID = "qqMessage".hashCode();
    public static final int ADDFRIEND_NOTIFY_ID = "qqAddFriend".hashCode();
    public static final String ACTION_REPLY = "com.tencent.qq.action.conversation.reply";
    public static final String ACTION_REJECT = "com.tencent.qq.action.addFriend.reject";
    public static final String ACTION_ACCEPT = "com.tencent.qq.action.addFriend.accept";
    public static final String ACTION_FOREGROUND = "com.tencent.qq.action.set.foreground";
    public static final String QQ_CHANNEL_ID = "qqChannelId";
    public static final String QQ_CHANNEL_NAME = "qqChannelName";

    public QQReceiver() {

    }

    public QQReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(QQ_ACTION_NEW_MESSAGE);
        context.registerReceiver(this, intentFilter);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (QQ_ACTION_NEW_MESSAGE.equals(action)) {
//            String conversationType = intent.getStringExtra("conversationType");
            String contactName = intent.getStringExtra("contactName");
            String conversationContent = intent.getStringExtra("conversationContent");
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent qqIntent = new Intent(ACTION_FOREGROUND);
//            ComponentName componentName = new ComponentName("com.tencent.qqlite", "com.tencent.mobileqq.activity.SplashActivity");
//            qqIntent.setComponent(componentName);
//            if (NOTIFICATION_TYPE_ADDFRIEND.equals(conversationType)) {
            manager.notify(MESSAGE_NOTIFY_ID, getNotification(context, qqIntent, contactName, conversationContent));
//            }
//            if (NOTIFICATION_TYPE_MESSAGE.equals(conversationType)) {
//                manager.notify(MESSAGE_NOTIFY_ID, getNotification(context, qqIntent, contactName, conversationContent));
//            }
//            if (NOTIFICATION_TYPE_ADDFRIEND_RESULT.equals(conversationType)) {
//                manager.notify(MESSAGE_NOTIFY_ID, getNotification(context, qqIntent, contactName, conversationContent));
//            }
        }
    }


    private Notification getNotification(Context context, Intent intent, String contactName, String conversatrionContent) {
        Bundle bundle = new Bundle();
        bundle.putString("extra_type", "readboy");
        NotificationChannel channel = new NotificationChannel(QQ_CHANNEL_ID, QQ_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.qq_icon);
        builder.setAutoCancel(true);
        builder.setContentTitle(contactName);
        builder.setExtras(bundle);
        builder.setContentText(conversatrionContent);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setChannelId(QQ_CHANNEL_ID);
        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            builder.setContentIntent(pendingIntent);
        }
        return builder.build();
    }
}
