package com.example.cluein;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    OkHttpClient client;
    Button loginBtn;
    String postURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/users/login.php";
    TextInputEditText textEmail;
    TextInputEditText textPassword;
    TextView display;
    TextInputLayout emailLayout, pswLayout;
    ImageView btnGoogle;
    CheckBox rememberMe;
    public static User user;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        client = new OkHttpClient();
        loginBtn = findViewById(R.id.loginBtn);
        textEmail = findViewById(R.id.Emailtxt);
        textPassword = findViewById(R.id.pswdtxt);
        emailLayout = findViewById(R.id.emailLayout);
        pswLayout = findViewById(R.id.pswLayout);
        display = findViewById(R.id.textView2);
        rememberMe = findViewById(R.id.checkBox);

        btnGoogle = findViewById(R.id.btnGoogle);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        int redRed = Color.parseColor("#FF0000");
        ColorStateList errorColorStateList = ColorStateList.valueOf(redRed);

        emailLayout.setErrorTextColor(errorColorStateList);
        emailLayout.setErrorIconTintList(errorColorStateList);
        emailLayout.setBoxStrokeErrorColor(errorColorStateList);

        pswLayout.setErrorTextColor(errorColorStateList);
        pswLayout.setErrorIconTintList(errorColorStateList);
        pswLayout.setBoxStrokeErrorColor(errorColorStateList);

        // Load Remember Me data and trigger Auto-Login
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isRemembered = preferences.getBoolean(KEY_REMEMBER, false);
        if (isRemembered) {
            String savedEmail = preferences.getString(KEY_EMAIL, "");
            String savedPassword = preferences.getString(KEY_PASSWORD, "");
            
            textEmail.setText(savedEmail);
            textPassword.setText(savedPassword);
            rememberMe.setChecked(true);

            // Auto-trigger the login process
            if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
                post();
            }
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

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
                    post();
                }
            }
        });

        btnGoogle.setOnClickListener(v -> {
            signIn();
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            if (account != null) {
                String email = account.getEmail();
                String displayName = account.getDisplayName();
                String firstName = account.getGivenName();
                String lastName = account.getFamilyName();
                String googleId = account.getId();

                // Create a temporary User object for the session
                user = new User(firstName, lastName, email, "", googleId);

                // Save email to SharedPreferences
                getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("USER_EMAIL", email)
                        .apply();

                ToDashboard(null);
            }
        } catch (ApiException e) {
            Log.w("GOOGLE_AUTH", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
        startActivity(intent);
    }

    private boolean ValidateLogInInputForm(View v, boolean isValid) {
        String password = textPassword.getText().toString();
        String email = textEmail.getText().toString();

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
    String email;
    public void post() {
        email = textEmail.getText().toString();

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .build();

        Request request = new Request.Builder()
                .url(postURL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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
                                
                                user = new User(first_name, last_name, email, passwordFetched, userID);
                                AuthenticateUser();
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

    private void AuthenticateUser() {
        if (user == null) {
            Log.e("AUTH", "User object is null, cannot authenticate.");
            return;
        }
        
        hashpswd passHash = new hashpswd(textPassword.getText().toString());
        String inputPassword = passHash.getHashed();
        String storedPassword = user.getPassword();
        String storedEmail = user.getEmail();
        if (Objects.equals(storedEmail, email) && Objects.equals(storedPassword, inputPassword)) {
            // Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show(); // Removed for smoother auto-login

            // Save Remember Me state
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (rememberMe.isChecked()) {
                editor.putBoolean(KEY_REMEMBER, true);
                editor.putString(KEY_EMAIL, textEmail.getText().toString());
                editor.putString(KEY_PASSWORD, textPassword.getText().toString());
            } else {
                editor.putBoolean(KEY_REMEMBER, false);
                editor.remove(KEY_EMAIL);
                editor.remove(KEY_PASSWORD);
            }
            editor.apply();

            ToDashboard(null);
        } else {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
            // If auto-login failed because password changed, clear the remembered state
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
        }
    }

    public void ToDashboard(View v) {
        Intent Dashboard = new Intent(this, MainActivity.class);
        if (user != null) {
            String loggedInEmail = user.getEmail();
            Dashboard.putExtra("USER_EMAIL", loggedInEmail);
            
            // Save email to SharedPreferences for fragments to access
            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("USER_EMAIL", loggedInEmail)
                    .apply();
        }
        startActivity(Dashboard);
        finish();
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
