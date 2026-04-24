package com.example.cluein;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class CardViewFragment extends Fragment {

    private static final String ARG_TITLE = "event_title";
    private static final String ARG_LOCATION = "event_location";
    private static final String ARG_DATE = "event_date";
    private static final String ARG_PRICE = "event_price";
    private static final String ARG_IMAGE_URL = "event_image_url";
    private static final String ARG_DESCRIPTION = "event_description";

    public static CardViewFragment newInstance(Event event) {
        CardViewFragment fragment = new CardViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, event.getEvent_title());
        args.putString(ARG_LOCATION, event.getLocation());
        args.putString(ARG_DATE, event.getEventDate());
        args.putDouble(ARG_PRICE, event.getPrice());
        args.putString(ARG_IMAGE_URL, event.getImageURL());
        args.putString(ARG_DESCRIPTION, event.getDescription());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_view, container, false);

        ImageView imgEvent = view.findViewById(R.id.frag_img_event);
        TextView tvTitle = view.findViewById(R.id.frag_tv_title);
        TextView tvLocation = view.findViewById(R.id.frag_tv_location);
        TextView tvDate = view.findViewById(R.id.frag_tv_date);
        TextView tvPrice = view.findViewById(R.id.frag_tv_price);
        TextView tvDescription = view.findViewById(R.id.frag_tv_description);
        ImageView btnClose = view.findViewById(R.id.btn_close_fragment);

        if (getArguments() != null) {
            tvTitle.setText(getArguments().getString(ARG_TITLE));
            tvLocation.setText(getArguments().getString(ARG_LOCATION));
            tvDate.setText(getArguments().getString(ARG_DATE));
            tvPrice.setText("R" + getArguments().getDouble(ARG_PRICE));
            tvDescription.setText(getArguments().getString(ARG_DESCRIPTION));

            Glide.with(this)
                    .load(getArguments().getString(ARG_IMAGE_URL))
                    .centerCrop()
                    .placeholder(R.drawable.dribbble_logo)
                    .into(imgEvent);
        }

        btnClose.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().remove(CardViewFragment.this).commit();
            }
        });

        // Close when clicking outside the card (on the dim background)
        view.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().remove(CardViewFragment.this).commit();
            }
        });

        return view;
    }
}