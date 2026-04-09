package com.example.cluein;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // 1. Get the service
        EventApiService apiService = RetrofitClient.getApiService();

// 2. Call the sports events method
        apiService.getWitsSportEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> sportsEvents = response.body();

                    // 3. This is where you send the data to your RecyclerView
                    // For example:
                    // eventAdapter = new EventAdapter(sportsEvents);
                    // recyclerView.setAdapter(eventAdapter);

                    Log.d("ClueIn", "Got " + sportsEvents.size() + " sports matches!");
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                // Handle the error (e.g., no internet)
                Log.e("ClueIn", "Failed to get sports: " + t.getMessage());
            }

        });
    }
}