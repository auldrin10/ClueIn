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
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FacebookAuthProvider;
import java.util.Arrays;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;
import android.content.Context;

public class SignUpActivity extends AppCompatActivity {
    public  static  String NewUser;
    String signupURL = "https://wmc.ms.wits.ac.za/students/sgroup2672/users/signup.php";
    OkHttpClient userclient = new OkHttpClient();
    
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // Explicitly initialize Facebook SDK before any LoginManager calls
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplicationContext());

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
                        Toast.makeText(SignUpActivity.this, "Facebook Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });

        btnGoogle.setOnClickListener(v -> signInWithGoogle());
        btnInstagram.setOnClickListener(v -> startInstagramAuth());
        btnFacebook.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(this, mCallbackManager, Arrays.asList("email", "public_profile"));
        });

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
                ToCategory(v);
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
        NewUser = email;
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
                            // Save to SharedPreferences for AddEvent authorization
                            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("USER_EMAIL", firebaseUser.getEmail())
                                    .apply();

                            Toast.makeText(this, "Google Registration Successful", Toast.LENGTH_SHORT).show();
                            
                            // Redirect to Dashboard (or Category choice if you prefer)
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("USER_EMAIL", firebaseUser.getEmail());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
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
                            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("USER_EMAIL", firebaseUser.getEmail())
                                    .apply();

                            Toast.makeText(this, "Facebook Registration Successful", Toast.LENGTH_SHORT).show();
                            
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("USER_EMAIL", firebaseUser.getEmail());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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

        userclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Instagram Token Error", Toast.LENGTH_SHORT).show());
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
        userclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Instagram Profile Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String username = json.getString("username");

                        runOnUiThread(() -> {
                            String placeholderEmail = username + "@instagram.com";
                            
                            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("USER_EMAIL", placeholderEmail)
                                    .apply();

                            Toast.makeText(SignUpActivity.this, "Instagram Registration: " + username, Toast.LENGTH_SHORT).show();
                            
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.putExtra("USER_EMAIL", placeholderEmail);
                            startActivity(intent);
                            finish();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
        startActivity(intent);
    }
}
