package com.example.cluein;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private Uri imageUrl;
    private MaterialButton selectImage;
    private ImageView imageView;
    private ScrollView mainFormContainer;

    private static final String TAG = "AddEventFragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private Calendar selectedEventDate = Calendar.getInstance();

    // List of authorized emails (A, B, C)
    private final List<String> authorizedEmails = Arrays.asList("3030015@students.wits.ac.za", "3002003@students.wits.ac.za", "3032986@students.wits.ac.za");
    private final ActivityResultLauncher<String> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUrl = uri;
                    imageView.setImageURI(uri);
                    imageView.setVisibility(View.VISIBLE);
                    selectImage.setText("Change Image");
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
        selectImage = view.findViewById(R.id.selectImage);
        imageView = view.findViewById(R.id.eventImage);
        EditText[] editTexts = {eventName, eventLocation, eventCategory, eventDate, eventTime, eventPrice, eventDescription};

        // CHECK AUTHORIZATION ON LOAD
        checkUserAuthorization();

        checkNotificationPermission();

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEventName = eventName.getText().toString().trim();
                String strEventLocation = eventLocation.getText().toString().trim();
                String strEventCategory = eventCategory.getText().toString().trim();
                String strEventDate = eventDate.getText().toString().trim();
                String strEventTime = eventTime.getText().toString().trim();
                String strEventPrice = eventPrice.getText().toString().trim();
                String strEventDescription = eventDescription.getText().toString().trim();

                boolean isValid = true;

                if (strEventName.isEmpty()) {
                    eventName.setError("Event name missing.");
                    isValid = false;
                }
                if (strEventLocation.isEmpty()) {
                    eventLocation.setError("Event location missing.");
                    isValid = false;
                }
                if (strEventCategory.isEmpty()) {
                    eventCategory.setError("Event category missing.");
                    isValid = false;
                }
                if (strEventDate.isEmpty()) {
                    eventDate.setError("Event date missing.");
                    isValid = false;
                }
                if (strEventTime.isEmpty()) {
                    eventTime.setError("Event time missing.");
                    isValid = false;
                }
                if (strEventPrice.isEmpty()) {
                    eventPrice.setError("Event price missing.");
                    isValid = false;
                }
                if (strEventDescription.isEmpty()) {
                    eventDescription.setError("Event description missing.");
                    isValid = false;
                } else {
                    int wordCount = strEventDescription.trim().split("\\s+").length;
                    if (wordCount > 100) {
                        eventDescription.setError("Description cannot exceed 100 words (Current: " + wordCount + ")");
                        isValid = false;
                    }
                }
                if (imageUrl == null) {
                    Toast.makeText(requireContext(), "Please select an image.", Toast.LENGTH_LONG).show();
                    isValid = false;
                }

                if (isValid) {
                    uploadImageAndPost();
                }
            }
        });

        pickDate.setOnClickListener(v -> dateDialog());
        pickTime.setOnClickListener(v -> timeDialog());
        selectImage.setOnClickListener(v -> selectImageLauncher.launch("image/*"));
        clearForm.setOnClickListener(v -> clearFormFields());

        for (EditText edit : editTexts) {
            edit.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (edit == eventName) {
                        eventName.setError(null);
                        eventName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_event_24, 0);
                    } else if (edit == eventLocation) {
                        eventLocation.setError(null);
                        eventLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_add_location_24, 0);
                    } else if (edit == eventCategory) {
                        eventCategory.setError(null);
                        eventCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_ad_group_24, 0);
                    } else if (edit == eventDate) {
                        eventDate.setError(null);
                        eventDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_calendar_clock_24, 0);
                    } else if (edit == eventTime) {
                        eventTime.setError(null);
                        eventTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_alarm_24, 0);
                    } else if (edit == eventPrice) {
                        eventPrice.setError(null);
                        eventPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rand, 0);
                    } else if (edit == eventDescription) {
                        eventDescription.setError(null);
                        eventDescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_description_24, 0);
                    }

                    if (edit != eventDescription) {
                        addEvent.setEnabled(true);
                        addEvent.setBackgroundResource(R.drawable.sign_up_button);
                    }
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        return view;
    }

    private void checkUserAuthorization() {
        String userEmail = "";
        if (getActivity() != null && getActivity().getIntent() != null) {
            userEmail = getActivity().getIntent().getStringExtra("USER_EMAIL");
        }

        if (userEmail == null || !authorizedEmails.contains(userEmail.toLowerCase())) {
            // NOT AUTHORIZED
            if (mainFormContainer != null) {
                mainFormContainer.setVisibility(View.GONE);
            }
            showUnauthorizedDialog();
        } else {
            // AUTHORIZED
            if (mainFormContainer != null) {
                mainFormContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showUnauthorizedDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_unauthorized, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            // Redirect back to Main Feed
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).findViewById(R.id.nav_feed).performClick();
            }
        });

        dialog.show();
    }

    String postEventURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/eventpost.php";

    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) return "";

            // Resize image to keep Base64 string at a reasonable length
            int maxWidth = 600;
            int maxHeight = 600;
            if (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight) {
                float aspectRatio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
                int newWidth, newHeight;
                if (aspectRatio > 1) {
                    newWidth = maxWidth;
                    newHeight = (int) (maxWidth / aspectRatio);
                } else {
                    newHeight = maxHeight;
                    newWidth = (int) (maxHeight * aspectRatio);
                }
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // Use JPEG compression to further reduce size
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.NO_WRAP);
        } catch (IOException e) {
            Log.e(TAG, "Error converting URI to Base64", e);
            return "";
        }
    }

    private void uploadImageAndPost() {
        if (imageUrl == null) return;

        // Show a progress dialog or some loading indicator
        Toast.makeText(requireContext(), "Uploading event...", Toast.LENGTH_SHORT).show();

        String fileName = "events/" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = storage.getReference().child(fileName);

        storageRef.putFile(imageUrl)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    post(downloadUrl);
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed", e);
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    public void post(String imageUrlString) {
        String eName = eventName.getText().toString().trim();
        String eLoc = eventLocation.getText().toString().trim();
        String eDate = eventDate.getText().toString().trim();
        String eTime = eventTime.getText().toString().trim();
        String eCat = eventCategory.getText().toString().trim();
        String ePrice = eventPrice.getText().toString().trim();
        String eDesc = eventDescription.getText().toString().trim();

        RequestBody body = new FormBody.Builder()
                .add("event_name", eName)
                .add("event_location", eLoc)
                .add("event_date", eDate)
                .add("event_time", eTime)
                .add("event_category", eCat)
                .add("event_price", ePrice)
                .add("event_description", eDesc)
                .add("event_image", imageUrlString)
                .build();

        Request request = new Request.Builder().url(postEventURL).post(body).build();

        Eventclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseData = response.body().string();
                if (response.isSuccessful() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String message = jsonObject.optString("message", "Success");
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            if (message.toLowerCase().contains("successfully")) {
                                clearFormFields();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parse error: " + responseData);
                        }
                    });
                }
            }
        });
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(String eventId, String eventTitle) {
        Calendar reminderCalendar = (Calendar) selectedEventDate.clone();
        reminderCalendar.add(Calendar.MINUTE, -20);

        if (reminderCalendar.getTimeInMillis() < System.currentTimeMillis()) return;

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
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), pendingIntent);
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

    private void showEventError() {
        new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                .setTitle("Error")
                .setMessage("This event exists!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void clearFormFields() {
        eventName.setText("");
        eventLocation.setText("");
        eventCategory.setText("");
        eventDate.setText("");
        eventTime.setText("");
        eventPrice.setText("");
        eventDescription.setText("");
        imageUrl = null;
        if (imageView != null) {
            imageView.setVisibility(View.GONE);
        }
        if (selectImage != null) {
            selectImage.setText("Select Image");
        }
        if (addEvent != null) {
            addEvent.setEnabled(true);
            addEvent.setBackgroundResource(R.drawable.sign_up_button);
        }
    }

    private void setupLocationDropdown() {
        String[] locations = {
                "Great Hall",
                "Matrix",
                "Rugby Stadium",
                "Soccer Stadium",
                "Netball",
                "Mathematical Sciences Building",
                "Science Stadium",
                "Oppenheimer Life Sciences",
                "FNB Building",
                "Flower Hall",
                "Theatre",
                "Amic Deck",
                "Wits Art Musium",
                "Online"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, locations);
        eventLocation.setAdapter(adapter);
    }

    private void setupCategoryDropdown() {
        String[] categories = {"Social", "Sports", "Academic", "Nightlife", "Cultural", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categories);
        eventCategory.setAdapter(adapter);
    }
}
