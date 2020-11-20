package com.tourtrek.data;

import android.location.Address;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Attraction {

    private List<String> reviews;
    private String location;
    private float cost;
    private String name;
    private String description;
    private String attractionUID;
    private Date startDate;
    private String startTime;
    private Date endDate;
    private String endTime;
    private String address;
    private String coverImageURI;

    /**
     * Empty constructor needed for Firestore
     */
    public Attraction(){}

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCoverImageURI() {
        return coverImageURI;
    }

    public void setCoverImageURI(String coverImageURI) {
        this.coverImageURI = coverImageURI;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get attraction cost
     *
     * @return cost of this attraction
     */
    public float getCost() {
        return this.cost;
    }

    /**
     * Set attraction cost
     *
     * @param cost
     */
    public void setCost(float cost) {
        this.cost = cost;
    }

    /**
     * Getter for reviews
     *
     * @return current reviews
     */
    public List<String> getReviews() {
        if (this.reviews == null){
            return new ArrayList<String>();
        }
        return this.reviews;
    }

    /**
     * Setter for reviews
     *
     * @param reviews list of reviews for the tour
     */
    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public String getName() {
        if (this.name == null){
            this.name = "";
        }
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAttractionUID() {
        return attractionUID;
    }

    public void setAttractionUID(String attractionUID) {
        this.attractionUID = attractionUID;
    }

    public String getLocation() {
        if (location == null)
            location = "";
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String retrieveStartDateAsString() {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        return formatter.format(startDate);
    }

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

    public void setEndDateFromString(String endDateString) throws ParseException {

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        this.endDate = formatter.parse(endDateString);
    }

    public String retrieveEndDateAsString() {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        return formatter.format(endDate);
    }
}
