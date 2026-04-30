package com.example.cluein;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.HistoryViewHolder> {

    private List<String> historyList;
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onHistoryItemClick(String query);
        void onDeleteHistoryClick(String query);
    }

    public SearchHistoryAdapter(List<String> historyList, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_search, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        String query = historyList.get(position);
        holder.tvHistoryText.setText(query);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onHistoryItemClick(query);
        });
        
        holder.btnDeleteHistory.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteHistoryClick(query);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<String> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryText;
        ImageView btnDeleteHistory;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryText = itemView.findViewById(R.id.tvHistoryText);
            btnDeleteHistory = itemView.findViewById(R.id.btnDeleteHistory);
        }
    }
}
