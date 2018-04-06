package com.example.jackbriody.TimeGuardian;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Services {
    @SerializedName("police")
    @Expose
    private boolean police = true; //change later if no default should be listed
    @SerializedName("fire")
    @Expose
    private boolean fire = false;
    @SerializedName("medical")
    @Expose
    private boolean medical = false;

    public Services(boolean police, boolean fire, boolean medical) {
        this.police = police;
        this.fire = fire;
        this.medical = medical;
    }

    /**
     * @return police
     */
    public boolean isPolice() {
        return police;
    }

    /**
     * @return fire
     */
    public boolean isFire() {
        return fire;
    }

    /**
     * @return medical
     */
    public boolean isMedical() {
        return medical;
    }
}
