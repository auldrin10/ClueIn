package com.example.cluein;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EventApiService {

    // General events
    @GET("events")
    Call<List<Event>> getAllEvents();

    // Search by Campus (Fixed syntax)
    @GET("events")
    Call<List<Event>> getEventsByLocation(@Query("location") String campusName);

    // Search by Category (Fixed syntax)
    @GET("events")
    Call<List<Event>> getEventsByCategory(@Query("category") String categoryName);

    // Filter by Price (Fixed syntax)
    @GET("events")
    Call<List<Event>> getEventsByMaxPrice(@Query("max_price") double maxPrice);

    // --- LIVE SPORTS DATA (Apify) ---
    // This is for the live data we just set up for Wits Sport
    @GET("v2/datasets/sLlOSEXduYaLXrcde/items?format=json&clean=true")
    Call<List<Event>> getWitsSportEvents();
}