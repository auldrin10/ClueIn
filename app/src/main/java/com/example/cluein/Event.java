package com.example.cluein;
import com.google.gson.annotations.SerializedName;

public class Event {
    //SerializedName let the java code to be readable and also handling the messy API data
   //example the API might call event_pstr_url which refering to imageurl
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
   @SerializedName("price")
   private double price ;
   @SerializedName("id")
   private String Event_id;


   //This check whether the event is on campus
   @SerializedName("is_wits_event")
   private boolean isOnCampus;

   public String getEvent_id() { return Event_id;}

   public void setEvent_id(String event_id) { Event_id = event_id;}

   public String getEvent_title() {
      return event_title;
   }

   public String getImageURL() {
      return imageURL;
   }

   public void setImageURL(String imageURL) {
      this.imageURL = imageURL;
   }

   public void setEvent_title(String event_title) {
      this.event_title = event_title;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getEventDate() {
      return eventDate;
   }

   public void setEventDate(String eventDate) {
      this.eventDate = eventDate;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isOnCampus() {
      return isOnCampus;
   }

   public void setOnCampus(boolean onCampus) {
      isOnCampus = onCampus;
   }

   public double getPrice() {
      return price;
   }

   public void setPrice(double price) {
      this.price = price;
   }

   public Event(String event_title, String imageURL, String location, String eventDate, String description, double price,String Event_id,boolean isOnCampus) {
      this.event_title=event_title;
      this.eventDate=eventDate;
      this.imageURL=imageURL;
      this.location=location;
      this.description=description;
      this.isOnCampus=isOnCampus;
      this.price=price;
      this.Event_id=Event_id;
   }


}
