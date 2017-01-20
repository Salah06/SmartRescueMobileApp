package com.smartcity.smartrescue.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.SimpleArrayMap;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartcity.smartrescue.ui.MainActivity;
import com.smartcity.smartrescue.service.Command;
import com.smartcity.smartrescue.service.MapCommand;
import com.smartcity.smartrescue.service.RequestCommand;
import com.smartcity.smartrescue.service.SituationCommand;

import java.util.Map;

import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final int NOTIFICATION_REQCODE = 0;

    private static SimpleArrayMap<String, Command> commands = new SimpleArrayMap<>();
    static {
        commands.put(RequestCommand.TRIGGER, new RequestCommand());
        commands.put(MapCommand.TRIGGER, new MapCommand());
        commands.put(SituationCommand.TRIGGER, new SituationCommand());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String command = remoteMessage.getData().get("command");
        String data = remoteMessage.getData().get("data");

        showNotification(command, data);

        Timber.d(command);
        Timber.d(data);
        JsonParser parser = new JsonParser();
        JsonObject jsonData = parser.parse(data).getAsJsonObject();
        if (!command.isEmpty()) {
            Command cmd = commands.get(command);
            if (null != cmd) {
                cmd.setContext(this);
                try {
                    cmd.run(jsonData);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
    }

    private void showNotification(String title, String msg) {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, NOTIFICATION_REQCODE, i,
            PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pi);

        Timber.d(msg);

        int notifId = 0;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(notifId, builder.build());
    }
}
