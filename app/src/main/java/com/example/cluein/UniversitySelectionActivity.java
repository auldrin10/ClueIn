package com.example.cluein;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class UniversitySelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_selection);

        CardView cardWits = findViewById(R.id.cardWits);
        CardView cardUJ = findViewById(R.id.cardUJ);

        cardWits.setOnClickListener(v -> selectUniversity("Wits"));
        cardUJ.setOnClickListener(v -> selectUniversity("UJ"));
    }

    private void selectUniversity(String university) {
        // Save selection to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("SelectedUniversity", university).apply();

        // Navigate to MainActivity
        Intent intent = new Intent(UniversitySelectionActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}