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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchBarFragment extends Fragment {

    private TextInputEditText searchEditText;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> allEvents = new ArrayList<>();
    private List<Event> filteredList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_bar, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(filteredList, getContext(), false);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchEvents();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void fetchEvents() {
        firestore.collection("Events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            allEvents.clear();
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
            // Initially, you might want the list to be empty or show all. 
            // The requirement says "pop out when they are still typing", 
            // so we'll keep it empty until they start typing.
        }).addOnFailureListener(e -> {
            Log.e("SearchBarFragment", "Error fetching events", e);
        });
    }

    private void filterEvents(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            adapter.notifyDataSetChanged();
            return;
        }

        String lowerCaseQuery = query.toLowerCase().trim();

        for (Event event : allEvents) {
            boolean matchesTitle = event.getEvent_title() != null && event.getEvent_title().toLowerCase().contains(lowerCaseQuery);
            boolean matchesCategory = event.getCategory() != null && event.getCategory().toLowerCase().contains(lowerCaseQuery);
            boolean matchesLocation = event.getLocation() != null && event.getLocation().toLowerCase().contains(lowerCaseQuery);

            if (matchesTitle || matchesCategory || matchesLocation) {
                filteredList.add(event);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
