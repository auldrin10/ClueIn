package com.example.cluein;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CategoryActivity extends AppCompatActivity {
String postURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/preference.php";
String postNewURL = " https://wmc.ms.wits.ac.za/students/sgroup2672/users/login.php";
    private Button btnSkip;
    // Keep track of selected category view IDs
    private final Set<Integer> selectedCategories = new HashSet<>();

    // Define your colors (replace with your actual color resources)
    ArrayList<String> Categories = new ArrayList<>();
    private int colorDefault;
    private int colorSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        // Initialize colors
        colorDefault = ContextCompat.getColor(this,R.color.app_on_background);
        colorSelected = Color.LTGRAY; // The color when "clicked"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSkip = findViewById(R.id.btn_skip);

        // 1. Setup toggle logic for each category card
        int[] cardIds = {
                R.id.card_music, R.id.card_food, R.id.card_football,
                R.id.card_rugby, R.id.card_hackathon, R.id.card_worship,
                R.id.card_netball, R.id.card_nightlife
        };

        for (int id : cardIds) {
            setupCategoryToggle(findViewById(id));
        }

        // 2. Handle Skip/Continue Button
        btnSkip.setOnClickListener(v -> {
            // Animation for the button
            postNewUSER();
            for (String category : Categories) {
                postPreference(NewUser.getUserID(), category);
            }
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {
                    Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                    // Optional: Pass selectedCategories to the next activity
                    startActivity(intent);

                    finish();
                });
            });
        });
    }

    /**
     * Handles the selection logic: background color change and button text update.
     */
    private void setupCategoryToggle(View view) {
        if (view == null) return;

        view.setOnClickListener(v -> {
            // Animation
            view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {

                    int viewId = view.getId();
                    String categoryName = getResources().getResourceEntryName(viewId).replace("card_", "");
                    if (selectedCategories.contains(viewId)) {
                        // Deselect
                        selectedCategories.remove(viewId);
                        Categories.remove(categoryName);
                        Log.d("Selected Categories", selectedCategories.toString());
                        setCardBackground(view, colorDefault);
                    } else {
                        // Select
                        selectedCategories.add(viewId);
                        Categories.add(categoryName);
                        setCardBackground(view, colorSelected);
                    }
Log.d("Selected Categories", Categories.toString());
                    // Update Skip button text
                    updateSkipButtonText();
                });
            });
        });
    }

    private void setCardBackground(View view, int color) {
        if (view instanceof CardView) {
            ((CardView) view).setCardBackgroundColor(color);
        } else {
            view.setBackgroundColor(color);
        }
    }

    private void updateSkipButtonText() {
        if (selectedCategories.isEmpty()) {
            btnSkip.setText("Skip");
        } else {
            btnSkip.setText("Continue");
        }
    }
    OkHttpClient client2 = new OkHttpClient();
    String NewEmail;
    User NewUser;
    public void postNewUSER() {
        NewEmail ="mannagnyibrighton2@gmail.com";

        RequestBody body = new FormBody.Builder()
                .add("email", NewEmail)
                .build();

        Request request = new Request.Builder()
                .url(postNewURL)
                .post(body)
                .build();

        client2.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Call call, @androidx.annotation.NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@androidx.annotation.NonNull Call call, @androidx.annotation.NonNull Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(json);

                            if (obj.has("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("error"), Toast.LENGTH_SHORT).show();
                            } else {
                                String first_name = obj.optString("first_name", "");
                                String last_name = obj.optString("last_name", "");
                                String userID = obj.optString("user_id", "");
                                String passwordFetched = obj.optString("password", "");

                                NewUser = new User(first_name, last_name, NewEmail, passwordFetched, userID);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Email does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void postPreference(String userid, String category) {

        RequestBody body = new FormBody.Builder()
                .add("use_id", userid)
                .add("category", category)
                .build();

        Request request = new Request.Builder()
                .url(postURL)
                .post(body)
                .build();

        client2.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call,
                                  @NonNull IOException e) {

                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(),
                                "Insert Failed",
                                Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call,
                                   @NonNull Response response)
                    throws IOException {

                String res = response.body().string();

                runOnUiThread(() -> {

                    Log.d("SERVER_RESPONSE", res);

                });
            }
        });
    }
}