package com.example.cluein;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.List;

public class EventReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "event_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventId = intent.getStringExtra("eventId");
        String eventTitle = intent.getStringExtra("eventTitle");
        String message = intent.getStringExtra("message");

        if (eventId == null) return;

        // Condition 1: Check if the event is in favorites
        boolean isFavorite = false;
        List<Event> favorites = FavoriteManager.getInstance().getFavoriteEvents();
        for (Event event : favorites) {
            if (event.getEvent_id() != null && event.getEvent_id().equals(eventId)) {
                isFavorite = true;
                break;
            }
        }

        // Condition 2: Only send if it's still a favorite
        if (!isFavorite) {
            return; 
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Event Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                notificationIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_event_24)
                .setContentTitle(eventTitle)
                .setContentText(message) // Line controlling the specific reminder text
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(eventId.hashCode() + (message != null ? message.hashCode() : 0), builder.build());
    }
}
