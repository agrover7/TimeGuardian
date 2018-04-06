package com.example.jackbriody.TimeGuardian; /**
 * Created by jackbriody on 3/24/18.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthTokenResponse {

        @SerializedName("access_token")
        @Expose
        private String accessToken;
        @SerializedName("token_type")
        @Expose
        private String tokenType = "bearer";
        @SerializedName("expires_in")
        @Expose
        private Integer expiresIn = 36000;
        @SerializedName("scope")
        @Expose
        private String scope = "space delimited string of scopes";

    public AuthTokenResponse(String accessToken, String refreshToken, String tokenType, Integer expiresIn, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
    }
    /**
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return the type of token
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * @return the time until the token expires (in seconds); 10 hours
     */
    public Integer getExpiresIn() {
        return expiresIn;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

}
