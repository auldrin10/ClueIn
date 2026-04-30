package com.example.cluein;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;
    private boolean isFavoriteView;

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

        holder.tvDescription.setVisibility(View.GONE);
        holder.btnCloseExpand.setVisibility(View.GONE);

        if (isFavoriteView) {
            holder.btnFavorite.setVisibility(View.GONE);
            holder.btnRemove.setVisibility(View.VISIBLE);
        } else {
            holder.btnFavorite.setVisibility(View.VISIBLE);
            holder.btnRemove.setVisibility(View.GONE);
            if (FavoriteManager.getInstance().isFavorite(event)) {
                holder.btnFavorite.setColorFilter(Color.RED);
            } else {
                holder.btnFavorite.clearColorFilter();
            }
        }

        holder.itemView.setOnClickListener(v -> {
            CardViewFragment fragment = CardViewFragment.newInstance(event);
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.btnFavorite.setOnClickListener(v -> {
            FavoriteManager.getInstance().addFavorite(event);
            holder.btnFavorite.setColorFilter(Color.RED);
            
            // Trigger scheduling logic
            scheduleAllReminders(event);
            
            Toast.makeText(context, "Added to Favorites!", Toast.LENGTH_SHORT).show();
        });

        holder.btnRemove.setOnClickListener(v -> {
            FavoriteManager.getInstance().removeFavorite(event);
            eventList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, eventList.size());
            Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
        });
    }

    private void scheduleAllReminders(Event event) {
        // Using 'd' instead of 'dd' to handle single-digit days like "1 - May" or "01 - May"
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        try {
            Date eventDate = sdf.parse(event.getEventDate());
            if (eventDate == null) return;

            long diffInMillis = eventDate.getTime() - System.currentTimeMillis();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            // 1. Immediate Notification
            sendImmediateNotification(event, "Event starts in " + daysLeft + " days");

            // 2. 48 Hour Reminder
            scheduleAlarm(event, eventDate.getTime() - TimeUnit.HOURS.toMillis(48), "Event starts in 2 days", 48);

            // 3. 24 Hour Reminder
            scheduleAlarm(event, eventDate.getTime() - TimeUnit.HOURS.toMillis(24), "Event starts in 1 day", 24);

        } catch (ParseException e) {
            Log.e("EventAdapter", "Error parsing date: " + event.getEventDate() + " for event: " + event.getEvent_title());
        }
    }

    private void sendImmediateNotification(Event event, String message) {
        Intent intent = new Intent(context, EventReminderReceiver.class);
        intent.putExtra("eventId", event.getEvent_id());
        intent.putExtra("eventTitle", event.getEvent_title());
        intent.putExtra("message", message);
        context.sendBroadcast(intent);
    }

    private void scheduleAlarm(Event event, long triggerAtMillis, String message, int requestCodeOffset) {
        if (triggerAtMillis <= System.currentTimeMillis()) return;

        Intent intent = new Intent(context, EventReminderReceiver.class);
        intent.putExtra("eventId", event.getEvent_id());
        intent.putExtra("eventTitle", event.getEvent_title());
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                event.getEvent_id().hashCode() + requestCodeOffset,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // Fix: Check for exact alarm permission on Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                } else {
                    // Fallback to non-exact alarm if permission is missing
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDate, tvPrice, tvDescription;
        ImageView imgEvent, btnFavorite, btnRemove, btnCloseExpand;
        MaterialCardView cardView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_event_title);
            tvLocation = itemView.findViewById(R.id.tv_event_location);
            tvDate = itemView.findViewById(R.id.tv_event_date);
            tvPrice = itemView.findViewById(R.id.tv_event_price);
            tvDescription = itemView.findViewById(R.id.tv_event_description);
            imgEvent = itemView.findViewById(R.id.img_event_poster);
            cardView = itemView.findViewById(R.id.event_card);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnCloseExpand = itemView.findViewById(R.id.btn_close_expand);
        }
    }
}
