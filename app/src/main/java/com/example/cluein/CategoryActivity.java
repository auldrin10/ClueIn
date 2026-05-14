package com.example.cluein;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.*;

public class CategoryActivity extends AppCompatActivity {

    private Button btnSkip;
    // Keep track of selected category view IDs
    private final Set<Integer> selectedCategories = new HashSet<>();

    // Define your colors (replace with your actual color resources)
    private int colorDefault;
    private int colorSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        // Initialize colors
        colorDefault = ContextCompat.getColor(this,R.color.app_on_background);
        colorSelected = Color.LTGRAY; // The color when "clicked"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSkip = findViewById(R.id.btn_skip);

        // 1. Setup toggle logic for each category card
        int[] cardIds = {
                R.id.card_music, R.id.card_food, R.id.card_football,
                R.id.card_rugby, R.id.card_hackathon, R.id.card_worship,
                R.id.card_netball, R.id.card_nightlife
        };

        for (int id : cardIds) {
            setupCategoryToggle(findViewById(id));
        }

        // 2. Handle Skip/Continue Button
        btnSkip.setOnClickListener(v -> {
            // Animation for the button
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {
                    Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                    // Optional: Pass selectedCategories to the next activity
                    startActivity(intent);
                    finish();
                });
            });
        });
    }

    /**
     * Handles the selection logic: background color change and button text update.
     */
    private void setupCategoryToggle(View view) {
        if (view == null) return;

        view.setOnClickListener(v -> {
            // Animation
            view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {

                    int viewId = view.getId();
                    if (selectedCategories.contains(viewId)) {
                        // Deselect
                        selectedCategories.remove(viewId);
                        setCardBackground(view, colorDefault);
                    } else {
                        // Select
                        selectedCategories.add(viewId);
                        setCardBackground(view, colorSelected);
                    }

                    // Update Skip button text
                    updateSkipButtonText();
                });
            });
        });
    }

    private void setCardBackground(View view, int color) {
        if (view instanceof CardView) {
            ((CardView) view).setCardBackgroundColor(color);
        } else {
            view.setBackgroundColor(color);
        }
    }

    private void updateSkipButtonText() {
        if (selectedCategories.isEmpty()) {
            btnSkip.setText("Skip");
        } else {
            btnSkip.setText("Continue");
        }
    }
}