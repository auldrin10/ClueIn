package com.example.cluein;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LogoutDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_logout_confirmation, container, false);

        Button btnCancel = view.findViewById(R.id.btnCancelLogout);
        Button btnConfirm = view.findViewById(R.id.btnConfirmLogout);

        btnCancel.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> {
            // Step 1: Clear Remember Me / Auto-Login preferences
            if (getActivity() != null) {
                SharedPreferences loginPrefs = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginPrefs.edit();
                editor.putBoolean("remember", false);
                editor.remove("email");
                editor.remove("password");
                editor.apply();

                // Also clear general UserPrefs used for email identification in fragments
                SharedPreferences userPrefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                userPrefs.edit().clear().apply();
            }

            // Step 2: Go back to Login Activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
