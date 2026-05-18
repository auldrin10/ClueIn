package com.example.cluein;

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
    // NETWORK
    OkHttpClient client = new OkHttpClient();
    // UI
    Button btnSkip;

    ArrayList<String> Categories = new ArrayList<>();

    private final Set<Integer> selectedCategories =
            new HashSet<>();

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

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });
        // COLORS
        colorDefault =
                ContextCompat.getColor(
                        this,
                        R.color.app_on_background
                );

        colorSelected = Color.LTGRAY;
        // BUTTON
        btnSkip = findViewById(R.id.btn_skip);
        // CATEGORY CARDS
        int[] cardIds = {

                R.id.card_music,
                R.id.card_football,
                R.id.card_socials,
                R.id.card_academics,
                R.id.card_financial_literacy,
                R.id.card_career_expo
        };

        for (int id : cardIds) {

            View card = findViewById(id);

            if (card != null) {

                setupCategoryToggle(card);

            } else {

                Log.e("CARD_ERROR",
                        "Card not found: " + id);
            }
        }
        // BUTTON CLICK
        btnSkip.setOnClickListener(v -> {

            // get user first
            postNewUSER();

        });
    }


    // CATEGORY TOGGLE
    private void setupCategoryToggle(View view) {

        view.setOnClickListener(v -> {

            int viewId = view.getId();

            String categoryName =
                    getResources()
                            .getResourceEntryName(viewId)
                            .replace("card_", "");

            if (selectedCategories.contains(viewId)) {

                // REMOVE
                selectedCategories.remove(viewId);

                Categories.remove(categoryName);

                setCardBackground(view, colorDefault);

            } else {

                // ADD
                selectedCategories.add(viewId);

                Categories.add(categoryName);

                setCardBackground(view, colorSelected);
            }

            Log.d("CATEGORIES",
                    Categories.toString());

            updateButtonText();
        });
    }

    // CARD COLOR

    private void setCardBackground(View view,
                                   int color) {

        if (view instanceof CardView) {

            ((CardView) view)
                    .setCardBackgroundColor(color);

        } else {

            view.setBackgroundColor(color);
        }
    }


    // BUTTON TEXT


    private void updateButtonText() {

        if (Categories.isEmpty()) {

            btnSkip.setText("Skip");

        } else {

            btnSkip.setText("Continue");
        }
    }


    // GET USER USING EMAIL


    public void postNewUSER() {

        // email from signup
        NewEmail = SignUpActivity.NewUser;

        Log.d("EMAIL_SENT", NewEmail);

        RequestBody body =
                new FormBody.Builder()

                        .add("email", NewEmail)

                        .build();

        Request request =
                new Request.Builder()

                        .url(getUserURL)

                        .post(body)

                        .build();

        client.newCall(request)
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(
                            @NonNull Call call,
                            @NonNull IOException e) {

                        runOnUiThread(() ->

                                Toast.makeText(
                                        getApplicationContext(),
                                        "Network Error",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );

                        Log.e("NETWORK_ERROR",
                                e.toString());
                    }

                    @Override
                    public void onResponse(
                            @NonNull Call call,
                            @NonNull Response response)
                            throws IOException {

                        String json =
                                response.body().string();

                        Log.d("USER_RESPONSE",
                                json);

                        runOnUiThread(() -> {

                            try {

                                JSONObject obj =
                                        new JSONObject(json);

                                // check error
                                if (obj.has("error")) {

                                    Toast.makeText(
                                            getApplicationContext(),
                                            obj.getString("error"),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                String first_name =
                                        obj.optString(
                                                "first_name",
                                                ""
                                        );

                                String last_name =
                                        obj.optString(
                                                "last_name",
                                                ""
                                        );

                                String userID =
                                        obj.optString(
                                                "user_id",
                                                ""
                                        );

                                String password =
                                        obj.optString(
                                                "password",
                                                ""
                                        );

                                // create user
                                NewUser = new User(
                                        first_name,
                                        last_name,
                                        NewEmail,
                                        password,
                                        userID
                                );

                                Log.d("USER_ID",
                                        NewUser.getUserID());


                                // INSERT ALL CATEGORIES


                                for (String category :
                                        Categories) {

                                    postPreference(
                                            NewUser.getUserID(),
                                            category
                                    );
                                }



                                Intent intent =
                                        new Intent(
                                                CategoryActivity.this,
                                                MainActivity.class
                                        );

                                startActivity(intent);

                                finish();

                            } catch (JSONException e) {

                                e.printStackTrace();

                                Toast.makeText(
                                        getApplicationContext(),
                                        "JSON Error",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Log.e("JSON_ERROR",
                                        e.toString());
                            }
                        });
                    }
                });
    }


    // INSERT PREFERENCE


    public void postPreference(
            String userid,
            String category) {

        Log.d("INSERT_USERID", userid);

        Log.d("INSERT_CATEGORY", category);

        RequestBody body =
                new FormBody.Builder()

                        // IMPORTANT
                        .add("user_id", userid)
                        .add("category", category)
                        .build();

        Request request =
                new Request.Builder()

                        .url(postPreferenceURL)
                        .post(body)
                        .build();

        client.newCall(request)
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(
                            @NonNull Call call,
                            @NonNull IOException e) {

                        runOnUiThread(() ->

                                Toast.makeText(
                                        getApplicationContext(),
                                        "Insert Failed",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );

                        Log.e("INSERT_FAIL",
                                e.toString());
                    }

                    @Override
                    public void onResponse(
                            @NonNull Call call,
                            @NonNull Response response)
                            throws IOException {

                        String res =
                                response.body().string();

                        Log.d("INSERT_RESPONSE",
                                res);
                    }
                });
    }
}