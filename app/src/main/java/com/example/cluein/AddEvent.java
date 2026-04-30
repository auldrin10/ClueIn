package com.example.cluein;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEvent extends Fragment {

//    Instances
    private EditText eventName, eventLocation, eventDate, eventTime, eventPrice, eventDescription;
    private Button addEvent,pickDate,pickTime;
    private Uri imageUrl;
    private MaterialButton selectImage;
    private ImageView imageView;
    private static final String TAG = "AddEventFragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Calendar selectedEventDate = Calendar.getInstance();

    private final ActivityResultLauncher<String> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUrl = uri;
                    imageView.setImageURI(uri);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Notifications permission denied", Toast.LENGTH_SHORT).show();
                }
            });


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
        EditText[] editTexts = {eventName, eventLocation, eventDate, eventTime, eventPrice, eventDescription};

        checkNotificationPermission();

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

              boolean isValid = true;

                if(strEventName.isEmpty()){
                    eventName.setError("Event name missing.");
                    isValid = false;
                }if(strEventLocation.isEmpty()){
                    eventLocation.setError("Event location missing.");
                    isValid = false;
                }if(strEventDate.isEmpty()) {
                    eventDate.setError("Event date missing.");
                    isValid = false;
                }if(strEventTime.isEmpty()){
                    eventTime.setError("Event time missing.");
                    isValid = false;
                }if(strEventPrice.isEmpty()){
                    eventPrice.setError("Event price missing.");
                        isValid = false;
                }if(strEventDescription.isEmpty()){
                    eventDescription.setError("Event description missing.");
                        isValid = false;
                }if(isValid) {
                        double dblprice = 0.0;
                        try {
                            dblprice = Double.parseDouble(strEventPrice);
                        } catch (Exception e) {
                            Log.e("Error", "Failed parsing: " + strEventPrice);
                            Toast.makeText(requireContext(), "Wrong input!", Toast.LENGTH_LONG).show();
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
                        eventMap.put("Image_url", imageUrl != null ? imageUrl.toString() : "");


                        db.collection("Events")
                                .add(eventMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "Event added with ID: " + documentReference.getId());
                                        scheduleNotification(documentReference.getId(), strEventName);
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

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageLauncher.launch("image/*");
            }
        });


//        on text changed listner
        for(EditText edit: editTexts) {
            edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {

                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (edit == eventName) {
                        eventName.setError(null);
                        eventName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_event_24, 0);
                    } else if (edit == eventLocation) {
                        eventLocation.setError(null);
                        eventLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_add_location_24, 0);
                    } else if (edit == eventDate) {
                        eventDate.setError(null);
                        eventDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_calendar_clock_24, 0);
                    } else if (edit == eventTime) {
                        eventTime.setError(null);
                        eventTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_calendar_clock_24, 0);
                    } else if (edit == eventPrice) {
                        eventPrice.setError(null);
                        eventPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_attach_money_24, 0);
                    } else if (edit == eventDescription) {
                        eventDescription.setError(null);
                        eventDescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_description_24, 0);
                    }

                }

            });
        }

        return view;
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void scheduleNotification(String eventId, String eventTitle) {
        Calendar reminderCalendar = (Calendar) selectedEventDate.clone();
        // Set reminder for 20 minutes before
        reminderCalendar.add(Calendar.MINUTE, -20);

        if (reminderCalendar.getTimeInMillis() < System.currentTimeMillis()) {
            Log.d(TAG, "Reminder time is in the past, skipping scheduling.");
            return;
        }

        Intent intent = new Intent(requireContext(), EventReminderReceiver.class);
        intent.putExtra("eventId", eventId);
        intent.putExtra("eventTitle", eventTitle);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(), 
                eventId.hashCode(),
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), pendingIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

//    Time and date dialogs methods
    private void dateDialog(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                selectedEventDate.set(Calendar.YEAR, year);
                selectedEventDate.set(Calendar.MONTH, month);
                selectedEventDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                eventDate.setText(sdf.format(selectedEventDate.getTime()));
            }
        }, year, month, day);

        // Restrict picking dates in the past
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void timeDialog(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                selectedEventDate.set(Calendar.HOUR_OF_DAY, hours);
                selectedEventDate.set(Calendar.MINUTE, minutes);
                eventTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hours, minutes));
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }
}
