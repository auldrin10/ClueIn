package com.example.cluein;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEvent extends Fragment {

//    Instances
    EditText eventName,eventLocation,eventDateTime, eventPrice, eventDescription;
    Button addEvent;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_event, container, false);


        eventName = view.findViewById(R.id.eventName);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventDateTime = view.findViewById(R.id.eventDateTime);
        eventPrice = view.findViewById(R.id.eventPrice);
        eventDescription = view.findViewById((R.id.eventDescription));

//        List of edittext instances
        EditText[] editTexts = {eventName,eventLocation, eventDateTime, eventPrice, eventDescription};

        addEvent = view.findViewById(R.id.addEvent);

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eventName.toString().trim().isBlank()){
                    eventName.setError("");
                }else if(eventLocation.toString().trim().isBlank()){
                    eventLocation.setError("");
                } else if(eventDateTime.toString().trim().isBlank()){
                    eventDateTime.setError("");
                }else if(eventPrice.toString().trim().isBlank()){
                    eventPrice.setError("");
                }else if(eventDescription.toString().trim().isBlank()){
                    eventDescription.setError("");
                }else{
                    Map<String, Object> event = new HashMap<>();
                    event.put("Event Name", eventName.toString());
                    event.put("Event DateTime",eventDateTime);
                    event.put("Event Location",eventLocation.toString());
                    event.put("Event Price",eventPrice);
                    event.put("Event Description",eventDescription.toString());

                    db.collection("Events")
                            .add(event);


                }
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int id = view.getId();

                    if(id == R.id.eventName){
                        eventName.setError(null);
                        eventName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.baseline_event_24,0);
                    }else if(id == R.id.eventLocation){
                        eventLocation.setError(null);
                        eventLocation.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.baseline_add_location_24,0);
                    } else if(id == R.id.eventDateTime){
                        eventDateTime.setError(null);
                        eventDateTime.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.outline_calendar_clock_24,0);
                    }else if(id == R.id.eventPrice){
                        eventPrice.setError(null);
                        eventPrice.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.outline_attach_money_24,0);
                    }else if(id == R.id.eventDescription){
                        eventDescription.setError(null);
                        eventDescription.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.baseline_description_24,0);
                    }
            }
        };

        for(EditText edit: editTexts){
            edit.addTextChangedListener(watcher);
        }

//        Extracting information from the form

        return view;
    }


}