package com.example.cluein;

//The EventAdapter is the bridge between our data and our UI. so for example
// it takes our card(item.event xml) and append every event on our list and append it to card where
//the title,price, date is placed in the correct place as how I implemented the card(item_event.xml);
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cluein.Event;
import com.example.cluein.EventApiService;
import com.example.cluein.R;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;

    // Constructor to receive the list of events from the API
    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This appends the picture to the navy/orange item_event.xml layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // 1. Set the text details
        holder.tvTitle.setText(event.getEvent_title());
        holder.tvLocation.setText(event.getLocation());
        holder.tvDate.setText(event.getEventDate());
        holder.tvPrice.setText(String.valueOf(event.getPrice()));

        // 2. Load image with Glide (Premium Webtickets look)
        Glide.with(context)
                .load(event.getImageURL())
                .centerCrop()
                .placeholder(R.drawable.dribbble_logo)
                .into(holder.imgEvent);

        // 3. The "Campus Glow" Logic for Wits Events
        if (event.isOnCampus()) {
            holder.cardView.setStrokeWidth(4);
            holder.cardView.setStrokeColor(Color.parseColor("#FF9800")); // Neon Orange
        } else {
            holder.cardView.setStrokeWidth(0); // Standard Navy
        }

        // 4. Click listener to open the Detail Page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Event.class);
            intent.putExtra("EVENT_ID", event.getEvent_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // This class finds the IDs in the item_event.xml
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDate, tvPrice;
        ImageView imgEvent;
        MaterialCardView cardView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_event_title);
            tvLocation = itemView.findViewById(R.id.tv_event_location);
            tvDate = itemView.findViewById(R.id.tv_event_date);
            tvPrice = itemView.findViewById(R.id.tv_event_price);
            imgEvent = itemView.findViewById(R.id.img_event_poster);
            cardView = itemView.findViewById(R.id.event_card);
        }
    }
    public void updateList(List<Event> newList) {
        this.eventList = newList;
        notifyDataSetChanged(); // This triggers the UI to redraw the cards
    }
}