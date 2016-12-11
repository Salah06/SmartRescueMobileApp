package com.smartcity.smartrescue.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.smartcity.smartrescue.MainActivity;

import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final int NOTIFICATION_REQCODE = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getData().get("msg"));
    }

    private void showNotification(String msg) {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, NOTIFICATION_REQCODE, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
            .setAutoCancel(true)
            .setContentTitle(msg)
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pi);

        Timber.d(msg);

        int notifId = 0;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(notifId, builder.build());
    }
}
