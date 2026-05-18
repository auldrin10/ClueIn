package com.example.cluein;
import com.google.gson.annotations.SerializedName;

public class Event {
    @SerializedName("name")
    private String event_title;
    @SerializedName("poster_path")
    private String imageURL;
    @SerializedName("venue_name")
    private String location;
    @SerializedName("event_date")
    private String eventDate;
    @SerializedName("description")
    private String description;

    @SerializedName(value = "event_category", alternate = {"category", "Event_category", "category_id"})
    private String category;
    
    @SerializedName("price")
    private double price;
    @SerializedName("id")
    private String Event_id;

    @SerializedName("is_wits_event")
    private boolean isOnCampus;

    private boolean isExpanded = false;

    public String getEvent_id() { return Event_id;}
    public void setEvent_id(String event_id) { Event_id = event_id;}
    public String getEvent_title() { return event_title; }
    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    public void setEvent_title(String event_title) { this.event_title = event_title; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isOnCampus() { return isOnCampus; }
    public void setOnCampus(boolean onCampus) { isOnCampus = onCampus; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { this.isExpanded = expanded; }

    public String getCategoryImageURL() {
        String cat = (category != null ? category : "").toLowerCase().trim();
        String title = (event_title != null ? event_title : "").toLowerCase();
        
        // 1. Check Category ID or Name first (Most reliable)
        
        // Music (ID 3)
        if (cat.equals("3") || cat.contains("music") || title.contains("music") || title.contains("concert") || title.contains("dj"))
            return "https://images.unsplash.com/photo-1514525253361-bee8a187499d?q=80&w=1000&auto=format&fit=crop";
            
        // Sports (ID 2)
        if (cat.equals("2") || cat.contains("sport") || title.contains("sport") || title.contains("soccer") || title.contains("rugby") || title.contains("stadium"))
            return "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?q=80&w=1000&auto=format&fit=crop";
            
        // Food (ID 4)
        if (cat.equals("4") || cat.contains("food") || title.contains("food") || title.contains("eat") || title.contains("drink") || title.contains("cafe") || title.contains("lunch"))
            return "https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=1000&auto=format&fit=crop";
            
        // Nightlife (ID 5)
        if (cat.equals("5") || cat.contains("nightlife") || title.contains("party") || title.contains("club") || title.contains("night") || title.contains("dance"))
            return "https://images.unsplash.com/photo-1566737236500-c8ac43014a67?q=80&w=1000&auto=format&fit=crop";
            
        // Academics (ID 6)
        if (cat.equals("6") || cat.contains("academic") || title.contains("study") || title.contains("lecture") || title.contains("exam") || title.contains("workshop") || title.contains("seminar"))
            return "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?q=80&w=1000&auto=format&fit=crop";
            
        // Social (ID 1)
        if (cat.equals("1") || cat.contains("social") || title.contains("social") || title.contains("meet") || title.contains("hangout") || title.contains("gathering"))
            return "https://images.unsplash.com/photo-1511632765486-a01980e01a18?q=80&w=1000&auto=format&fit=crop";
            
        // Tech / Hackathon
        if (title.contains("hack") || title.contains("tech") || title.contains("code") || title.contains("computer") || title.contains("programming"))
            return "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?q=80&w=1000&auto=format&fit=crop";

        // Default Fallback
        return "https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?q=80&w=1000&auto=format&fit=crop";
    }

    public Event(String event_title, String imageURL, String location, String eventDate, String description, double price, String Event_id, boolean isOnCampus, String category) {
        this.event_title = event_title;
        this.imageURL = imageURL;
        this.location = location;
        this.eventDate = eventDate;
        this.description = description;
        this.price = price;
        this.Event_id = Event_id;
        this.isOnCampus = isOnCampus;
        this.category = category;
        this.isExpanded = false;
    }
}