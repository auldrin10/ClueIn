package com.example.cluein;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class UpdateProfilePic extends Fragment {

    private RelativeLayout btnUpdateCategory, btnChangePassword, btnDeleteAccount;
    private ImageView btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_profile_pic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        btnUpdateCategory = view.findViewById(R.id.btnUpdateCategory);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        btnBack = view.findViewById(R.id.btnBack);

        // Back button
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Update Category
        btnUpdateCategory.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.FargmentContainer, new UpdateCategory())
                    .addToBackStack(null)
                    .commit();
        });

        // Change Password
        btnChangePassword.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.FargmentContainer, new ChangePassword())
                    .addToBackStack(null)
                    .commit();
        });

        // Delete Account - This shows the small pop-up (DialogFragment)
        btnDeleteAccount.setOnClickListener(v -> {
            DeleteAccountDialog dialog = new DeleteAccountDialog();
            dialog.show(getParentFragmentManager(), "DeleteDialog");
        });
    }
}
