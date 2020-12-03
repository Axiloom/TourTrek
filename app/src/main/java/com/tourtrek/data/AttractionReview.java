package com.tourtrek.data;

import com.google.firebase.firestore.DocumentReference;
import com.tourtrek.data.Attraction;
import com.tourtrek.data.User;

public class AttractionReview {
    private float stars;
    private DocumentReference user;
    private DocumentReference attraction;
    private String comment;

    public AttractionReview (){
    }


    public AttractionReview(DocumentReference user, DocumentReference attraction, int stars, String comment){
        this.user = user;
        this.attraction = attraction;
        this.stars = stars;
        this.comment = comment;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
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

    public DocumentReference getAttraction() {
        return attraction;
    }

    public void setAttraction(DocumentReference attraction) {
        this.attraction = attraction;
    }
}
