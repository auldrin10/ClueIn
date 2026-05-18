package com.example.cluein;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class favouritesFragment extends Fragment {

    RecyclerView recyclerView;
    EventAdapter adapter;
    TextView emptyText;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view =
                inflater.inflate(
                        R.layout.fragment_favourites,
                        container,
                        false
                );

        recyclerView =
                view.findViewById(
                        R.id.recyclerFavorites
                );

        emptyText =
                view.findViewById(
                        R.id.tvEmptyFavorites
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        // Initialize with current local favorites for immediate visibility
        List<Event> currentFavorites = FavoriteManager.getInstance().getFavoriteEvents();
        adapter =
                new EventAdapter(
                        currentFavorites,
                        getContext(),
                        true
                );

        recyclerView.setAdapter(adapter);
        
        refreshUI();

        // Sync with database
        loadFavorites();

        return view;
    }

    private void loadFavorites() {

        FavoriteManager
                .getInstance()
                .loadFavoritesFromDatabase(
                        () -> {

                            if(getActivity()==null) return;

                            getActivity().runOnUiThread(() -> {

                                adapter.updateList(
                                        FavoriteManager
                                                .getInstance()
                                                .getFavoriteEvents()
                                );

                                refreshUI();

                            });

                        }
                );
    }

    private void refreshUI(){
        if(adapter.getItemCount() == 0){
            emptyText.setVisibility(View.VISIBLE);
        }
        else{
            emptyText.setVisibility(View.GONE);
        }
    }
}