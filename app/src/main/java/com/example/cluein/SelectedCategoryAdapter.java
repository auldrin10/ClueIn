package com.example.cluein;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SelectedCategoryAdapter extends RecyclerView.Adapter<SelectedCategoryAdapter.ViewHolder> {

    private List<Category> selectedCategories;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public SelectedCategoryAdapter(List<Category> selectedCategories, OnDeleteClickListener deleteListener) {
        this.selectedCategories = selectedCategories;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_selected, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = selectedCategories.get(position);
        holder.tvName.setText(category.getName());
        holder.imgIcon.setImageResource(category.getIconResId());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedCategories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgIcon, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            imgIcon = itemView.findViewById(R.id.imgCategoryIcon);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
