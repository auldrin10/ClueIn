package com.example.cluein;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePassword extends Fragment {

    private TextInputEditText etNewPassword, etConfirmPassword;
    private TextInputLayout layoutNewPassword, layoutConfirmPassword;
    private LinearLayout validationLayout;
    private ImageView iconLength, iconNum, iconUpper, iconSpecial;
    private TextView tvLength, tvNum, tvUpper, tvSpecial;
    private Button btnResetPassword;
    private ImageView btnBack;

    public ChangePassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        etNewPassword = view.findViewById(R.id.pswdtxt);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        layoutNewPassword = view.findViewById(R.id.layoutNewPassword);
        layoutConfirmPassword = view.findViewById(R.id.layoutConfirmPassword);
        validationLayout = view.findViewById(R.id.validationLayout);

        iconLength = view.findViewById(R.id.iconLength);
        iconNum = view.findViewById(R.id.iconNum);
        iconUpper = view.findViewById(R.id.iconUpper);
        iconSpecial = view.findViewById(R.id.iconSpecial);

        tvLength = view.findViewById(R.id.tvLength);
        tvNum = view.findViewById(R.id.tvNum);
        tvUpper = view.findViewById(R.id.tvUpper);
        tvSpecial = view.findViewById(R.id.tvSpecial);

        btnResetPassword = view.findViewById(R.id.btnResetPassword);
        btnBack = view.findViewById(R.id.btnBack);

        // Back button logic
        btnBack.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        // Show validation rules when focused
        etNewPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                validationLayout.setVisibility(View.VISIBLE);
            } else {
                // Keep it visible if there are errors, otherwise hide
                if (validatePassword(etNewPassword.getText().toString())) {
                    validationLayout.setVisibility(View.GONE);
                }
            }
        });

        // Password validation logic
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();
                validatePassword(pass);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Reset Button logic
        btnResetPassword.setOnClickListener(v -> {
            String newPass = etNewPassword.getText().toString();
            String confirmPass = etConfirmPassword.getText().toString();

            if (!validatePassword(newPass)) {
                Toast.makeText(getContext(), "Password is too weak", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                layoutConfirmPassword.setError("Passwords do not match");
                return;
            } else {
                layoutConfirmPassword.setError(null);
            }

            // TODO: Proceed with password update logic (Firebase/API)
            Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validatePassword(String pass) {
        boolean isValid = true;

        // Rule 1: Length
        if (pass.length() >= 8) {
            iconLength.setImageResource(R.drawable.check);
            tvLength.setTextColor(Color.GREEN);
        } else {
            iconLength.setImageResource(R.drawable.multiplication);
            tvLength.setTextColor(Color.RED);
            isValid = false;
        }

        // Rule 2: Numbers
        if (pass.matches(".*[0-9].*")) {
            iconNum.setImageResource(R.drawable.check);
            tvNum.setTextColor(Color.GREEN);
        } else {
            iconNum.setImageResource(R.drawable.multiplication);
            tvNum.setTextColor(Color.RED);
            isValid = false;
        }

        // Rule 3: Uppercase
        if (pass.matches(".*[A-Z].*")) {
            iconUpper.setImageResource(R.drawable.check);
            tvUpper.setTextColor(Color.GREEN);
        } else {
            iconUpper.setImageResource(R.drawable.multiplication);
            tvUpper.setTextColor(Color.RED);
            isValid = false;
        }

        // Rule 4: Special Characters
        if (pass.matches(".*[@#$%^&*+=!].*")) {
            iconSpecial.setImageResource(R.drawable.check);
            tvSpecial.setTextColor(Color.GREEN);
        } else {
            iconSpecial.setImageResource(R.drawable.multiplication);
            tvSpecial.setTextColor(Color.RED);
            isValid = false;
        }

        if (!isValid) {
            layoutNewPassword.setError("Password is weak");
        } else {
            layoutNewPassword.setError(null);
        }

        return isValid;
    }
}
