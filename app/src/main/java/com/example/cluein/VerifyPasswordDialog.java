package com.example.cluein;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerifyPasswordDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_account_password, container, false);

        TextInputEditText etPassword = view.findViewById(R.id.etPassword);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnVerify = view.findViewById(R.id.btnVerify);

        btnCancel.setOnClickListener(v -> dismiss());

        btnVerify.setOnClickListener(v -> {
            String enteredPassword = etPassword.getText().toString();
            
            if (enteredPassword.isEmpty()) {
                etPassword.setError("Password is required");
                return;
            }

            User currentUser = LoginActivity.user;
            if (currentUser != null) {
                String user_id = currentUser.getUserID();
                // Hash the entered password to match stored password format
                hashpswd hasher = new hashpswd(enteredPassword);
                String hashedInput = hasher.getHashed();
                
                if (currentUser.getPassword().equals(hashedInput)) {
                    // Password matches, show the delete confirmation dialog
                    DeleteAccountDialog deleteDialog = new DeleteAccountDialog();
                    deleteDialog.show(getParentFragmentManager(), "DeleteAccountDialog");
                    deleteUser(user_id, hashedInput);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "User session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        return view;
    }
    OkHttpClient client = new OkHttpClient();

    String deleteUserURL =
            "https://wmc.ms.wits.ac.za/students/sgroup2672/users/deteleteuser.php";

    public void deleteUser(String user_id,
                           String user_password) {

        RequestBody body =
                new FormBody.Builder()
                        .add("user_id", user_id)
                        .add("user_password", user_password)
                        .build();

        Request request =
                new Request.Builder()
                        .url(deleteUserURL)
                        .post(body)
                        .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call,
                                  @NonNull IOException e) {

                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(
                                activity,
                                "Network Error",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
                }

                Log.e(
                        "DELETE_USER_ERROR",
                        e.toString()
                );
            }

            @Override
            public void onResponse(@NonNull Call call,
                                   @NonNull Response response)
                    throws IOException {

                String res = response.body() != null ? response.body().string() : "Empty response";

                Log.d(
                        "DELETE_USER_RESPONSE",
                        res
                );

                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(
                                activity,
                                res,
                                Toast.LENGTH_SHORT
                        ).show();
                    });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
