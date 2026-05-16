package com.example.cluein;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FavoriteManager {

    private static FavoriteManager instance;

    private List<Event> favoriteEvents;

    OkHttpClient client = new OkHttpClient();

    String saveURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/savefavevent.php";

    String removeURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/removefavevent.php";

    private FavoriteManager() {
        favoriteEvents = new ArrayList<>();
    }

    public static synchronized FavoriteManager getInstance() {
        if (instance == null) {
            instance = new FavoriteManager();
        }
        return instance;
    }

    private String getUserId() {
        if (LoginActivity.user != null) {
            return LoginActivity.user.getUserID();
        }
        return "unknown";
    }

    private String getUserName() {
        if (LoginActivity.user != null) {
            return LoginActivity.user.getFirstName();
        }
        return "Guest";
    }

    // ======================================
    // ADD FAVORITE
    // ======================================

    public void addFavorite(Event event) {

        if (!isFavorite(event)) {

            favoriteEvents.add(event);

            saveFavoriteToDatabase(
                    getUserId(),
                    getUserName(),
                    event.getEvent_id()
            );
        }
    }

    // ======================================
    // REMOVE FAVORITE
    // ======================================

    public void removeFavorite(Event event) {

        favoriteEvents.removeIf(
                e -> e.getEvent_id()
                        .equals(event.getEvent_id())
        );

        removeFavoriteFromDatabase(
                getUserId(),
                event.getEvent_id()
        );
    }

    // ======================================
    // CHECK FAVORITE
    // ======================================

    public boolean isFavorite(Event event) {

        for (Event e : favoriteEvents) {

            if (
                    e.getEvent_id()
                            .equals(event.getEvent_id())
            ) {

                return true;
            }
        }

        return false;
    }

    // ======================================
    // GET FAVORITES
    // ======================================

    public List<Event> getFavoriteEvents() {

        return new ArrayList<>(favoriteEvents);
    }

    // ======================================
    // SAVE TO DATABASE
    // ======================================

    public void saveFavoriteToDatabase(
            String user_id,
            String user_name,
            String event_id
    ) {
        if (user_id.equals("unknown")) return;

        RequestBody body =
                new FormBody.Builder()

                        .add("user_id", user_id)

                        .add("user_name", user_name)

                        .add("event_id", event_id)

                        .build();

        Request request =
                new Request.Builder()

                        .url(saveURL)

                        .post(body)

                        .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(
                    @NonNull Call call,
                    @NonNull IOException e
            ) {

                Log.e(
                        "SAVE_EVENT_ERROR",
                        e.toString()
                );
            }

            @Override
            public void onResponse(
                    @NonNull Call call,
                    @NonNull Response response
            ) throws IOException {

                String res =
                        response.body().string();

                Log.d(
                        "SAVE_EVENT_RESPONSE",
                        res
                );
            }
        });
    }

    // ======================================
    // REMOVE FROM DATABASE
    // ======================================

    public void removeFavoriteFromDatabase(
            String user_id,
            String event_id
    ) {
        if (user_id.equals("unknown")) return;

        RequestBody body =
                new FormBody.Builder()

                        .add("user_id", user_id)

                        .add("event_id", event_id)

                        .build();

        Request request =
                new Request.Builder()

                        .url(removeURL)

                        .post(body)

                        .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(
                    @NonNull Call call,
                    @NonNull IOException e
            ) {

                Log.e(
                        "REMOVE_EVENT_ERROR",
                        e.toString()
                );
            }

            @Override
            public void onResponse(
                    @NonNull Call call,
                    @NonNull Response response
            ) throws IOException {

                String res =
                        response.body().string();

                Log.d(
                        "REMOVE_EVENT_RESPONSE",
                        res
                );
            }
        });
    }
}