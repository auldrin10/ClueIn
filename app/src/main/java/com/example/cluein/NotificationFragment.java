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

public class NotificationFragment extends Fragment {
    private RecyclerView rvNotifications;
    private TextView tvNoNotifications;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        rvNotifications = view.findViewById(R.id.rvNotifications);
        tvNoNotifications = view.findViewById(R.id.tvNoNotifications);
        
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        
        loadNotifications();
        
        return view;
    }

    private void loadNotifications() {
        if (getContext() == null) return;
        List<NotificationModel> notifications = NotificationStore.getNotifications(getContext());
        if (notifications.isEmpty()) {
            tvNoNotifications.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            tvNoNotifications.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
            adapter = new NotificationAdapter(notifications);
            rvNotifications.setAdapter(adapter);
        }
    }
}
