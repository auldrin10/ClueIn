package com.example.cluein;

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
    private RelativeLayout btnAccount;
    private RelativeLayout btnLogout;
    private LinearLayout updatePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        // Handle Initials logic
        setupInitials();

        // 1. Tapping the Profile Header (Auldrin Matlala) takes you to Account Details
        updatePic.setOnClickListener(v -> {
            Fragment fragment = new InsideAccount();
            navigateTo(fragment);
        });

        // 2. Tapping the "Account" row (or the arrow) takes you to Account Settings (UpdateProfilePic)
        btnAccount.setOnClickListener(v -> {
            Fragment fragment = new UpdateProfilePic();
            navigateTo(fragment);
        });

        // 3. Tapping the "Log out" row shows the confirmation dialog
        btnLogout.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog();
            dialog.show(getParentFragmentManager(), "LogoutDialog");
        });
    }

    private void setupInitials() {
        // Hardcoded or fetched name
        String name = "";
        // Note: You might want to get this from a User object or Firebase later
        
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
