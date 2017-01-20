package com.yuyu.module.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yuyu.module.R
import com.yuyu.module.activity.LoginActivity
import com.yuyu.module.utils.Constant

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val userName = remoteMessage.data[getString(R.string.user_name)]
        val message = remoteMessage.data[getString(R.string.message)]
        sendNotification(userName, message)
    }

    private fun sendNotification(userName: String?, message: String?) {
        Intent(this, LoginActivity::class.java).let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            PendingIntent.getActivity(this, Constant.NOTIFICATION_ID, it,
                    PendingIntent.FLAG_ONE_SHOT).let {

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(Constant.NOTIFICATION_ID, NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("$userName: $message")
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(it)
                        .build())
            }
        }
    }

}

/*
package com.yuyu.module.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yuyu.module.R;
import com.yuyu.module.activity.LoginActivity;
import com.yuyu.module.utils.Constant;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String userName = remoteMessage.getData().get(getString(R.string.user_name));
        String message = remoteMessage.getData().get(getString(R.string.message));
        sendNotification(userName, message);
    }

    private void sendNotification(String userName, String message) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Constant.NOTIFICATION_ID, intent,
        PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constant.NOTIFICATION_ID, new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(userName + ": " + message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .build());
    }
}
*/
