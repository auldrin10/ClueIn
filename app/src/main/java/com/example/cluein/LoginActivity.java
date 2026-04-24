package com.example.cluein;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    TextView textEmail;
    TextView textPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginBtn);
        textEmail = findViewById(R.id.Emailtxt);
        textPassword = findViewById(R.id.pswdtxt);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }



        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isValid = true;
                isValid = ValidateLogInInputForm(v, isValid);

                if(isValid){
                    ToDashboard(v);
                }
            }
        });
    }
    private  boolean ValidateLogInInputForm(View v , boolean isValid){
        String password = textPassword.getText().toString();
        String email = textEmail.getText().toString();

        if(password.isEmpty()){
            textPassword.setError("Password is required");
            textPassword.requestFocus();
            isValid = false;
        }
        if(email.isEmpty()){
            textEmail.setError("Email is required");
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

