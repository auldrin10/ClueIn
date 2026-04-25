package com.example.cluein;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEvent extends Fragment {

    private EditText eventName, eventLocation, eventDateTime, eventPrice, eventDescription;
    private Button addEvent;
    private Uri imageUrl;
    private MaterialButton selectImage;
    private ImageView imageView;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUrl = result.getData().getData();
                    Glide.with(requireContext()).load(imageUrl).into(imageView);
                } else {
                    Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        // Initialize Views
        eventName = view.findViewById(R.id.eventName);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventDateTime = view.findViewById(R.id.eventDateTime);
        eventPrice = view.findViewById(R.id.eventPrice);
        eventDescription = view.findViewById(R.id.eventDescription);
        addEvent = view.findViewById(R.id.addEvent);
        selectImage = view.findViewById(R.id.selectImage);
        imageView = view.findViewById(R.id.eventImage);

        selectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        addEvent.setOnClickListener(v -> {
            String name = eventName.getText().toString().trim();
            String location = eventLocation.getText().toString().trim();
            String dateTime = eventDateTime.getText().toString().trim();
            String price = eventPrice.getText().toString().trim();
            String description = eventDescription.getText().toString().trim();

            if (name.isEmpty()) {
                eventName.setError("Event name is required");
            } else if (location.isEmpty()) {
                eventLocation.setError("Location is required");
            } else if (dateTime.isEmpty()) {
                eventDateTime.setError("Date/Time is required");
            } else if (price.isEmpty()) {
                eventPrice.setError("Price is required");
            } else if (description.isEmpty()) {
                eventDescription.setError("Description is required");
            } else if (imageUrl == null) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            } else {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("Event Name", name);
                eventMap.put("Event DateTime", dateTime);
                eventMap.put("Event Location", location);
                eventMap.put("Event Price", price);
                eventMap.put("Event Description", description);
                eventMap.put("Event Image", imageUrl.toString());

                db.collection("Events")
                        .add(eventMap)
                        .addOnSuccessListener(documentReference -> 
                            Toast.makeText(requireContext(), "Event Added!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> 
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        // Add TextWatchers to clear errors
        setupErrorClearing(eventName, R.drawable.baseline_event_24);
        setupErrorClearing(eventLocation, R.drawable.baseline_add_location_24);
        setupErrorClearing(eventDateTime, R.drawable.outline_calendar_clock_24);
        setupErrorClearing(eventPrice, R.drawable.outline_attach_money_24);
        setupErrorClearing(eventDescription, R.drawable.baseline_description_24);

        return view;
    }

    private void setupErrorClearing(EditText editText, int iconRes) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setError(null);
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconRes, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
