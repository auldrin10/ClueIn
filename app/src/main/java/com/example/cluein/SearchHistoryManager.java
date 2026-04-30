package com.example.cluein;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryManager {
    private static final String PREF_NAME = "search_history_prefs";
    private static final String KEY_HISTORY = "search_history";
    private static final int MAX_HISTORY_SIZE = 10;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public SearchHistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) return;

        List<String> history = getSearchHistory();
        
        // Remove if already exists to move it to the top
        history.remove(query);
        
        // Add to the beginning
        history.add(0, query);

        // Limit size
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }

        String json = gson.toJson(history);
        sharedPreferences.edit().putString(KEY_HISTORY, json).apply();
    }

    public List<String> getSearchHistory() {
        String json = sharedPreferences.getString(KEY_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void deleteHistoryItem(String query) {
        List<String> history = getSearchHistory();
        history.remove(query);
        String json = gson.toJson(history);
        sharedPreferences.edit().putString(KEY_HISTORY, json).apply();
    }

    public void clearHistory() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply();
    }
}
