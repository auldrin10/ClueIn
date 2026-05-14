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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private ProgressBar progressBar;
    public List<Event> eventList = new ArrayList<>();
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
//        fetchEvents();
        
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
                
                // Safely get price as Double, handling cases where it might be a String in Firestore
                Double price = 0.0;
                Object priceObj = document.get("price");
                if (priceObj instanceof Number) {
                    price = ((Number) priceObj).doubleValue();
                } else if (priceObj instanceof String) {
                    try {
                        price = Double.parseDouble((String) priceObj);
                    } catch (NumberFormatException e) {
                        Log.e("MainFragment", "Invalid price format: " + priceObj);
                    }
                }
                
                String category = document.getString("Event_category");
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
                        isWitsEvent != null ? isWitsEvent : false, 
                        category != null ? category : "General"
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
                eventList.add(new Event("Wits Music Festival", "https://picsum.photos/id/1/600/400", "Wits Great Hall", "25 October 2026", "The biggest party of the year!", 70.0, "101", true , "Graduations"));
                eventList.add(new Event("Tech Hackathon 2026", "https://picsum.photos/id/2/600/400", "Matrix Building", "26 October 2026", "24 hours of pure coding", 300.0, "102", true, "Hackathon"));
                adapter.notifyDataSetChanged();
            }
            
            if (message != null && !message.isEmpty() && getContext() != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    String getEventsURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/getEvents.php";
    OkHttpClient client3 = new OkHttpClient();

    public void fetchEvents() {
        Request request = new Request.Builder()
                .url(getEventsURL)
                .get()
                .build();

        client3.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (getContext() != null) {
                            Toast.makeText(
                                    getContext(),
                                    "Failed to fetch events",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                }
                Log.e("EVENT_ERROR", e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Log.d("EVENT_RESPONSE", json);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONArray array = new JSONArray(json);
                            eventList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String event_id = obj.optString("event_id");
                                String event_name = obj.optString("event_name");
                                String location = obj.optString("location");
                                String time = obj.optString("time");
                                String date = obj.optString("date");
                                String priceStr = obj.optString("price");
                                String description = obj.optString("description");
                                String event_image = obj.optString("event_image");

                                double price = 0.0;
                                try {
                                    price = Double.parseDouble(priceStr);
                                } catch (NumberFormatException e) {
                                    Log.e("MainFragment", "Invalid price: " + priceStr);
                                }

//                                Event event = new Event(event_name, event_image, location, date, description, price, event_id, true, "General");

                                eventList.add(new Event(event_name, event_image, location, date, description, price, event_id, true, "General"));
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("TOTAL_EVENTS", String.valueOf(eventList.size()));
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (getContext() != null) {
                                Toast.makeText(
                                        getContext(),
                                        "JSON Error",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
