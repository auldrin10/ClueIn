package com.example.cluein;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    private String name;
    String signupURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/users/signup.php";
    OkHttpClient userclient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputLayout nameLayout = findViewById(R.id.fnameLayout);
        TextInputEditText nameInput = findViewById(R.id.txtInptUserFname);
        TextInputLayout emailLayout = findViewById(R.id.emailLayout);
        TextInputEditText emailInput = findViewById(R.id.txtInptEmail);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
        EditText password = findViewById(R.id.txtInptPassword);
        LinearLayout validationLayout = findViewById(R.id.validationLayout);

        ImageView iconLength = findViewById(R.id.iconLength);
        TextView textLength = findViewById(R.id.chckBoxEightChars);
        ImageView iconNum = findViewById(R.id.iconAtLeastNum);
        TextView textNum = findViewById(R.id.chckBoxNumbers);
        ImageView iconLowerCase = findViewById(R.id.iconLower);
        TextView textLowerCase = findViewById(R.id.chckBoxLowerCaseChar);
        ImageView iconUpperCase = findViewById(R.id.iconUpper);
        TextView textUpperCase = findViewById(R.id.chckBoxUpperCaseChar);
        ImageView iconSpecialChar = findViewById(R.id.iconSpecialChar);
        TextView textSpecialChar = findViewById(R.id.chckBoxSpecialChar);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        View dot1 = findViewById(R.id.dot1);

        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                validationLayout.setVisibility(View.VISIBLE);
            } else {
                validationLayout.setVisibility(View.GONE);
            }
        });

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameLayout.setError(null);
                nameLayout.setErrorEnabled(false);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailLayout.setError(null);
                emailLayout.setErrorEnabled(false);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();
                boolean isValid = true;

                if (pass.length() >= 8) {
                    iconLength.setImageResource(R.drawable.ic_check_circle_green);
                    textLength.setTextColor(Color.GREEN);
                } else {
                    iconLength.setImageResource(R.drawable.multiplication);
                    textLength.setTextColor(Color.RED);
                    isValid = false;
                }

                if (pass.matches(".*[0-9].*")) {
                    iconNum.setImageResource(R.drawable.ic_check_circle_green);
                    textNum.setTextColor(Color.GREEN);
                } else {
                    iconNum.setImageResource(R.drawable.multiplication);
                    textNum.setTextColor(Color.RED);
                    isValid = false;
                }

                if (pass.matches(".*[a-z].*")) {
                    iconLowerCase.setImageResource(R.drawable.ic_check_circle_green);
                    textLowerCase.setTextColor(Color.GREEN);
                } else {
                    iconLowerCase.setImageResource(R.drawable.multiplication);
                    textLowerCase.setTextColor(Color.RED);
                    isValid = false;
                }

                if (pass.matches(".*[A-Z].*")) {
                    iconUpperCase.setImageResource(R.drawable.ic_check_circle_green);
                    textUpperCase.setTextColor(Color.GREEN);
                } else {
                    iconUpperCase.setImageResource(R.drawable.multiplication);
                    textUpperCase.setTextColor(Color.RED);
                    isValid = false;
                }

                if (pass.matches(".*[@#$%^&*+=!].*")) {
                    iconSpecialChar.setImageResource(R.drawable.ic_check_circle_green);
                    textSpecialChar.setTextColor(Color.GREEN);
                } else {
                    iconSpecialChar.setImageResource(R.drawable.multiplication);
                    textSpecialChar.setTextColor(Color.RED);
                    isValid = false;
                }

                if (!isValid) {
                    validationLayout.setVisibility(View.VISIBLE);
                    passwordLayout.setError("Password is weak");
                } else {
                    validationLayout.setVisibility(View.GONE);
                    passwordLayout.setError(null);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSignUp.setOnClickListener(v -> {
            boolean isFormValid = true;

            if (nameInput.getText().toString().isEmpty()) {
                nameLayout.setError("Full Name is required");
                isFormValid = false;
            }

            String email = emailInput.getText().toString();
            if (email.isEmpty()) {
                emailLayout.setError("Email Address is required");
                isFormValid = false;
            }

            if (password.getText().toString().isEmpty()) {
                passwordLayout.setError("Password is required");
                isFormValid = false;
            }

            if (isFormValid) {
                // Save email to SharedPreferences so MainActivity knows the user
                getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("USER_EMAIL", email)
                        .apply();
                
                post();
                ToCategory(v);
            }
        });

        dot1.setBackgroundResource(R.drawable.active_dot);
    }

    public void post() {
        String first_name = ((EditText) findViewById(R.id.txtInptUserFname)).getText().toString();
        String last_name = ((EditText) findViewById(R.id.txtInptUserLname)).getText().toString();
        String email = ((EditText) findViewById(R.id.txtInptEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.txtInptPassword)).getText().toString();

        hashpswd passHash = new hashpswd(password);
        String hashedPassword = passHash.getHashed();

        RequestBody body = new FormBody.Builder()
                .add("first_name", first_name)
                .add("last_name", last_name)
                .add("email", email)
                .add("password", hashedPassword)
                .build();

        Request request = new Request.Builder().url(signupURL).post(body).build();

        userclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Network Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseData = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String message = jsonObject.optString("message", "Success");
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parse error", e);
                        }
                    });
                }
            }
        });
    }

    public void ToLogIn(View v) {
        Intent LogIn = new Intent(this, LoginActivity.class);
        startActivity(LogIn);
    }

    public void ToCategory(View v) {
        Intent Category = new Intent(this, CategoryActivity.class);
        startActivity(Category);
    }
}