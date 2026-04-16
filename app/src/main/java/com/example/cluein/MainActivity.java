package com.example.cluein;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 2. Initialize Adapter with empty list
        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(eventAdapter);

        // 3. Get the service
        EventApiService apiService = RetrofitClient.getApiService();

        // 4. Call the sports events method with parameters
        apiService.getWitsSportEvents("json", true).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body());
                    eventAdapter.notifyDataSetChanged();

                    Log.d("ClueIn", "Got " + eventList.size() + " sports matches!");
                } else {
                    Log.e("ClueIn", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e("ClueIn", "Failed to get sports: " + t.getMessage());
            }
        });
    }
}
