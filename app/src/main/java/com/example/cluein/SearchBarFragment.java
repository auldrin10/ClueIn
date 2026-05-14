package com.example.cluein;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

public class SearchBarFragment extends Fragment {

    private TextInputLayout searchLayout;
    private TextInputEditText searchEditText;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private SearchHistoryAdapter historyAdapter;
    private SearchHistoryManager historyManager;
    
    private List<Event> allEvents = new ArrayList<>();
    private List<Event> filteredList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private OkHttpClient client = new OkHttpClient();
    private String getEventsURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/getEvents.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_bar, container, false);

        searchLayout = view.findViewById(R.id.searchLayout);
        searchEditText = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults);

        historyManager = new SearchHistoryManager(requireContext());
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize Adapters
        eventAdapter = new EventAdapter(filteredList, getContext(), false);
        historyAdapter = new SearchHistoryAdapter(historyManager.getSearchHistory(), new SearchHistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onHistoryItemClick(String query) {
                searchEditText.setText(query);
                searchEditText.setSelection(query.length());
                performSearch(query);
            }

            @Override
            public void onDeleteHistoryClick(String query) {
                historyManager.deleteHistoryItem(query);
                historyAdapter.updateData(historyManager.getSearchHistory());
            }
        });

        // Show history by default if search is empty
        showHistoryIfEmpty("");

        firestore = FirebaseFirestore.getInstance();
        fetchEvents();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (query.isEmpty()) {
                    showHistoryIfEmpty(query);
                } else {
                    filterEvents(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Make magnifying glass clickable
        searchLayout.setEndIconOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            }
        });

        // Optional: Perform search when user presses Enter on keyboard
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
                return true;
            }
            return false;
        });

        return view;
    }

    private void performSearch(String query) {
        historyManager.saveSearchQuery(query);
        filterEvents(query);
        // Update history adapter in case it's shown later
        historyAdapter.updateData(historyManager.getSearchHistory());
    }

    private void showHistoryIfEmpty(String query) {
        if (query.isEmpty()) {
            List<String> history = historyManager.getSearchHistory();
            if (!history.isEmpty()) {
                recyclerView.setAdapter(historyAdapter);
                historyAdapter.updateData(history);
            } else {
                recyclerView.setAdapter(eventAdapter);
                filteredList.clear();
                eventAdapter.notifyDataSetChanged();
            }
        }
    }

    private void fetchEvents() {
        allEvents.clear();
        fetchFirestoreEvents();
        fetchPhpEvents();
    }

    private void fetchFirestoreEvents() {
        firestore.collection("Events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                String id = document.getId();
                String eventTitle = document.getString("Event_title");
                String location = document.getString("Location");
                String eventDate = document.getString("event_date");
                String description = document.getString("description");
                
                Double price = 0.0;
                Object priceObj = document.get("price");
                if (priceObj instanceof Number) {
                    price = ((Number) priceObj).doubleValue();
                } else if (priceObj instanceof String) {
                    try {
                        price = Double.parseDouble((String) priceObj);
                    } catch (NumberFormatException e) {
                        Log.e("SearchBarFragment", "Invalid price format: " + priceObj);
                    }
                }
                
                String category = document.getString("Event_category");
                String imageUrl = document.getString("Image_url");
                Boolean isWitsEvent = document.getBoolean("is_wits_event");

                allEvents.add(new Event(
                        eventTitle,
                        imageUrl,
                        location,
                        eventDate,
                        description,
                        price,
                        id,
                        isWitsEvent != null ? isWitsEvent : false, 
                        category != null ? category : ""
                ));
            }
            refreshFilterIfNotEmpty();
        }).addOnFailureListener(e -> {
            Log.e("SearchBarFragment", "Error fetching Firestore events", e);
        });
    }

    private void fetchPhpEvents() {
        Request request = new Request.Builder()
                .url(getEventsURL)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("SearchBarFragment", "Failed to fetch PHP events", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() == null) return;
                String json = response.body().string();
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONArray array = new JSONArray(json);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String event_id = obj.optString("event_id");
                                String event_name = obj.optString("event_name");
                                String location = obj.optString("location");
                                String date = obj.optString("date");
                                String priceStr = obj.optString("price");
                                String description = obj.optString("description");
                                String eventImage = obj.optString("event_image");

                                double price = 0.0;
                                try {
                                    price = Double.parseDouble(priceStr);
                                } catch (NumberFormatException e) {
                                    Log.e("SearchBarFragment", "Invalid price: " + priceStr);
                                }

                                allEvents.add(new Event(event_name, eventImage, location, date, description, price, event_id, true, "General"));
                            }
                            refreshFilterIfNotEmpty();
                        } catch (JSONException e) {
                            Log.e("SearchBarFragment", "JSON Error in PHP fetch", e);
                        }
                    });
                }
            }
        });
    }

    private void refreshFilterIfNotEmpty() {
        if (searchEditText != null) {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                filterEvents(query);
            }
        }
    }

    private void filterEvents(String query) {
        if (recyclerView.getAdapter() != eventAdapter) {
            recyclerView.setAdapter(eventAdapter);
        }
        
        filteredList.clear();
        String lowerCaseQuery = query.toLowerCase().trim();

        for (Event event : allEvents) {
            boolean matchesTitle = event.getEvent_title() != null && event.getEvent_title().toLowerCase().contains(lowerCaseQuery);
            boolean matchesCategory = event.getCategory() != null && event.getCategory().toLowerCase().contains(lowerCaseQuery);
            boolean matchesLocation = event.getLocation() != null && event.getLocation().toLowerCase().contains(lowerCaseQuery);

            if (matchesTitle || matchesCategory || matchesLocation) {
                filteredList.add(event);
            }
        }
        eventAdapter.notifyDataSetChanged();
    }
}
