package com.example.cluein;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteAccountDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Step 1: Inflate the layout we created for the dialog
        View view = inflater.inflate(R.layout.dialog_delete_confirmation, container, false);

        // Step 2: Initialize the buttons
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirmDelete = view.findViewById(R.id.btnConfirmDelete);

        // Step 3: Set up the click listeners
        btnCancel.setOnClickListener(v -> {
            // Simply close the dialog if "No" is pressed
            dismiss();
        });

        btnConfirmDelete.setOnClickListener(v -> {
            // Perform the delete action here
            // e.g., deleteUserFromFirebase();
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // This ensures the dialog has a nice width on the screen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
