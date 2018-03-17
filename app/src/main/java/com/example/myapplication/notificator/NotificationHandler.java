package com.example.myapplication.notificator;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.ui.ScrollingActivity;

public class NotificationHandler {
    public final static long FIRST_TIME_DELAY = 10000;
    public final static long INTERVAL_DELAY = 30000;

    // Notification handler singleton
    private static NotificationHandler nHandler;
    private static NotificationManager mNotificationManager;
    private static AlarmManager alarmMgr;
    private static PendingIntent alarmIntent;
    private static Context context;

    private NotificationHandler() {
    }

    public static NotificationHandler getInstance(Context con) {
        context = con;
        if (nHandler == null) {
            nHandler = new NotificationHandler();
            mNotificationManager =
                    (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, BootReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + FIRST_TIME_DELAY, INTERVAL_DELAY, alarmIntent);
        }

        return nHandler;
    }

    public void createExpandableNotification(Context context, Tweet tweet) {
        Intent intent = new Intent(context, ScrollingActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        String title = "Новый твит от " + tweet.getUser().getScreenName();
        String text = Utils.removeLinks(tweet.getText());

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] content = text.split("\\.");

        inboxStyle.setBigContentTitle(title);
        for (String line : content) {
            inboxStyle.addLine(line);
        }

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, "TWEET_NOTIFY")
                .setSmallIcon(R.drawable.twitter)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentIntent(pIntent);

        mNotificationManager.notify(1, nBuilder.build());
    }
}
