package com.tourtrek.data;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {

    private String username;
    private String email;
    private String profileImageURI;
    private List<DocumentReference> tours;
    private List<DocumentReference> contacts;
    private HashMap<String, Object> settings;
    private List<DocumentReference> friends;

    /**
     * Empty constructor needed for firestore
     */
    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.tours = new ArrayList<>();
        this.friends= new ArrayList<>();
    }

    /**
     * Getter for settings.
     *
     * @return current settings
     */
    public HashMap<String, Object> getSettings() {
        return settings;
    }

    /**
     * Setter for settings.
     *
     * @param settings settings to set
     */
    public void setSettings(HashMap<String, Object> settings) {
        this.settings = settings;
    }

    /**
     * Getter for username
     *
     * @return current username
     */
    public String getUsername() {
        if (this.username == null){
            this.username = "";
        }
        return this.username;
    }

    /**
     * Setter for username
     *
     * @param username username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for email
     *
     * @return current email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for email
     *
     * @param email email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for profileImageURI
     *
     * @return current profileImageURI
     */
    public String getProfileImageURI() {
        return profileImageURI;
    }

    /**
     * Setter for profileImageURI
     *
     * @param profileImageURI profileImageURI to set
     */
    public void setProfileImageURI(String profileImageURI) {
        this.profileImageURI = profileImageURI;
    }

    /**
     * Getter for tours
     *
     * @return current tours
     */
    public List<DocumentReference> getTours() {
        if (this.tours == null){
            this.tours = new ArrayList<>();
        }
        return tours;
    }

    /**
     * Setter for tours
     *
     * @param tours tours to set
     */
    public void setTours(List<DocumentReference> tours) {
        this.tours = tours;
    }

    public List<DocumentReference> getContacts() {
        if (this.contacts == null){
            this.contacts = new ArrayList<>();
        }
        return this.contacts;
    }

    public void setContacts(List<DocumentReference> contacts) {
        this.contacts = contacts;
    }

    /**
     * Getter for friends
     *
     * @return current friends
     */
    public List<DocumentReference> getFriends() {
        if (this.friends == null){
            this.friends = new ArrayList<>();
        }
        return friends;
    }

    /**
     * Setter for friends
     *
     * @param friends friends to set
     */
    public void setFriends(List<DocumentReference> friends) {
        this.friends = friends;
    }




    /**
     * Add tour reference to the user
     *
     * @param tourDocument tourDocument to add
     */
    public void addTourToTours(DocumentReference tourDocument){
        if (this.tours == null) {
            this.tours = new ArrayList<>();
        }

        this.tours.add(tourDocument);
    }

    /**
     * Add friend reference to the user
     *
     * @param friendDocument friendDocument to add
     */
    public void addFriendToFriends(DocumentReference friendDocument){
        if (this.friends == null) {
            this.friends = new ArrayList<>();
        }

        this.friends.add(friendDocument);
    }


}
