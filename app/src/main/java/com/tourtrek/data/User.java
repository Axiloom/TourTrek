package com.tourtrek.data;

import com.google.firebase.firestore.DocumentReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User{

    private String username;
    private String email;
    private String profileImageURI;
    private List<DocumentReference> tours;
    private List<DocumentReference> contacts;
    private DocumentReference currentTour; // to be updated when the user selects a tour
    private Tour currentTourObj; // update when the user selects a tour

    /**
     * Empty constructor needed for firestore
     */
    public User() {
//        this.username = "";
//        this.email = "";
//        this.tours = new ArrayList<>();
//        this.profileImageURI = "";
//        this.contacts = new ArrayList<>();
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
//        this.tours = new ArrayList<>();
//        this.profileImageURI = "";
//        this.contacts = new ArrayList<>();
    }

    /**
     * Getter for username
     *
     * @return current username
     */
    public String getUsername() {
        if (this.username != null){
            return this.username;
        }
        return "";
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
        if (this.email != null){
            return this.email;
        }
        return "";
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
        if (this.profileImageURI != null){
            return this.profileImageURI;
        }
        return "";
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
        return this.tours;
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

    public DocumentReference getCurrentTour() {
        return currentTour;
    }

    public void setCurrentTour(DocumentReference currentTour) {
        this.currentTour = currentTour;
    }

    public Tour getCurrentTourObj() {
        return currentTourObj;
    }

    public void setCurrentTourObj(Tour currentTourObj) {
        this.currentTourObj = currentTourObj;
    }
}
