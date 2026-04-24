package com.example.cluein;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.*;

public class ProfileFragment extends Fragment {
    private TextView username;
    private TextView tvProfileUserName;
    private TextView account;
    private TextView logOut;

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

        account = view.findViewById(R.id.txtAccount);
        logOut = view.findViewById(R.id.txtLogOut);
        updatePic = view.findViewById(R.id.updatePic);

        tvProfileUserName = view.findViewById(R.id.profileUserName);
        
        // String variable to store the text from the TextView/EditText
        String name = "";
        if (tvProfileUserName != null) {
            name = tvProfileUserName.getText().toString();
        }

        String[] splitted = name.split(" ");
        String initials = "";

        if (splitted.length > 0) {
            String firstName = splitted[0];
            String lastName = splitted[splitted.length - 1];

            if (!firstName.isEmpty()) {
                initials += firstName.charAt(0);
            }
            if (splitted.length > 1 && !lastName.isEmpty()) {
                initials += lastName.charAt(0);
            }


        }



        String upperInitials = initials.toUpperCase();
        tvProfileUserName.setText(upperInitials);


        //    The main part
//    Moving from profile to certain elements.
       updatePic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                Fragment fragment = new UpdateProfilePic();

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.FargmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
           }
       });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new InsideAccount();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                transaction.replace(R.id.FargmentContainer, fragment);

                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new LogOut();

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.FargmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }



}