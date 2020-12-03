package com.tourtrek.data;

import com.google.firebase.firestore.DocumentReference;
import com.tourtrek.data.Tour;
import com.tourtrek.data.User;

public class TourReview {
    private float stars;
    private DocumentReference user;
    private DocumentReference tour;
    private String comment;
    private  String userUID;
    private String tourUID;

    public TourReview (){
    }

    public TourReview(DocumentReference user, DocumentReference tour, int stars, String comment){
        this.user = user;
        this.tour = tour;
        this.stars = stars;
        this.comment = comment;
    }

    public float getStars() {
        return stars;
    }

    public String getComment() {
        return comment;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DocumentReference getUser() {
        return user;
    }

    public void setUser(DocumentReference user) {
        this.user = user;
    }

    public DocumentReference getTour() {
        return tour;
    }

    public void setTour(DocumentReference tour) {
        this.tour = tour;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getTourUID() {
        return tourUID;
    }

    public void setTourUID(String tourUID) {
        this.tourUID = tourUID;
    }
}
