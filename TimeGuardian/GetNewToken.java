package com.example.jackbriody.TimeGuardian;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetNewToken {
    @SerializedName("grant_type")
    @Expose
    private String grantType;
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("client_secret")
    @Expose
    private String clientSecret;
    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;

    /**
     * @return the type of grant
     */
    public String getGrantType() {
        return grantType;
    }

    /**
     * @param grantType
     */
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    /**
     * @return the client id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @return the client secret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @return the refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @param refreshToken
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}