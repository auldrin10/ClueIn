package com.example.cluein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CategoryActivity extends AppCompatActivity {

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

        // 1. Setup animations for each category card
        setupCategoryAnimation(findViewById(R.id.card_music), null);
        setupCategoryAnimation(findViewById(R.id.card_food), null);
        setupCategoryAnimation(findViewById(R.id.card_football), null);
        setupCategoryAnimation(findViewById(R.id.card_rugby), null);
        setupCategoryAnimation(findViewById(R.id.card_hackathon), null);
        setupCategoryAnimation(findViewById(R.id.card_worship), null);
        setupCategoryAnimation(findViewById(R.id.card_netball), null);
        setupCategoryAnimation(findViewById(R.id.card_nightlife), null);

        // 2. Handle Skip Button with animation and navigation to MainActivity
        btnSkip = findViewById(R.id.btn_skip);
        setupCategoryAnimation(btnSkip, () -> {
            Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Applies a scale micro-interaction animation to the given view.
     * @param view The view to animate.
     * @param onAction The action to perform after the animation sequence finishes.
     */
    private void setupCategoryAnimation(View view, Runnable onAction) {
        if (view == null) return;

        view.setOnClickListener(v -> {
            // Animation: Scale down to 0.9f and then back to 1f
            view.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() ->
                            view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .withEndAction(() -> {
                                        if (onAction != null) {
                                            onAction.run();
                                        }
                                    })
                    );
        });
    }
}