package com.example.cluein;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String name;
    // List of authorized emails moved here for UI control
    private final List<String> authorizedEmails = Arrays.asList(
            "3030015@students.wits.ac.za", 
            "3002003@students.wits.ac.za", 
            "3032986@students.wits.ac.za"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        
        // --- AUTHORIZATION CHECK FOR UI ---
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            userEmail = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("USER_EMAIL", null);
        }

        Menu menu = bottomNavigationView.getMenu();
        MenuItem addEventItem = menu.findItem(R.id.add_event);
        
        if (addEventItem != null) {
            // Only show the plus sign if the user is authorized
            if (userEmail != null && authorizedEmails.contains(userEmail.toLowerCase())) {
                addEventItem.setVisible(true);
            } else {
                addEventItem.setVisible(false); // Hides the item, leaving only 4 items
            }
        }
        // ----------------------------------

        // Load default fragment
        if (savedInstanceState == null) {
            replaceFragment(new MainFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_feed) {
                replaceFragment(new MainFragment());
                return true;
            } else if (itemId == R.id.nav_search) {
                replaceFragment(new SearchBarFragment());
                return true;
            } else if (itemId == R.id.add_event) {
                replaceFragment(new AddEvent());
                return true;
            } else if (itemId == R.id.nav_favorites) {
                replaceFragment(new favouritesFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FargmentContainer, fragment);
        fragmentTransaction.commit();
    }
}