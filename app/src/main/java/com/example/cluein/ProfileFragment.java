package com.example.cluein;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    private TextView tvProfileUserName;
    private TextView tvDisplayName;
    private RelativeLayout btnAccount;
    private RelativeLayout btnNotifications;
    private RelativeLayout btnLogout;
    private RelativeLayout btnTheme;
    private ImageView imgThemeIcon;
    private LinearLayout updatePic;
    private User user;

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
        btnNotifications = view.findViewById(R.id.btnNotifications);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnTheme = view.findViewById(R.id.btnTheme);
        imgThemeIcon = view.findViewById(R.id.imgThemeIcon);
        updatePic = view.findViewById(R.id.updatePic);
        tvProfileUserName = view.findViewById(R.id.profileUserName);
        tvDisplayName = view.findViewById(R.id.tv_display_name); 

        user = LoginActivity.user;
        if (user != null) {
            tvDisplayName.setText(user.getFirstName() + " " + user.getLastName());
        }

        // Initialize Theme state from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DarkTheme", false);
        updateThemeIcon(isDarkMode);

        // Toggle theme on clicking the theme row
        btnTheme.setOnClickListener(v -> {
            boolean currentMode = sharedPreferences.getBoolean("DarkTheme", false);
            boolean newMode = !currentMode;
            
            if (newMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            
            // Update the icon UI
            updateThemeIcon(newMode);
            
            // Save preference immediately
            sharedPreferences.edit().putBoolean("DarkTheme", newMode).apply();
        });

        updatePic.setOnClickListener(v -> navigateTo(new InsideAccount()));
        btnAccount.setOnClickListener(v -> navigateTo(new UpdateProfilePic()));
        
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> navigateTo(new NotificationFragment()));
        }

        btnLogout.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog();
            dialog.show(getParentFragmentManager(), "LogoutDialog");
        });
    }

    private void updateThemeIcon(boolean isDarkMode) {
        if (isDarkMode) {
            imgThemeIcon.setImageResource(R.drawable.ic_moon);
        } else {
            imgThemeIcon.setImageResource(R.drawable.ic_sun);
        }
    }

    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.FargmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
