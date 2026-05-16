package com.example.cluein;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CategoryActivity extends AppCompatActivity {
    String postPreferenceURL =
            "https://wmc.ms.wits.ac.za/students/sgroup2672/events/preference.php";

    String getUserURL =
            "https://wmc.ms.wits.ac.za/students/sgroup2672/users/login.php";

    OkHttpClient client = new OkHttpClient();

    Button btnSkip;
    ArrayList<String> Categories = new ArrayList<>();
    private final Set<Integer> selectedCategories = new HashSet<>();
    private int colorDefault;
    private int colorSelected;

    String NewEmail;
    User NewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        colorDefault = ContextCompat.getColor(this, R.color.app_on_background);
        colorSelected = Color.LTGRAY;

        btnSkip = findViewById(R.id.btn_skip);

        int[] cardIds = {
                R.id.card_music,
                R.id.card_food,
                R.id.card_football,
                R.id.card_rugby,
                R.id.card_hackathon,
                R.id.card_worship,
                R.id.card_netball,
                R.id.card_nightlife
        };

        for (int id : cardIds) {
            View card = findViewById(id);
            if (card != null) {
                setupCategoryToggle(card);
            }
        }

        btnSkip.setOnClickListener(v -> postNewUSER());
    }

    private void setupCategoryToggle(View view) {
        view.setOnClickListener(v -> {
            int viewId = view.getId();
            String categoryName = getResources().getResourceEntryName(viewId).replace("card_", "");

            if (selectedCategories.contains(viewId)) {
                selectedCategories.remove(viewId);
                Categories.remove(categoryName);
                setCardBackground(view, colorDefault);
            } else {
                selectedCategories.add(viewId);
                Categories.add(categoryName);
                setCardBackground(view, colorSelected);
            }
            updateButtonText();
        });
    }

    private void setCardBackground(View view, int color) {
        if (view instanceof CardView) {
            ((CardView) view).setCardBackgroundColor(color);
        } else {
            view.setBackgroundColor(color);
        }
    }

    private void updateButtonText() {
        if (Categories.isEmpty()) {
            btnSkip.setText("Skip");
        } else {
            btnSkip.setText("Continue");
        }
    }

    public void postNewUSER() {
        NewEmail = SignUpActivity.NewUser;
        if (NewEmail == null) {
            NewEmail = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("USER_EMAIL", null);
        }

        if (NewEmail == null) {
            Toast.makeText(this, "Session expired, please sign up again", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("email", NewEmail)
                .build();

        Request request = new Request.Builder()
                .url(getUserURL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject obj = new JSONObject(json);
                        if (obj.has("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("error"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String first_name = obj.optString("first_name", "");
                        String last_name = obj.optString("last_name", "");
                        String userID = obj.optString("user_id", "");
                        String password = obj.optString("password", "");

                        NewUser = new User(first_name, last_name, NewEmail, password, userID);
                        
                        // FIX: Initialize the static user object so MainActivity and fragments don't crash
                        LoginActivity.user = NewUser;

                        for (String category : Categories) {
                            postPreference(NewUser.getUserID(), category);
                        }

                        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                        intent.putExtra("USER_EMAIL", NewEmail);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public void postPreference(String userid, String category) {
        RequestBody body = new FormBody.Builder()
                .add("user_id", userid)
                .add("category", category)
                .build();

        Request request = new Request.Builder()
                .url(postPreferenceURL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("INSERT_FAIL", e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("INSERT_RESPONSE", response.body().string());
            }
        });
    }
}
