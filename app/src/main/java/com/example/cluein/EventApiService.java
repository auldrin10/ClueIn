package com.example.cluein;
import java.util.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//Retrofit is a translator and a Courier, it helps to convert Data from APIs(JSON) to our app in simple terms.
//A interface class acts as a contract with our class the outside  world(servers).
// for example our interface will be the menu that we want to choose from(events) and the outside world will be the APIs(kitchen)
public interface EventApiService {
    @GET ("events")
    Call<List<Event>> getAllEvents();
    //So this get the entire lists of events and this is primarily for events that are not inside on Campus;
    @GET("events")
    Call<List<Event>> getEventsByLocation(@Query("location")String campusName);
    //So this let you search events based on the category it can be maybe academics or social(Fresher's party)
    @GET("events")
    Call<List<Event>> getEventsByCategory(@Query("category") String categoryName);
    //So I added this so that we can filter events also based on prices so that student can able to attend events that meet their budget as we know we are broke!
    @GET("events")
    Call<List<Event>> getEventsByMaxPrice(@Query("max_price") double maxPrice);
}
