package com.example.cluein;

import static android.app.Activity.RESULT_OK;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEvent extends Fragment {

    private EditText eventName, eventLocation, eventDate, eventTime, eventPrice, eventDescription;
    private Button addEvent,pickDate,pickTime;
    private Uri imageUrl;
    private MaterialButton selectImage;
    private ImageView imageView;
    private static final String TAG = "AddEventFragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        // Initialize Views
        eventName = view.findViewById(R.id.eventName);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventDate = view.findViewById(R.id.eventDate);
        eventTime = view.findViewById(R.id.eventTime);
        eventPrice = view.findViewById(R.id.eventPrice);
        eventDescription = view.findViewById(R.id.eventDescription);
        addEvent = view.findViewById(R.id.addEvent);
        pickDate = view.findViewById(R.id.btnPickDate);
        pickTime = view.findViewById(R.id.btnPickTime);
        selectImage = view.findViewById(R.id.selectImage);
        imageView = view.findViewById(R.id.eventImage);

//        Validation
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEventName = eventName.getText().toString().trim();
                String strEventLocation = eventLocation.getText().toString().trim();
                String strEventDate = eventDate.getText().toString().trim();
                String strEventTime = eventTime.getText().toString().trim();
                String strEventPrice = eventPrice.getText().toString().trim();
                String strEventDescription = eventDescription.getText().toString().trim();

                if(strEventName.isEmpty()){
                    eventName.setError("Event name missing.");
                }else if(strEventLocation.isEmpty()){
                    eventLocation.setError("Event location missing.");
                }else if(strEventDate.isEmpty()){
                    eventDate.setError("Event date missing.");
                }else if(strEventTime.isEmpty()){
                    eventTime.setError("Event time missing.");
                }else if(strEventPrice.isEmpty()){
                    eventPrice.setError("Event price missing.");
                }else if(strEventDescription.isEmpty()){
                    eventDescription.setError("Event description missing.");
                }else{
                    double dblprice = 0.0;
                    try{
                         dblprice = Double.parseDouble(strEventPrice);
                    }catch (Exception e){
                        Log.e("Error", "Failed parsing: "+ strEventPrice);
                        Toast.makeText(requireContext(),"Wrong input!",Toast.LENGTH_LONG).show();
                        return;
                    }

                    Map<String, Object> eventMap = new HashMap<>();
                    eventMap.put("Event_title", strEventName);
                    eventMap.put("Location", strEventLocation);
                    eventMap.put("event_date", strEventDate);
                    eventMap.put("Event_time", strEventTime);
                    eventMap.put("price", dblprice);
                    eventMap.put("description", strEventDescription);
                    eventMap.put("is_wits_event", false);
                    eventMap.put("Image_url", "https://picsum.photos/id/4/600/400");


                    db.collection("Events")
                            .add(eventMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "Event added with ID: "+ documentReference.getId());
                                    Toast.makeText(requireContext(), "Event Added!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding event", e);
                                    Toast.makeText(requireContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

//        Time and date pickers
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog();
            }
        });

        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeDialog();
            }
        });
        return view;
    }

//    Time and date dialogs methods
    private void dateDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                eventDate.setText(String.valueOf(year)+"/"+String.valueOf(month + 1)+"/"+String.valueOf(day));
            }
        }, 2026, 0, 25);
        datePickerDialog.show();
    }

    private void timeDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                eventTime.setText(String.valueOf(hours)+":"+String.valueOf(minutes));
            }
        }, 12, 50, true);
        timePickerDialog.show();
    }

}
