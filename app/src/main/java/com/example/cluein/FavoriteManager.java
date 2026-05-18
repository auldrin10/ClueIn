package com.example.cluein;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

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
    String loadURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/users/usersfavourates.php";

    public void loadFavoritesFromDatabase(Runnable onComplete) {

        String user_id = getUserId();

        if(user_id.equals("unknown")) return;

        RequestBody body =
                new FormBody.Builder()
                        .add("user_id", user_id)
                        .build();

        Request request =
                new Request.Builder()
                        .url(loadURL)
                        .post(body)
                        .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(
                    @NonNull Call call,
                    @NonNull IOException e
            ) {

                Log.e(
                        "LOAD_FAV_ERROR",
                        e.toString()
                );
            }

            @Override
            public void onResponse(
                    @NonNull Call call,
                    @NonNull Response response
            ) throws IOException {

                try{

                    String res =
                            response.body().string();

                    Log.d(
                            "FAVORITE_RESPONSE",
                            res
                    );

                    JSONObject obj =
                            new JSONObject(res);

                    JSONArray arr =
                            obj.getJSONArray("events");

                    favoriteEvents.clear();

                    for(int i=0;i<arr.length();i++){

                        JSONObject item =
                                arr.getJSONObject(i);

                        // Extract values safely and ensure types match the Event constructor
                        String eventName = item.optString("event_name", "");
                        String imageUrl = item.optString("event_image", ""); // Assuming correct key
                        String location = item.optString("location", "");
                        String date = item.optString("date", "");
                        String description = item.optString("description", "");
                        double price = item.optDouble("price", 0.0);
                        String eventId = item.optString("event_id", "");
                        String categoryId = item.optString("category_id", "");

                        Event event =
                                new Event(
                                        eventName,
                                        imageUrl,
                                        location,
                                        date,
                                        description,
                                        price,
                                        eventId,
                                        true,
                                        categoryId
                                );

                        favoriteEvents.add(event);
                    }

                    if(onComplete!=null){

                        onComplete.run();

                    }

                }
                catch(Exception e){

                    Log.e(
                            "LOAD_PARSE_ERROR",
                            e.toString()
                    );
                }
            }
        });
    }
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


    public List<Event> getFavoriteEvents() {

        return new ArrayList<>(favoriteEvents);
    }


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
