package com.example.cluein;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    TextInputEditText textEmail;
    TextInputEditText textPassword;
    TextInputLayout emailLayout, pswLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginBtn);
        textEmail = findViewById(R.id.Emailtxt);
        textPassword = findViewById(R.id.pswdtxt);
        emailLayout = findViewById(R.id.emailLayout);
        pswLayout = findViewById(R.id.pswLayout);

        // Set error colors to "Red Red"
        int redRed = Color.parseColor("#FF0000");
        ColorStateList errorColorStateList = ColorStateList.valueOf(redRed);
        
        emailLayout.setErrorTextColor(errorColorStateList);
        emailLayout.setErrorIconTintList(errorColorStateList);
        emailLayout.setBoxStrokeErrorColor(errorColorStateList);
        
        pswLayout.setErrorTextColor(errorColorStateList);
        pswLayout.setErrorIconTintList(errorColorStateList);
        pswLayout.setBoxStrokeErrorColor(errorColorStateList);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Add TextWatchers to clear errors when typing
        textEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    emailLayout.setError(null);
                    emailLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        textPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    pswLayout.setError(null);
                    pswLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;
                isValid = ValidateLogInInputForm(v, isValid);

                if (isValid) {
                    ToDashboard(v);
                }
            }
        });
    }

    private boolean ValidateLogInInputForm(View v, boolean isValid) {
        String password = textPassword.getText().toString();
        String email = textEmail.getText().toString();

        // Clear previous errors
        emailLayout.setError(null);
        pswLayout.setError(null);

        if (password.isEmpty()) {
            pswLayout.setErrorEnabled(true);
            pswLayout.setError("Password is required");
            textPassword.requestFocus();
            isValid = false;
        }
        if (email.isEmpty()) {
            emailLayout.setErrorEnabled(true);
            emailLayout.setError("Email is required");
            textEmail.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    public void ToDashboard(View v) {
        Intent Dashboard = new Intent(this, MainActivity.class);
        startActivity(Dashboard);
    }

    public void ToSignUp(View v) {
        Intent SignUp = new Intent(this, SignUpActivity.class);
        startActivity(SignUp);
    }

    public void ResetActivity(View v) {
        Intent ResetPage = new Intent(this, LostPasswordActivity.class);
        startActivity(ResetPage);
    }
}