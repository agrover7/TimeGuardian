package com.example.jackbriody.TimeGuardian;

public class UpdatedLocationResponse {

    private LocationCoordinates location;

    public UpdatedLocationResponse(LocationCoordinates location) {
        this.location = location;
    }
    /**
     * @return the user's new location
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

}


