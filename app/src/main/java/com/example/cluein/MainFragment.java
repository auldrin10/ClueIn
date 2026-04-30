package com.example.cluein;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private ProgressBar progressBar;
    private List<Event> eventList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();

    FirebaseFirestore firestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Passing 'false' because this is the Main Feed, not Favorites
        adapter = new EventAdapter(eventList, getContext(), false);
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        fetchEvents();
        firestore = FirebaseFirestore.getInstance();
//        for(int i = 0; i < 5; i++){
//            Map<String, Object> event = new HashMap<>();
//            event.put("Event_title", "Wits Music Festival");
//            event.put("Image_url", "https://picsum.photos/id/1/600/400");
//            event.put("Location", "Noswall");
//            event.put("event_date", String.valueOf(i) + "- May 2026");
//            event.put("description", "TESTING");
//            event.put("price", i * 102.95);
//            event.put("is_wits_event", true);
//
//            firestore.collection("Events").add(event);
//
//        }

// Get all documents from "users" collection
        firestore.collection("Events").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String Event_Title = document.getString("Event_title");
                        String location = document.getString("Location");
                        String event_dateTime = document.getString("event_date");
                        String description = document.getString("description");
                        double price = document.getDouble("price");
                        String image_url = document.getString("Image_url");
                        boolean is_wits_event = document.getBoolean("is_wits_event");
                        eventList.add(new Event(Event_Title, image_url, location, event_dateTime, description, price, id, is_wits_event));
                    }
                });

        return view;
    }


    private void fetchEvents() {
        String url = "https://api.mockaroo.com/api/603386a0?count=50&key=3d8a6660";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                loadSampleData("Check your internet connection");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    List<Event> fetchedEvents = gson.fromJson(jsonResponse, new TypeToken<List<Event>>(){}.getType());

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            if (fetchedEvents != null && !fetchedEvents.isEmpty()) {
                                eventList.clear();
                                eventList.addAll(fetchedEvents);
                                adapter.notifyDataSetChanged();
                            } else {
                                loadSampleData("Server returned no events");
                            }
                        });
                    }
                } else {
                    loadSampleData("Server busy (Error: " + response.code() + ")");
                }
            }
        });
    }

    private void loadSampleData(String message) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            eventList.clear();

            eventList.add(new Event("Wits Music Festival", "https://picsum.photos/id/1/600/400", "Wits Great Hall", "Fri, 25 Oct", "The biggest party of the year!", 120.0, "101", true));
            eventList.add(new Event("Tech Hackathon 2024", "https://picsum.photos/id/2/600/400", "Matrix Building", "Sat, 26 Oct", "24 hours of pure coding and coffee", 0.0, "102", true));
            eventList.add(new Event("Varsity Rugby: Wits vs UJ", "https://picsum.photos/id/3/600/400", "Wits Stadium", "Mon, 28 Oct", "Come support your team!", 50.0, "103", true));
            eventList.add(new Event("Entrepreneurship Talk", "https://picsum.photos/id/4/600/400", "Science Stadium", "Wed, 30 Oct", "Learn from industry experts", 0.0, "104", false));
            firestore.collection("Events").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String id = document.getId();
                    String Event_Title = document.getString("Event_title");
                    String location = document.getString("Location");
                    String event_dateTime = document.getString("event_date");
                    String description = document.getString("description");
                    double price = document.getDouble("price");
                    String image_url = document.getString("Image_url");
                    boolean is_wits_event = document.getBoolean("is_wits_event");
                    eventList.add(new Event(Event_Title, image_url, location, event_dateTime, description, price, id, is_wits_event));
                }
            });
            adapter.notifyDataSetChanged();

        });
    }
}