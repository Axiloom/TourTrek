package com.tourtrek.data;

import com.google.firebase.firestore.DocumentReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tour{

    private String name; // constructor
    private Date startDate; // constructor
    private Date endDate; // constructor
    private String location; // constructor
    private Long length;
    private float cost; // constructor
    private Boolean notifications; // constructor
    private List<String> reviews; // constructor
    private String description; // constructor
    private Boolean publiclyAvailable; // constructor
    private List<DocumentReference> attractions;
    private String coverImageURI; // constructor
    private String tourUID; // constructor
    private double totalRating;
    private double rating;

    /**
     * Empty constructor needed for Firestore
     */
    public Tour() {
        this.reviews = new ArrayList<>();
        this.totalRating = 0;
        this.rating = 0;

    }

    /**
     * Complete constructor
     */
    public Tour(String name, Date startDate, Date endDate, String location, float cost, Boolean notifications,
                List<String> reviews, String description, Boolean publiclyAvailable, List<DocumentReference> attractions,
                String coverImageURI, String tourUID){

        // populate all fields except for length and attractions
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.cost = cost;
        this.notifications = notifications;
        this.reviews = reviews;
        this.description = description;
        this.publiclyAvailable = publiclyAvailable;
        this.attractions = attractions;
        this.coverImageURI = coverImageURI;
        this.tourUID = tourUID;

    }

    /**
     * Getter for name
     *
     * @return current name
     */
    public String getName() {
        if (this.name == null){
            this.name = "";
        }
        return this.name;
    }

    /**
     * Setter for name
     *
     * @param name name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for coverImageURI
     *
     * @return current coverImageURI
     */
    public String getCoverImageURI() {
        if (this.coverImageURI == null){
            this.coverImageURI = "";
        }
        return this.coverImageURI;
    }

    /**
     * Setter for coverImageURI
     *
     * @param coverImageURI coverImageURI to set
     */
    public void setCoverImageURI(String coverImageURI) {
        this.coverImageURI = coverImageURI;
    }

    /**
     * Getter for attractions
     *
     * @return current attractions
     */
    public List<DocumentReference> getAttractions() {
        if (this.attractions == null){
            this.attractions = new ArrayList<>();
        }
        return this.attractions;
    }

    /**
     * Setter for attractions
     *
     * @param attractions attractions to set
     */
    public void setAttractions(List<DocumentReference> attractions) {
        this.attractions = attractions;
    }

    /**
     * Adds a new attraction to this tour
     *
     * @param attraction attraction to add
     */
    public void addAttraction(DocumentReference attraction) {
        if (attractions == null) {
            attractions = new ArrayList<>();
        }
        attractions.add(attraction);
    }

    /**
     * Getter for startDate
     *
     * @return current startDate
     */
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * Setter for startDate
     *
     * @param startDate startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Retrieves the current start date as a string
     *
     * @return current start date
     */
    public String retrieveStartDateAsString() {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        return formatter.format(startDate);
    }

    /**
     * Setter for startDate
     *
     * @param startDateString startDate to set as a string
     * @throws ParseException thrown if startDateString cannot be converted to Date
     */
    public void setStartDateFromString(String startDateString) throws ParseException {

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        this.startDate = formatter.parse(startDateString);
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String retrieveEndDateAsString() {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        return formatter.format(endDate);
    }

    public void setEndDateFromString(String endDateString) throws ParseException {

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        this.endDate = formatter.parse(endDateString);
    }

    /**
     * Getter for notifications
     *
     * @return current notifications
     */
    public Boolean getNotifications() {
        if (notifications == null) {
            notifications = false;
        }
        return notifications;
    }

    /**
     * Setter for notifications
     *
     * @param notifications notifications to set
     */
    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }

    /**
     * Getter for location
     *
     * @return current location
     */
    public String getLocation() {
        if (this.location == null){
            this.location = "";
        }
        return this.location;
    }

    /**
     * Setter for location
     *
     * @param location location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter for length
     *
     * @return current length
     */
    public Long getLength() {
        return length;
    }

    /**
     * Setter for length
     *
     * @param length length to set
     */
    public void setLength(Long length) {
        this.length = length;
    }

    public float getCost() {
        return this.cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public String getDescription() {
        if (this.description == null){
            this.description = "";
        }
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for publiclyAvailable
     *
     * @return current publiclyAvailable
     */
    public Boolean isPubliclyAvailable() {
        if (publiclyAvailable == null) {
            publiclyAvailable = false;
        }

        return publiclyAvailable;
    }

    /**
     * Setter for publiclyAvailable
     *
     * @param publiclyAvailable publiclyAvailable to set
     */
    public void setPubliclyAvailable(Boolean publiclyAvailable) {
        this.publiclyAvailable = publiclyAvailable;
    }

    public String getTourUID() {
        return this.tourUID;
    }

    public void setTourUID(String tourUID) {
        this.tourUID = tourUID;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void addUser (String user) {
        this.reviews.add(user);
    }
}
