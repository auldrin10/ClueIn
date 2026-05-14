package com.example.cluein;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
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
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FacebookAuthProvider;
import java.util.Arrays;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;

public class LoginActivity extends AppCompatActivity {
    OkHttpClient client;
    Button loginBtn;
    String postURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/users/login.php";
    TextInputEditText textEmail;
    TextInputEditText textPassword;
    TextView display;
    TextInputLayout emailLayout, pswLayout;
    ImageView btnGoogle, btnInstagram, btnFacebook;
    public static User user;
    
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private CallbackManager mCallbackManager;

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

        btnGoogle = findViewById(R.id.btnGoogle);
        btnInstagram = findViewById(R.id.btnInstagram);
        btnFacebook = findViewById(R.id.btnFacebook);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Define the Launcher for Google Sign-In result
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                firebaseAuthWithGoogle(account.getIdToken());
                            }
                        } catch (ApiException e) {
                            Log.w("GOOGLE_AUTH", "Google sign in failed", e);
                            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Initialize Facebook Login
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("FB_AUTH", "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("FB_AUTH", "facebook:onError", error);
                        Toast.makeText(LoginActivity.this, "Facebook Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });

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

        btnGoogle.setOnClickListener(v -> signInWithGoogle());
        btnInstagram.setOnClickListener(v -> startInstagramAuth());
        btnFacebook.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(this, mCallbackManager, Arrays.asList("email", "public_profile"));
        });
    }

    private void startInstagramAuth() {
        String clientId = getString(R.string.instagram_client_id);
        String redirectUri = getString(R.string.instagram_redirect_uri);
        String authUrl = "https://api.instagram.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=user_profile,user_media" +
                "&response_type=code";

        InstagramDialog dialog = new InstagramDialog(this, authUrl, redirectUri, code -> {
            getInstagramToken(code);
        });
        dialog.show();
    }

    private void getInstagramToken(String code) {
        String clientId = getString(R.string.instagram_client_id);
        String clientSecret = getString(R.string.instagram_client_secret);
        String redirectUri = getString(R.string.instagram_redirect_uri);

        RequestBody formBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("grant_type", "authorization_code")
                .add("redirect_uri", redirectUri)
                .add("code", code)
                .build();

        Request request = new Request.Builder()
                .url("https://api.instagram.com/oauth/access_token")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Instagram Token Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String accessToken = json.getString("access_token");
                        String userId = json.getString("user_id");
                        getInstagramUserProfile(accessToken, userId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getInstagramUserProfile(String accessToken, String userId) {
        String url = "https://graph.instagram.com/" + userId + "?fields=id,username&access_token=" + accessToken;

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Instagram Profile Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String username = json.getString("username");
                        
                        runOnUiThread(() -> {
                            // Since Instagram doesn't give email easily with Basic Display API,
                            // we'll use "username@instagram.com" as a placeholder email for your system
                            String placeholderEmail = username + "@instagram.com";
                            
                            // Map to your internal User object
                            user = new User(username, "", placeholderEmail, "", userId);
                            
                            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("USER_EMAIL", placeholderEmail)
                                    .apply();

                            Toast.makeText(LoginActivity.this, "Instagram Login: " + username, Toast.LENGTH_SHORT).show();
                            ToDashboard(null);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            user = new User(
                                    firebaseUser.getDisplayName(),
                                    "",
                                    firebaseUser.getEmail(),
                                    "",
                                    firebaseUser.getUid()
                            );
                            
                            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("USER_EMAIL", firebaseUser.getEmail())
                                    .apply();

                            Toast.makeText(this, "Facebook Login Successful", Toast.LENGTH_SHORT).show();
                            ToDashboard(null);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Map Firebase user to your internal User object
                            user = new User(
                                    firebaseUser.getDisplayName(), 
                                    "", 
                                    firebaseUser.getEmail(), 
                                    "", 
                                    firebaseUser.getUid()
                            );
                            
                            // Save to SharedPreferences for AddEvent authorization
                            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("USER_EMAIL", firebaseUser.getEmail())
                                    .apply();

                            Toast.makeText(this, "Google Login Successful", Toast.LENGTH_SHORT).show();
                            ToDashboard(null);
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
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
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            ToDashboard(null);
        } else {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
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
