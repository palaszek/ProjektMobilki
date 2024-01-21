package com.example.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationChannelBuilder {

    public static final String CHANNEL_ID = "channel1";
    public static final int CHANNEL_ID_INT = 0;
    public static final String CHANNEL_NAME = "MUSIC_NOTIFICATION_CHANNEL";


    public void showNotification(Context context, AudioModel song, int res) {

        Intent actionIntent = new Intent(context, MusicNotification.class);
        actionIntent.setAction("ACTION_PREVIOUS");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 0, actionIntent , PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent actionIntent2 = new Intent(context, MusicNotification.class);
        actionIntent2.setAction("ACTION_PLAY/PAUSE");
        PendingIntent play_pausePendingIntent = PendingIntent.getBroadcast(context, 0, actionIntent2 , PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent actionIntent3 = new Intent(context, MusicNotification.class);
        actionIntent3.setAction("ACTION_NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, actionIntent3 , PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.music_icon)
            .setAutoCancel(true)
            .addAction(R.drawable.baseline_skip_previous_24, "Previous", previousPendingIntent)
            .addAction(res, "Play/Pause", play_pausePendingIntent)
            .addAction(R.drawable.baseline_skip_next_24, "Next", nextPendingIntent)
            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
            .setContentTitle(song.getTitle())
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions.
            return;
        }
        notificationManager.notify(CHANNEL_ID_INT, builder.build());

    }
}
