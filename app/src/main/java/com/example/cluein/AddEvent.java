package com.example.cluein;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddEvent extends Fragment {
    OkHttpClient Eventclient = new OkHttpClient();
    private EditText eventName, eventDate, eventTime, eventPrice, eventDescription;
    private AutoCompleteTextView eventLocation, eventCategory;
    private Button addEvent, clearForm, pickDate, pickTime;
    private ScrollView mainFormContainer;

    private static final String TAG = "AddEventFragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Calendar selectedEventDate = Calendar.getInstance();

    private final List<String> authorizedEmails = Arrays.asList("3030015@students.wits.ac.za", "3002003@students.wits.ac.za", "3032986@students.wits.ac.za");

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

        mainFormContainer = view.findViewById(R.id.mainFormContainer);
        eventName = view.findViewById(R.id.eventName);
        eventLocation = view.findViewById(R.id.eventLocation);
        setupLocationDropdown();
        eventDate = view.findViewById(R.id.eventDate);
        eventTime = view.findViewById(R.id.eventTime);
        eventCategory = view.findViewById(R.id.eventCategory);
        setupCategoryDropdown();
        eventPrice = view.findViewById(R.id.eventPrice);
        eventDescription = view.findViewById(R.id.eventDescription);
        addEvent = view.findViewById(R.id.addEvent);
        clearForm = view.findViewById(R.id.clearForm);
        pickDate = view.findViewById(R.id.btnPickDate);
        pickTime = view.findViewById(R.id.btnPickTime);

        EditText[] editTexts = {eventName, eventLocation, eventCategory, eventDate, eventTime, eventPrice, eventDescription};

        checkUserAuthorization();
        checkNotificationPermission();

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    // Images are now determined by category automatically in the Adapter
                    post("category_default");
                }
            }
        });

        pickDate.setOnClickListener(v -> dateDialog());
        pickTime.setOnClickListener(v -> timeDialog());
        clearForm.setOnClickListener(v -> clearFormFields());

        for (EditText edit : editTexts) {
            edit.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    edit.setError(null);
                    addEvent.setEnabled(true);
                    addEvent.setBackgroundResource(R.drawable.sign_up_button);
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        return view;
    }

    private boolean validateFields() {
        String strEventName = eventName.getText().toString().trim();
        String strEventLocation = eventLocation.getText().toString().trim();
        String strEventCategory = eventCategory.getText().toString().trim();
        String strEventDate = eventDate.getText().toString().trim();
        String strEventTime = eventTime.getText().toString().trim();
        String strEventPrice = eventPrice.getText().toString().trim();
        String strEventDescription = eventDescription.getText().toString().trim();

        if (strEventName.isEmpty()) { eventName.setError("Event name missing."); return false; }
        if (strEventLocation.isEmpty()) { eventLocation.setError("Event location missing."); return false; }
        if (strEventCategory.isEmpty()) { eventCategory.setError("Event category missing."); return false; }
        if (strEventDate.isEmpty()) { eventDate.setError("Event date missing."); return false; }
        if (strEventTime.isEmpty()) { eventTime.setError("Event time missing."); return false; }
        if (strEventPrice.isEmpty()) { eventPrice.setError("Event price missing."); return false; }
        if (strEventDescription.isEmpty()) { eventDescription.setError("Event description missing."); return false; }
        
        int wordCount = strEventDescription.trim().split("\\s+").length;
        if (wordCount > 100) {
            eventDescription.setError("Description cannot exceed 100 words (Current: " + wordCount + ")");
            return false;
        }
        
        return true;
    }

    String postEventURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/eventpost.php";

    public void post(String imageUrlString) {

        String eName = eventName.getText().toString().trim();
        String eLoc = eventLocation.getText().toString().trim();
        String rawDate = eventDate.getText().toString().trim();
        String eTime = eventTime.getText().toString().trim();
        String ePrice = eventPrice.getText().toString().trim();
        String eDesc = eventDescription.getText().toString().trim();

        String formattedDate = rawDate;
        try{
            SimpleDateFormat input = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date d = input.parse(rawDate);
            if(d != null){
                formattedDate = output.format(d);
            }
        }catch(Exception e){
            Log.e("DATE_ERROR",e.getMessage());
        }

        String eCat = eventCategory.getText().toString().trim();
        switch(eCat){
            case "Society": eCat="1"; break;
            case "Sports": eCat="2"; break;
            case "Music Concerts": eCat="3"; break;
            case "Financial literacy": eCat="4"; break;
            case "Career Expo": eCat="5"; break;
            case "Academics": eCat="6"; break;
        }

        RequestBody body = new FormBody.Builder()
                .add("event_name",eName)
                .add("event_location",eLoc)
                .add("category_id",eCat)
                .add("event_date",formattedDate)
                .add("event_time",eTime)
                .add("event_price",ePrice)
                .add("event_description",eDesc)
                .add("event_image",imageUrlString)
                .build();

        Request request = new Request.Builder()
                .url(postEventURL)
                .post(body)
                .build();

        Eventclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e){
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "NETWORK ERROR:\n"+e.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String serverResponse = response.body()!=null ? response.body().string() : "";
                Log.d("SERVER_RESPONSE",serverResponse);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try{
                            JSONObject obj = new JSONObject(serverResponse);
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            Toast.makeText(requireContext(), status+" : "+message, Toast.LENGTH_LONG).show();
                            if(status.equals("success")){
                                clearFormFields();
                            }
                        }catch(Exception e){
                            Toast.makeText(requireContext(), serverResponse, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void checkUserAuthorization() {
        String userEmail = "";
        if (getActivity() != null && getActivity().getIntent() != null) {
            userEmail = getActivity().getIntent().getStringExtra("USER_EMAIL");
        }
        if (userEmail == null || !authorizedEmails.contains(userEmail.toLowerCase())) {
            if (mainFormContainer != null) mainFormContainer.setVisibility(View.GONE);
            showUnauthorizedDialog();
        } else {
            if (mainFormContainer != null) mainFormContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showUnauthorizedDialog() {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_unauthorized, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                .setView(dialogView).setCancelable(false).create();
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).findViewById(R.id.nav_feed).performClick();
            }
        });
        dialog.show();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void dateDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.DialogTheme, (view, year, month, dayOfMonth) -> {
            selectedEventDate.set(Calendar.YEAR, year);
            selectedEventDate.set(Calendar.MONTH, month);
            selectedEventDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            eventDate.setText(sdf.format(selectedEventDate.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void timeDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.DialogTheme, (view, hourOfDay, minute) -> {
            selectedEventDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedEventDate.set(Calendar.MINUTE, minute);
            eventTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void clearFormFields() {
        eventName.setText("");
        eventLocation.setText("");
        eventCategory.setText("");
        eventDate.setText("");
        eventTime.setText("");
        eventPrice.setText("");
        eventDescription.setText("");
        if (addEvent != null) {
            addEvent.setEnabled(true);
            addEvent.setBackgroundResource(R.drawable.sign_up_button);
        }
    }

    private void setupLocationDropdown() {
        String[] locations = {"Great Hall", "Matrix", "Rugby Stadium", "Football Stadium", "Campus(Library lawns)", "Mathematical Sciences Building", "Science Stadium", "Oppenheimer Life Sciences", "FNB Building", "Flower Hall", "Theatre Complex", "Club & Conference", "Origin Centre", "Solomon Mahlangu House"};
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, locations);
            eventLocation.setAdapter(adapter);
        }
    }

    private void setupCategoryDropdown() {
        String[] categories = {"Music Concerts", "Sports", "Society", "Academics", "Financial literacy", "Career Expo"};
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
            eventCategory.setAdapter(adapter);
        }
    }
}