package com.example.cluein;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {
    // Replace this with your actual API URL or your local server address
    private static final String BASE_URL = "https://api.apify.com/";
    private static Retrofit retrofit = null;

    public static EventApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // This converts the JSON data into Java Event objects automatically
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        // This links your interface to the actual network connection
        return retrofit.create(EventApiService.class);
    }
}
