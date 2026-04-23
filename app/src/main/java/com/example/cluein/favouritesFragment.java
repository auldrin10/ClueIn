package com.example.cluein;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class favouritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private TextView emptyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fixed: changed fragment_favourites_list to fragment_favourites
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerFavorites);
        emptyText = view.findViewById(R.id.tvEmptyFavorites);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Load events from the FavoriteManager
        List<Event> favList = FavoriteManager.getInstance().getFavoriteEvents();
        
        if (favList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }

        // Passing 'true' because this is the Favorites section
        adapter = new EventAdapter(favList, getContext(), true);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
