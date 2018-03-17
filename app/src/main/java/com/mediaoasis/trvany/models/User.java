package com.mediaoasis.trvany.models;

/**
 * Created by Nasr on 12/9/2016.
 */

public class User {
    private String UserID, username, email, Phone, Country, Language;
    private String PhotoURI;
    private int AllowNotifications;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURI() {
        return PhotoURI;
    }

    public void setPhotoURI(String photoURI) {
        PhotoURI = photoURI;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public int getAllowNotifications() {
        return AllowNotifications;
    }

    public void setAllowNotifications(int allowNotifications) {
        AllowNotifications = allowNotifications;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }
}
