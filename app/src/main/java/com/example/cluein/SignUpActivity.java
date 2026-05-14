package com.example.cluein;

import static android.service.controls.ControlsProviderService.TAG;

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

        /* Instances for input fields and layouts */
        TextInputLayout nameLayout = findViewById(R.id.fnameLayout);
        TextInputEditText nameInput = findViewById(R.id.txtInptUserFname);
        TextInputLayout emailLayout = findViewById(R.id.emailLayout);
        TextInputEditText emailInput = findViewById(R.id.txtInptEmail);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
        EditText password = findViewById(R.id.txtInptPassword);
        LinearLayout validationLayout = findViewById(R.id.validationLayout);

        /* Instance for imageview and textview */
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

        ImageView btnGoogle = findViewById(R.id.btnGoogle);
        ImageView btnInstagram = findViewById(R.id.btnInstagram);
        ImageView btnFacebook = findViewById(R.id.btnFacebook);

        btnGoogle.setOnClickListener(v -> openUrl("https://www.google.com"));
        btnInstagram.setOnClickListener(v -> openUrl("https://www.instagram.com"));
        btnFacebook.setOnClickListener(v -> openUrl("https://www.facebook.com"));

        /* Password only appears when users focuses on the inputbox*/
        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                validationLayout.setVisibility(View.VISIBLE);
            } else {
                validationLayout.setVisibility(View.GONE);
            }
        });

        /* TextWatchers to clear errors and show ticks while typing */
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameLayout.setError(null);
                nameLayout.setErrorEnabled(false);
                if (s.length() > 0) {
                    nameInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_account_circle_24, 0);
                } else {
                    nameInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_account_circle_24, 0);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailLayout.setError(null);
                emailLayout.setErrorEnabled(false);
                if (s.length() > 0 && android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    emailInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_green, 0);
                } else {
                    emailInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_email_24, 0);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();
                boolean isValid = true;

                // Rule 1: Length
                if (pass.length() >= 8) {
                    iconLength.setImageResource(R.drawable.ic_check_circle_green);
                    textLength.setTextColor(Color.GREEN);
                } else {
                    iconLength.setImageResource(R.drawable.multiplication);
                    textLength.setTextColor(Color.RED);
                    isValid = false;
                }

                // Rule 2: Numbers
                if (pass.matches(".*[0-9].*")) {
                    iconNum.setImageResource(R.drawable.ic_check_circle_green);
                    textNum.setTextColor(Color.GREEN);
                } else {
                    iconNum.setImageResource(R.drawable.multiplication);
                    textNum.setTextColor(Color.RED);
                    isValid = false;
                }

                // Rule 3: Lowercase
                if (pass.matches(".*[a-z].*")) {
                    iconLowerCase.setImageResource(R.drawable.ic_check_circle_green);
                    textLowerCase.setTextColor(Color.GREEN);
                } else {
                    iconLowerCase.setImageResource(R.drawable.multiplication);
                    textLowerCase.setTextColor(Color.RED);
                    isValid = false;
                }

                // Rule 4: Uppercase
                if (pass.matches(".*[A-Z].*")) {
                    iconUpperCase.setImageResource(R.drawable.ic_check_circle_green);
                    textUpperCase.setTextColor(Color.GREEN);
                } else {
                    iconUpperCase.setImageResource(R.drawable.multiplication);
                    textUpperCase.setTextColor(Color.RED);
                    isValid = false;
                }

                // Rule 5: Special Character
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
                    passwordLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));
                    passwordLayout.setError("Password is weak");
                    passwordLayout.setErrorIconDrawable(null);
                } else {
                    validationLayout.setVisibility(View.GONE);
                    passwordLayout.setError("Password is strong");
                    passwordLayout.setErrorIconDrawable(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSignUp.setOnClickListener(v -> {
            boolean isFormValid = true;

            if (nameInput.getText().toString().isEmpty()) {
                nameLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));
                nameLayout.setError("Full Name is required");
                isFormValid = false;
            }

            if (emailInput.getText().toString().isEmpty()) {
                emailLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));
                emailLayout.setError("Email Address is required");
                isFormValid = false;
            }

            if (password.getText().toString().isEmpty()) {
                passwordLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));
                passwordLayout.setError("Password is required");
                isFormValid = false;
            } else if (passwordLayout.getError() != null && passwordLayout.getError().equals("Password is weak")) {
                isFormValid = false;
            }

            if (isFormValid) {
                post();
            }
        });

        dot1.setBackgroundResource(R.drawable.active_dot);
    }

    public void post() {
        String first_name, last_name, email, password;
        first_name = ((EditText) findViewById(R.id.txtInptUserFname)).getText().toString();
        last_name = ((EditText) findViewById(R.id.txtInptUserLname)).getText().toString();
        email = ((EditText) findViewById(R.id.txtInptEmail)).getText().toString();
        password = ((EditText) findViewById(R.id.txtInptPassword)).getText().toString();

        hashpswd passHash = new hashpswd(password);
        String hashedPassword = passHash.getHashed();

        RequestBody body = new FormBody.Builder()
                .add("first_name", first_name)
                .add("last_name", last_name)
                .add("email", email)
                .add("password", hashedPassword)
                .build();

        Request request = new Request.Builder()
                .url(signupURL)
                .post(body)
                .build();

        userclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(SignUpActivity.this, "Network Error", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d("PHP_RESPONSE", responseData);

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

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
        startActivity(intent);
    }
}
