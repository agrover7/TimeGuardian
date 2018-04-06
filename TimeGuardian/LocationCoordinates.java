package com.example.jackbriody.TimeGuardian;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationCoordinates {

    @SerializedName("lat")
    @Expose
    private double latitude; //change later if no default should be listed
    @SerializedName("lng")
    @Expose
    private double longitude;
    @SerializedName("accuracy")
    @Expose
    private float accuracy;
    @SerializedName("created_at")
    @Expose
    private String creationTime;

    public LocationCoordinates(double latitude, double longitude, float accuracy, String creationTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.creationTime = creationTime;
    }
    /**
     * @return the accuracy of the user's location
     */
    public float getAccuracyToken() {
        return this.accuracy;
    }

    /**
     * @param accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accuracy = accuracy;
    }

    /**
     * @return the user's latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return the user's longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return the user's location
     */
    public float getAccuracy() {
        return this.accuracy;
    }


    /**
     * @return the creation time of the alarm
     */
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
}
