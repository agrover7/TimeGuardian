package com.example.jackbriody.TimeGuardian;

public class AlarmResponse {

    private Services services;
    private LocationCoordinates location;
    private String id;
    private static String status;
    private String time;

    public AlarmResponse(Services services, LocationCoordinates location, String id, String status, String time) {
        this.services = services;
        this.location = location;
        this.id = id;
        this.status = status;
        this.time = time;
    }

    /**
     * @return services
     */
    public Services getServices() {
        return services;
    }

    /**
     * @param services
     */
    public void setServices(Services services) {
        this.services = services;
    }

    /**
     * @return location of user
     */
    public LocationCoordinates getLocation() {
        return location;
    }

    /**
     * @return the emergency call's id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the status of the emergency call
     */
    public static String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the time of the call
     */
    public String getTime() {
        return time;
    }
}
