package com.example.cluein;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener; // The "Messenger"

    // It acts as a bridge so the Activity knows when an icon is clicked.
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // 2. CONSTRUCTOR: Now takes the list AND the listener
    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvName.setText(category.getName());
        holder.imgIcon.setImageResource(category.getIconResId());

        // 3. THE CLICK LOGIC: When a user taps "Coding", it calls the listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgIcon;

        public CategoryViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_header);
            imgIcon = view.findViewById(R.id.img_category_icon);
        }
    }
}