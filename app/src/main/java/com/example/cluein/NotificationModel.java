package com.example.cluein;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private String id;
    private String title;
    private String message;
    private long timestamp;
    private boolean isRead;

    public NotificationModel(String id, String title, String message, long timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
