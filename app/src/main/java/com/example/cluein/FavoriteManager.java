package com.example.cluein;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage favorites across the app.
 */
public class FavoriteManager {
    private static FavoriteManager instance;
    private List<Event> favoriteEvents;

    private FavoriteManager() {
        favoriteEvents = new ArrayList<>();
    }

    public static synchronized FavoriteManager getInstance() {
        if (instance == null) {
            instance = new FavoriteManager();
        }
        return instance;
    }

    public void addFavorite(Event event) {
        if (!isFavorite(event)) {
            favoriteEvents.add(event);
        }
    }

    public void removeFavorite(Event event) {
        favoriteEvents.removeIf(e -> e.getEvent_id().equals(event.getEvent_id()));
    }

    public boolean isFavorite(Event event) {
        for (Event e : favoriteEvents) {
            if (e.getEvent_id().equals(event.getEvent_id())) {
                return true;
            }
        }
        return false;
    }

    public List<Event> getFavoriteEvents() {
        return new ArrayList<>(favoriteEvents);
    }
}