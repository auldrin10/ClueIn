package com.example.cluein;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CardViewFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_TITLE = "event_title";
    private static final String ARG_LOCATION = "event_location";
    private static final String ARG_DATE = "event_date";
    private static final String ARG_PRICE = "event_price";
    private static final String ARG_IMAGE_URL = "event_image_url";
    private static final String ARG_DESCRIPTION = "event_description";

    private MapView mapView;
    private GoogleMap googleMap;
    private String eventLocationName;

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
        LinearLayout layoutLocation = view.findViewById(R.id.layout_location);
        mapView = view.findViewById(R.id.mapView);

        if (getArguments() != null) {
            String title = getArguments().getString(ARG_TITLE);
            eventLocationName = getArguments().getString(ARG_LOCATION);
            
            tvTitle.setText(title);
            tvLocation.setText(eventLocationName);
            tvDate.setText(getArguments().getString(ARG_DATE));
            tvPrice.setText("R" + getArguments().getDouble(ARG_PRICE));
            tvDescription.setText(getArguments().getString(ARG_DESCRIPTION));

            Glide.with(this)
                    .load(getArguments().getString(ARG_IMAGE_URL))
                    .centerCrop()
                    .placeholder(R.drawable.dribbble_logo)
                    .into(imgEvent);

            layoutLocation.setOnClickListener(v -> {
                if (eventLocationName != null && !eventLocationName.isEmpty()) {
                    openMap(eventLocationName);
                } else {
                    Toast.makeText(getContext(), "Location not available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Initialize Map
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        if (eventLocationName != null && !eventLocationName.isEmpty()) {
            LatLng latLng = getLatLngFromAddress(eventLocationName);
            if (latLng != null) {
                googleMap.addMarker(new MarkerOptions().position(latLng).title(eventLocationName));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            }
        }
    }

    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            Log.e("CardViewFragment", "Geocoding error: ", e);
        }
        return null;
    }

    private void openMap(String location) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Intent genericMapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            startActivity(genericMapIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}