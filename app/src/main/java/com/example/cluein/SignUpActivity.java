package com.example.cluein;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {
private String name;
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
        TextInputLayout nameLayout = findViewById(R.id.nameLayout);
        TextInputEditText nameInput = findViewById(R.id.txtInptUser);
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

        /* Password only appears when users focuses on the inputbox */
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
                    nameInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_green, 0);
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
                ToCategory(v);
            }
        });

        dot1.setBackgroundResource(R.drawable.active_dot);
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
