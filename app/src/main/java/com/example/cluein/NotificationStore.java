package com.example.cluein;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationStore {
    private static final String PREF_NAME = "notifications_pref";
    private static final String KEY_NOTIFICATIONS = "notifications_list";

    public static void saveNotification(Context context, NotificationModel notification) {
        List<NotificationModel> notifications = getNotifications(context);
        notifications.add(0, notification); // Add to top
        saveAllNotifications(context, notifications);
    }

    public static List<NotificationModel> getNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_NOTIFICATIONS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<NotificationModel>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void markAsRead(Context context, String notificationId) {
        List<NotificationModel> notifications = getNotifications(context);
        for (NotificationModel n : notifications) {
            if (n.getId().equals(notificationId)) {
                n.setRead(true);
                break;
            }
        }
        saveAllNotifications(context, notifications);
    }

    public static void markAllAsRead(Context context) {
        List<NotificationModel> notifications = getNotifications(context);
        for (NotificationModel n : notifications) {
            n.setRead(true);
        }
        saveAllNotifications(context, notifications);
    }

    public static boolean hasUnreadNotifications(Context context) {
        List<NotificationModel> notifications = getNotifications(context);
        for (NotificationModel n : notifications) {
            if (!n.isRead()) return true;
        }
        return false;
    }

    private static void saveAllNotifications(Context context, List<NotificationModel> notifications) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = new Gson().toJson(notifications);
        prefs.edit().putString(KEY_NOTIFICATIONS, json).apply();
    }

    public static void clearNotifications(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
