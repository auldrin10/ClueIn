package com.example.cluein;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    private TextView tvProfileUserName;
    private TextView tvDisplayName;
    private RelativeLayout btnAccount;
    private RelativeLayout btnLogout;
    private LinearLayout updatePic;
    private String name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        btnAccount = view.findViewById(R.id.btnAccount);
        btnLogout = view.findViewById(R.id.btnLogout);
        updatePic = view.findViewById(R.id.updatePic);
        tvProfileUserName = view.findViewById(R.id.profileUserName);
        // Assuming there is a TextView for the full name next to initials
        tvDisplayName = view.findViewById(R.id.tv_display_name); 

        // Load and Handle Initials logic
        setupProfileData();

        updatePic.setOnClickListener(v -> navigateTo(new InsideAccount()));
        btnAccount.setOnClickListener(v -> navigateTo(new UpdateProfilePic()));
        
        btnLogout.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog();
            dialog.show(getParentFragmentManager(), "LogoutDialog");
        });
    }

    private void setupProfileData() {
        // Fetch name from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String name = prefs.getString("user_name", "User Name");
        
        if (tvDisplayName != null) {
            tvDisplayName.setText(name);
        }

        // Generate Initials
        String[] splitted = name.split(" ");
        StringBuilder initials = new StringBuilder();

        if (splitted.length > 0) {
            String firstName = splitted[0];
            if (!firstName.isEmpty()) {
                initials.append(firstName.charAt(0));
            }
            if (splitted.length > 1) {
                String lastName = splitted[splitted.length - 1];
                if (!lastName.isEmpty()) {
                    initials.append(lastName.charAt(0));
                }
            }
        }
        tvProfileUserName.setText(initials.toString().toUpperCase());
    }

    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.FargmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
