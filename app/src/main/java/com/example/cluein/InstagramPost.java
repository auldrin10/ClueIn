package com.example.cluein;

import com.google.gson.annotations.SerializedName;

public class InstagramPost {
    @SerializedName("caption")
    private String caption;

    @SerializedName("displayUrl")
    private String imageUrl;

    @SerializedName("locationName")
    private String location;

    // Getters
    public String getCaption() {
        return caption;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getLocation() {
        return location != null ? location : "Wits University";
    }
}
