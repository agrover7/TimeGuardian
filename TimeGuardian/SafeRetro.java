package com.example.jackbriody.TimeGuardian;

import com.example.jackbriody.TimeGuardian.AlarmClass;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by jackbriody on 3/23/18.
 */

public interface SafeRetro {

    @POST("/v1/alarms")
    @FormUrlEncoded
    Call<AlarmResponse> PostAccessToken (
            @Header("type") String type,
            @Header("authorization") String auth,
            @Field("services") Services services,
            @Field("location.coordinates") LocationCoordinates location
    );

    //Change to Refresh
    @Headers("Accept: application/json")
    @POST("/oauth/token")
    @FormUrlEncoded
    Call<AuthTokenResponse> GetNewToken (
            @Field("grant_type") String grant_type,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("refresh_token") String refresh
    );

    @POST("/v1/alarms/{param}/locations")
    @FormUrlEncoded
    public void PostAccessToken(@Path("param") String id, Callback<AlarmResponse> resp);
    Call<UpdatedLocationResponse> UpdateLocation (
            @Header("type") String type,
            @Header("authorization") String auth,
            @Field("coordinates")LocationCoordinates coordinates
    );

}