package com.example.jackbriody.TimeGuardian;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.location.*;
import android.content.pm.PackageManager;


public class AlarmClass extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 30000;
    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private Services services = new Services(true, false, false);
    public double longitude = 0.0;
    public double latitude = 0.0;
    private LocationCoordinates location;
    private float accuracy;
    private long mEndTime;

    private String grant_type = "refresh_token";
    private String client_secret = "eWTSj_izMvD3nBJFXxkRDZF4aXDGKofYRZyzw_31oer31kuoY6-OVDs27nEHJu0B";
    private String client_id = "gk1nFtbQr4pBpJD0rzAp3vaSi555sm4s";

    private static String alarmID;
    private String alarmTime;
    private String alarmStatus;
    public static String newAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        mTextViewCountDown = findViewById(R.id.new_view_countdown);

        startTimer();

        updateCountDownText();

        Button help = findViewById(R.id.help);
        Button reset = findViewById(R.id.restart);
        Button cancel = findViewById(R.id.cancel);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrofit --> authToken POST
                if (checkPermissions() == true) {
                    final String BASE_URL = "https://login-sandbox.safetrek.io"; //change later

                    Long oldDate = PostLogin.getDefaultLong("@string/authDate",AlarmClass.this);
                    Long newDate = new Date().getTime();
                    Long diffDates = newDate - oldDate;
                    int timeToRefresh = (10 * 3600 * 1000) - (1000 * 60 * 3);   //10 hour token validity - a few minutes so that the app doesn't timeout mid-request

                    if(diffDates >= timeToRefresh) {
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
                                Toast.makeText(AlarmClass.this, "Contact with SafeTrek failed!", Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });
                    }
                    getLocation();
                    location = new LocationCoordinates(longitude, latitude, accuracy, alarmTime);
                    Retrofit retroAlarm = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    SafeRetro alarm = retroAlarm.create(SafeRetro.class);
                    Call<AlarmResponse> alarmCall = alarm.PostAccessToken(
                            "Content-Type: application/json",
                            "Authorization: Bearer " + newAccess,
                            services,
                            location
                    );

                    alarmCall.enqueue(new Callback<AlarmResponse>() {
                        @Override
                        public void onResponse(Call<AlarmResponse> call, Response<AlarmResponse> response) {
                            if(response.code() == 200) {
                                AlarmResponse returnedAlarm = response.body();
                                alarmStatus = returnedAlarm.getStatus();
                                alarmID = returnedAlarm.getId();
                                alarmTime = returnedAlarm.getTime();
                            }
                        }

                        @Override
                        public void onFailure(Call<AlarmResponse> call, Throwable t) {
                            Toast.makeText(AlarmClass.this, "Contact with SafeTrek failed!", Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });

                    Toast.makeText(AlarmClass.this, "Help is on the way!", Toast.LENGTH_LONG).show();
                    finish();
                    Intent revertToOld = new Intent(AlarmClass.this, PostLogin.class);
                    startActivity(revertToOld);
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent refresh = new Intent(AlarmClass.this, PostLogin.class);
                mTimeLeftInMillis = 0;
                updateCountDownText();
                finish();
                startActivity(refresh);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clear = new Intent(AlarmClass.this, PostLogin.class);
                mTimeLeftInMillis = 0;
                updateCountDownText();
                finish();
                startActivity(clear);
            }
        });
    }

    /**
     * @return user permissions (location)
     */
    private boolean checkPermissions() {
         boolean permissions = false;
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissions = true;
        }
        return permissions;
    }


    /**
     * sets the user's location (instance variables)
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
                    accuracy = location.getAccuracy();
                }
                catch (Exception e) {
                    Toast.makeText(this, "Error authenticating location services. Quit and try again.",Toast.LENGTH_LONG);
                }
            }
    }

    /**
     * triggers the timer's start
     */
    private void startTimer() {

        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                if (checkPermissions() == true) {
                    final String BASE_URL = "https://login-sandbox.safetrek.io"; //change later

                    Long oldDate = PostLogin.getDefaultLong("@string/authDate",AlarmClass.this);
                    Long newDate = new Date().getTime();
                    Long diffDates = newDate - oldDate;
                    int timeToRefresh = (10 * 3600 * 1000) - (1000 * 60 * 3);   //10 hour token validity - a few minutes so that the app doesn't timeout mid-request

                    if(diffDates >= timeToRefresh) {
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
                                Toast.makeText(AlarmClass.this, "Contact with SafeTrek failed!", Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });
                    }
                    getLocation();
                    location = new LocationCoordinates(longitude, latitude, accuracy, alarmTime);
                    Retrofit retroAlarm = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    SafeRetro alarm = retroAlarm.create(SafeRetro.class);
                    Call<AlarmResponse> alarmCall = alarm.PostAccessToken(
                           "Content-Type: application/json",
                            "Authorization: Bearer " + newAccess,
                            services,
                            location
                    );

                    alarmCall.enqueue(new Callback<AlarmResponse>() {
                        @Override
                        public void onResponse(Call<AlarmResponse> call, Response<AlarmResponse> response) {
                            if(response.code() == 200) {
                                AlarmResponse returnedAlarm = response.body();
                                alarmStatus = returnedAlarm.getStatus();
                                alarmID = returnedAlarm.getId();
                                alarmTime = returnedAlarm.getTime();
                            }
                        }

                        @Override
                        public void onFailure(Call<AlarmResponse> call, Throwable t) {
                            Toast.makeText(AlarmClass.this, "Contact with SafeTrek failed!", Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });

                    Toast.makeText(AlarmClass.this, "Help is on the way!", Toast.LENGTH_LONG).show();
                    finish();
                    Intent revertToOld = new Intent(AlarmClass.this, PostLogin.class);
                    startActivity(revertToOld);
                }
            }
        }.start();

        mTimerRunning = true;
    }

    /**
     * updates the text of the countdown
     */
    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);

    }

    /**
     * ensures that time values are stored if the timer is stopped
     * applicable for: app quits and back arrows
     */
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("@string/TimeLeft", mTimeLeftInMillis);
        editor.putBoolean("@string/IsRunning", mTimerRunning);
        editor.putLong("@string/EndTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    /**
     * uses stored time values from the time stop to maintain accuracy
     */
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("@string/TimeLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("@string/IsRunning", false);

        updateCountDownText();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("@string/EndTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
            }
            else {
                startTimer();
            }
        }
    }

}
