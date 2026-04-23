package com.example.cluein;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;
    private boolean isFavoriteView; // Flag to check if we are in the Favorites section

    public EventAdapter(List<Event> eventList, Context context, boolean isFavoriteView) {
        this.eventList = eventList;
        this.context = context;
        this.isFavoriteView = isFavoriteView;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvTitle.setText(event.getEvent_title());
        holder.tvLocation.setText(event.getLocation());
        holder.tvDate.setText(event.getEventDate());
        holder.tvPrice.setText("R" + String.valueOf(event.getPrice()));

        Glide.with(context)
                .load(event.getImageURL())
                .centerCrop()
                .placeholder(R.drawable.dribbble_logo)
                .into(holder.imgEvent);

        if (event.isOnCampus()) {
            holder.cardView.setStrokeWidth(4);
            holder.cardView.setStrokeColor(Color.parseColor("#FF9800"));
        } else {
            holder.cardView.setStrokeWidth(0);
        }

        // --- Logic for Favorite (Love) and Remove (X) buttons ---
        if (isFavoriteView) {
            holder.btnFavorite.setVisibility(View.GONE); // Hide love icon in Favorites
            holder.btnRemove.setVisibility(View.VISIBLE); // Show X icon in Favorites
        } else {
            holder.btnFavorite.setVisibility(View.VISIBLE); // Show love icon in Main Feed
            holder.btnRemove.setVisibility(View.GONE); // Hide X icon in Main Feed
            
            // Check if already favorite to update icon color/tint
            if (FavoriteManager.getInstance().isFavorite(event)) {
                holder.btnFavorite.setColorFilter(Color.RED);
            } else {
                holder.btnFavorite.clearColorFilter();
            }
        }

        holder.btnFavorite.setOnClickListener(v -> {
            FavoriteManager.getInstance().addFavorite(event);
            holder.btnFavorite.setColorFilter(Color.RED); // Visually confirm it's added
            Toast.makeText(context, "Added to Favorites!", Toast.LENGTH_SHORT).show();
        });

        holder.btnRemove.setOnClickListener(v -> {
            FavoriteManager.getInstance().removeFavorite(event);
            eventList.remove(position); // Remove from local list
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, eventList.size());
            Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {
            // Intent to detail page logic...
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDate, tvPrice;
        ImageView imgEvent, btnFavorite, btnRemove;
        MaterialCardView cardView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_event_title);
            tvLocation = itemView.findViewById(R.id.tv_event_location);
            tvDate = itemView.findViewById(R.id.tv_event_date);
            tvPrice = itemView.findViewById(R.id.tv_event_price);
            imgEvent = itemView.findViewById(R.id.img_event_poster);
            cardView = itemView.findViewById(R.id.event_card);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}