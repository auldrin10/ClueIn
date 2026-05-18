package com.example.cluein;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateCategory extends Fragment {

    OkHttpClient client = new OkHttpClient();

    String getUrl = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/filterpreference.php";
    String postUrl = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/preference.php";
    String removeUrl = "https://wmc.ms.wits.ac.za/students/sgroup2672/events/removepreference.php";

    RecyclerView rvSelectedCategories;
    SelectedCategoryAdapter adapter;
    List<Category> selectedCategoriesList = new ArrayList<>();

    // Standardized categories to match CategoryActivity and database expectations
    private final String[] allCategories = {
            "Music Concerts",
            "Sports",
            "Society",
            "Academics",
            "Financial Literacy",
            "Career Expo"
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_update__category,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        rvSelectedCategories = view.findViewById(R.id.rvSelectedCategories);
        rvSelectedCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new SelectedCategoryAdapter(selectedCategoriesList, position -> {
            String categoryName = selectedCategoriesList.get(position).getName();
            removePreference(categoryName);
        });
        
        rvSelectedCategories.setAdapter(adapter);

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddCategory);
        fabAdd.setOnClickListener(v -> showCategorySelectionDialog());

        loadPreferredCategories();
    }

    private void showCategorySelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a Category");
        builder.setItems(allCategories, (dialog, which) -> {
            String selected = allCategories[which];
            postPreference(selected);
        });
        builder.show();
    }

    private void postPreference(String categoryName) {
        if (LoginActivity.user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = LoginActivity.user.getUserID();

        // Using .trim() to ensure no hidden spaces cause database mismatch
        RequestBody body = new FormBody.Builder()
                .add("user_id", userId)
                .add("category", categoryName.trim())
                .build();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Network error: Failed to add " + categoryName, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (Response res = response) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (res.isSuccessful()) {
                                Toast.makeText(getContext(), categoryName + " added successfully", Toast.LENGTH_SHORT).show();
                                loadPreferredCategories(); 
                            } else {
                                Toast.makeText(getContext(), "Server error: " + res.code(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void removePreference(String categoryName) {
        if (LoginActivity.user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = LoginActivity.user.getUserID();

        RequestBody body = new FormBody.Builder()
                .add("user_id", userId)
                .add("category", categoryName.trim())
                .build();

        Request request = new Request.Builder()
                .url(removeUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to remove " + categoryName, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (Response res = response) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (res.isSuccessful()) {
                                Toast.makeText(getContext(), categoryName + " removed successfully", Toast.LENGTH_SHORT).show();
                                loadPreferredCategories(); 
                            } else {
                                Toast.makeText(getContext(), "Error: " + res.code(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void loadPreferredCategories() {
        if (LoginActivity.user == null) return;

        String userId = LoginActivity.user.getUserID();

        Request request = new Request.Builder()
                .url(getUrl + "?user_id=" + userId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String res = response.body().string();
                    JSONObject obj = new JSONObject(res);
                    JSONArray arr = obj.optJSONArray("categories");

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            selectedCategoriesList.clear();
                            if (arr != null) {
                                for (int i = 0; i < arr.length(); i++) {
                                    try {
                                        JSONObject item = arr.getJSONObject(i);
                                        String categoryName = item.optString("category", item.optString("category_id", ""));
                                        if (!categoryName.isEmpty()) {
                                            selectedCategoriesList.add(new Category(categoryName, getCategoryIcon(categoryName)));
                                        }
                                    } catch (Exception e) {
                                        Log.e("PARSE_ITEM_ERROR", e.toString());
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        });
                    }
                } catch (Exception e) {
                    Log.e("PARSE_ERROR", e.toString());
                } finally {
                    response.close();
                }
            }
        });
    }

    private int getCategoryIcon(String categoryName) {
        if (categoryName == null) return R.drawable.baseline_event_24;
        
        // Using case-insensitive comparisons to handle database variations
        String name = categoryName.toLowerCase().trim();
        
        if (name.contains("music")) return R.drawable.music_notes_svgrepo_com;
        if (name.contains("sport")) return R.drawable.football_svgrepo_com;
        if (name.contains("society")) return R.drawable.socials;
        if (name.contains("academic")) return R.drawable.academics;
        if (name.contains("financial")) return R.drawable.finacial_literacy;
        if (name.contains("career")) return R.drawable.career_expo;
        
        return R.drawable.baseline_event_24;
    }
}
