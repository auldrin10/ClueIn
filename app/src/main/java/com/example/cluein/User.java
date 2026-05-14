package com.example.cluein;

public class User {
    String first_name;
    String last_name;
    String email;
    String password;
    String user_id;
    public User(String first_name, String last_name,String email, String password ,String user_id) {

        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.user_id = user_id;

    }

    public String getFirst_name() {
        return first_name;
    }
    public String getLast_name() {
        return last_name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getUserID() {
        return user_id;
    }
}
