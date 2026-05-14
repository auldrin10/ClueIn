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

    User user;

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
        user = LoginActivity.user;
        // Load and Handle Initials logic
        tvDisplayName.setText(user.getFirstName() + " " + user.getLastName());

        updatePic.setOnClickListener(v -> navigateTo(new InsideAccount()));
        btnAccount.setOnClickListener(v -> navigateTo(new UpdateProfilePic()));
        
        btnLogout.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog();
            dialog.show(getParentFragmentManager(), "LogoutDialog");
        });
    }



    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.FargmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
