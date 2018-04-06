package com.example.jackbriody.TimeGuardian;

import android.app.IntentService;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LocationService extends IntentService {

    private LocationCoordinates location;
    private double latitude;
    private double longitude;
    private int accuracy;
    private String alarmTime;
    private String newAccess;
    private String grant_type = "refresh_token";
    private String client_secret = "eWTSj_izMvD3nBJFXxkRDZF4aXDGKofYRZyzw_31oer31kuoY6-OVDs27nEHJu0B";
    private String client_id = "gk1nFtbQr4pBpJD0rzAp3vaSi555sm4s";
    public LocationService() {

        super("Location Service");
    }


    @Override
    public void onCreate(){
        super.onCreate();
    }

    public void refreshToken() {
        final String BASE_URL = "https://login-sandbox.safetrek.io"; //change later

        Retrofit retroRefresh = new Retrofit.Builder()
                .baseUrl(BASE_URL)                              //same BASE_URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SafeRetro refresh = retroRefresh.create(SafeRetro.class);
        Call<AuthTokenResponse> tokenCall = refresh.GetNewToken(
                grant_type,
                client_id,
                client_secret,
                PostLogin.getRefreshToken()
        );

        tokenCall.enqueue(new Callback<AuthTokenResponse>() {
            @Override
            public void onResponse(Call<AuthTokenResponse> call, Response<AuthTokenResponse> response) {
                if(response.code() == 200) {
                    AuthTokenResponse auth = response.body();
                    newAccess = auth.getAccessToken();
                }
            }

            @Override
            public void onFailure(Call<AuthTokenResponse> call, Throwable t) {
                Toast.makeText(LocationService.this, "Contact with SafeTrek failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        int time = intent.getIntExtra("time", 0);
        // Log.v("import time is ", "" + time);
        while(!(AlarmResponse.getStatus().equals("CANCELLED"))) {
            try {
                Thread.sleep(60000);
            } catch (Exception e) {

            }

            Long oldDate = PostLogin.getDefaultLong("@string/authDate",LocationService.this);
            Long newDate = new Date().getTime();
            Long diffDates = newDate - oldDate;
            int timeToRefresh = (10 * 3600 * 1000) - (1000 * 60 * 3);

            if(diffDates >= timeToRefresh) {
                refreshToken();
            }

            String BASE_URL = "https://api.safetrek.io";
            getLocation();
            location = new LocationCoordinates(longitude, latitude, accuracy, alarmTime);

            Retrofit retroLocation = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            SafeRetro locUpdate = retroLocation.create(SafeRetro.class);
            Call<UpdatedLocationResponse> updatedLocation = locUpdate.UpdateLocation(
                    "Content-Type: application/json",
                    "Authorization: Bearer " + newAccess,
                    location
            );

            updatedLocation.enqueue(new Callback<UpdatedLocationResponse>() {
                @Override
                public void onResponse(Call<UpdatedLocationResponse> call, Response<UpdatedLocationResponse> response) {
                    Toast.makeText(LocationService.this, "Location updated.", Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(Call<UpdatedLocationResponse> call, Throwable t) {
                    Toast.makeText(LocationService.this, "Location update failed.", Toast.LENGTH_SHORT);
                }
            });
        }
    }

    /**
     * @return the location of the user (stored in instance variables)
     */
    private void getLocation() {
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager lm = (LocationManager)getSystemService(this.LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            try {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                accuracy = (int) location.getAccuracy();
            }
            catch (Exception e) {
                Toast.makeText(this, "Error authenticating location services. Quit and try again.",Toast.LENGTH_LONG);
            }
        }
    }

}
