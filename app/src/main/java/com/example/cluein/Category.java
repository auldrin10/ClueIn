package com.example.cluein;

public class Category {
    private String name;
    private String teh;
    private int iconResId; // To store R.drawable.ic_music, etc.

    public Category(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
}
