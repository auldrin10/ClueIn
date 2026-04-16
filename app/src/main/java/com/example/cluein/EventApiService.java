package com.example.cluein;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EventApiService {

    // General events
    @GET("events")
    Call<List<Event>> getAllEvents();

    // Search by Campus
    @GET("events")
    Call<List<Event>> getEventsByLocation(@Query("location") String campusName);

    // Search by Category
    @GET("events")
    Call<List<Event>> getEventsByCategory(@Query("category") String categoryName);

    // Filter by Price
    @GET("events")
    Call<List<Event>> getEventsByMaxPrice(@Query("max_price") double maxPrice);

    // --- LIVE SPORTS DATA (Apify) ---
    @GET("datasets/sLlOSEXduYaLXrcde/items")
    Call<List<Event>> getWitsSportEvents(
            @Query("format") String format,
            @Query("clean") boolean clean
    );
}
