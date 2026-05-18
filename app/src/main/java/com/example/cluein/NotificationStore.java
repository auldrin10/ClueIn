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
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = new Gson().toJson(notifications);
        prefs.edit().putString(KEY_NOTIFICATIONS, json).apply();
    }

    public static List<NotificationModel> getNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_NOTIFICATIONS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<NotificationModel>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void clearNotifications(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
