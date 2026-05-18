package com.example.cluein;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationModel> notifications;
    private OnNotificationReadListener listener;

    public interface OnNotificationReadListener {
        void onMarkAsRead(NotificationModel notification);
    }

    public NotificationAdapter(List<NotificationModel> notifications, OnNotificationReadListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(notification.getTimestamp())));

        // Show/hide unread dot and mark as read button
        if (notification.isRead()) {
            holder.unreadDot.setVisibility(View.GONE);
            holder.ivMarkAsRead.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.7f); // Dim read notifications
        } else {
            holder.unreadDot.setVisibility(View.VISIBLE);
            holder.ivMarkAsRead.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);
        }

        holder.ivMarkAsRead.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMarkAsRead(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateData(List<NotificationModel> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        ImageView ivMarkAsRead;
        View unreadDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            ivMarkAsRead = itemView.findViewById(R.id.ivMarkAsRead);
            unreadDot = itemView.findViewById(R.id.unreadDot);
        }
    }
}
