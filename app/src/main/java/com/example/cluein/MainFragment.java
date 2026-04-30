package com.example.cluein;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(eventList, getContext(), false);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        
        progressBar.setVisibility(View.VISIBLE);
        
        // Start by fetching from Firestore, then try Mockaroo as additional data or fallback
        fetchFirestoreEvents();
        
        return view;
    }

    private void fetchFirestoreEvents() {
        firestore.collection("Events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            boolean addedAny = false;
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                String id = document.getId();
                String eventTitle = document.getString("Event_title");
                String location = document.getString("Location");
                String eventDate = document.getString("event_date");
                String description = document.getString("description");
                Double price = document.getDouble("price");
                String imageUrl = document.getString("Image_url");
                Boolean isWitsEvent = document.getBoolean("is_wits_event");

                eventList.add(new Event(
                        eventTitle,
                        imageUrl,
                        location,
                        eventDate,
                        description,
                        price != null ? price : 0.0,
                        id,
                        isWitsEvent != null ? isWitsEvent : false
                ));
                addedAny = true;
            }
            
            if (addedAny) {
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
            
            // Also try fetching from the API
            fetchApiEvents();
            
        }).addOnFailureListener(e -> {
            Log.e("MainFragment", "Firestore error", e);
            fetchApiEvents();
        });
    }

    private void fetchApiEvents() {
        String url = "https://api.mockaroo.com/api/603386a0?count=50&key=3d8a6660";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (eventList.isEmpty()) {
                            loadSampleData("Check your internet connection");
                        }
                    });
                }
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
                                eventList.addAll(fetchedEvents);
                                adapter.notifyDataSetChanged();
                            } else if (eventList.isEmpty()) {
                                loadSampleData("No events found");
                            }
                        });
                    }
                } else if (eventList.isEmpty()) {
                    loadSampleData("Server busy");
                }
            }
        });
    }

    private void loadSampleData(String message) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            
            // Only add hardcoded samples if we have absolutely no data
            if (eventList.isEmpty()) {
                eventList.add(new Event("Wits Music Festival", "https://picsum.photos/id/1/600/400", "Wits Great Hall", "25 - October 2024", "The biggest party of the year!", 120.0, "101", true));
                eventList.add(new Event("Tech Hackathon 2024", "https://picsum.photos/id/2/600/400", "Matrix Building", "26 - October 2024", "24 hours of pure coding", 0.0, "102", true));
                adapter.notifyDataSetChanged();
            }
            
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
