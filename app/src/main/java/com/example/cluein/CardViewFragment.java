package com.example.cluein;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CardViewFragment extends Fragment {

    private static final String ARG_TITLE = "event_title";
    private static final String ARG_LOCATION = "event_location";
    private static final String ARG_DATE = "event_date";
    private static final String ARG_PRICE = "event_price";
    private static final String ARG_IMAGE_URL = "event_image_url";
    private static final String ARG_DESCRIPTION = "event_description";

    private MapView map = null; 
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

        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        View view = inflater.inflate(R.layout.fragment_card_view, container, false);

        ImageView imgEvent = view.findViewById(R.id.frag_img_event);
        TextView tvTitle = view.findViewById(R.id.frag_tv_title);
        TextView tvLocation = view.findViewById(R.id.frag_tv_location);
        TextView tvDate = view.findViewById(R.id.frag_tv_date);
        TextView tvPrice = view.findViewById(R.id.frag_tv_price);
        TextView tvDescription = view.findViewById(R.id.frag_tv_description);
        ImageView btnClose = view.findViewById(R.id.btn_close_fragment);
        LinearLayout layoutLocation = view.findViewById(R.id.layout_location);

        //Initialize a map view
        map = view.findViewById(R.id.map);

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
                    openExternalMap(eventLocationName);
                }
            });
        }

        setupMap();

        btnClose.setOnClickListener(v -> closeFragment());
        view.setOnClickListener(v -> closeFragment());

        return view;
    }

    private void setupMap() {
        if (map == null) return;

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(17.0);

        // Show User Location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), map);
            locationOverlay.enableMyLocation();
            map.getOverlays().add(locationOverlay);
        }

        // Show Event Marker
        if (eventLocationName != null && !eventLocationName.isEmpty()) {
            GeoPoint eventPoint = getGeoPointFromAddress("Wits " + eventLocationName);
            if (eventPoint != null) {
                Marker marker = new Marker(map);
                marker.setPosition(eventPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle(eventLocationName);
                
                // Add the click listener to the marker here
                marker.setOnMarkerClickListener((m, mapView) -> {
                    openExternalMap(eventLocationName);
                    return true;
                });

                map.getOverlays().add(marker);
                mapController.setCenter(eventPoint);
            }
        }
    }

    private GeoPoint getGeoPointFromAddress(String address) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return new GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            }
        } catch (IOException e) {
            Log.e("CardViewFragment", "Geocoding error", e);
        }
        return null;
    }

    private void openExternalMap(String location) {
        // Prepend "Wits " to ensure navigation starts within the correct context
        String witsLocation = "Wits " + location;
        
        // google.navigation:q= starts navigation mode specifically
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(witsLocation));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        
        // Target Google Maps specifically
        mapIntent.setPackage("com.google.android.apps.maps");
        
        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            // Fallback for devices without Google Maps (searches for location)
            Uri searchUri = Uri.parse("geo:0,0?q=" + Uri.encode(witsLocation));
            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, searchUri);
            startActivity(fallbackIntent);
        }
    }

    private void closeFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (map != null) map.onDetach();
    }
}