package com.example.jackbriody.TimeGuardian;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostAccessToken {
    @SerializedName("services")
    @Expose
    private Services services;
    @SerializedName("location.coordinates")
    @Expose
    private LocationCoordinates location;
    @SerializedName("status")
    @Expose
    private String status;

    public PostAccessToken(Services services, LocationCoordinates location) {
        this.services = services;
        this.location = location;
        this.status = status;
    }

    /**
     * @return the user's location
     */
    public LocationCoordinates getLocation() {
        return location;
    }

    /**
     * @param location
     */
    public void setLocation(LocationCoordinates location) {
        this.location = location;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = "CANCELLED";
    }
}
