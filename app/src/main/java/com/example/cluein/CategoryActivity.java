package com.example.cluein;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Setup RecyclerView
        //recyclerView = findViewById(R.id.rv_categories);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // 2. Prepare Data
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Music", R.drawable.music_notes_svgrepo_com));
        categoryList.add(new Category("Food", R.drawable.food_dish_svgrepo_com));
        categoryList.add(new Category("Football", R.drawable.football_svgrepo_com));
        categoryList.add(new Category("Rugby", R.drawable.sport_rugby_svgrepo_com));
        categoryList.add(new Category("Hackathon", R.drawable.coding_html_svgrepo_com));
        categoryList.add(new Category("Worship", R.drawable.gospel_choir_svgrepo_com));
        categoryList.add(new Category("Netball", R.drawable.dribbble_logo));
        categoryList.add(new Category("Nightlife", R.drawable.drinks_svgrepo_com));

        // 3. Set Adapter
        adapter = new CategoryAdapter(categoryList, category -> {
            // Basic selection logic if needed
        });
        recyclerView.setAdapter(adapter);

        // 4. Handle Next Button
        btnSkip = findViewById(R.id.btn_skip);
        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
