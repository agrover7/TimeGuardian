package com.example.jackbriody.TimeGuardian;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

public class PostLogin extends AppCompatActivity {


    private String access;
    private static String refresh;

    private Long totalTime;
    private EditText hours;
    private EditText minutes;
    private EditText seconds;
    private static Long userHours;
    private static Long userMinutes;
    private static Long userSeconds;

    private long start_time_in_millis = 10000;

    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;

    private long mEndTime;
    private boolean first = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_login);
        goToUrl("https://account-sandbox.safetrek.io/authorize?client_id=gk1nFtbQr4pBpJD0rzAp3vaSi555sm4s&scope=openid%20phone%20offline_access&state=hjbqfgubasfih1348971949291&response_type=code&redirect_uri=https://timeguardian.herokuapp.com/callback");
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        hours = findViewById(R.id.editText3);
        minutes = findViewById(R.id.editText4);
        seconds = findViewById(R.id.editText5);

        //Request Location Permissions
        requestLocationPermissions();

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                    Log.v("totalTime", totalTime + "");
                    Intent intent = new Intent(PostLogin.this, TimerService.class);
                    int newTime = totalTime.intValue();
                    intent.putExtra("time", newTime);
                    startService(intent);
                }
            }
        });


        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //timer set up
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

        if (findViewById(R.id.editText3) != null && findViewById(R.id.editText4) != null && findViewById(R.id.editText5) != null) {
            hours = findViewById(R.id.editText3);
            minutes = findViewById(R.id.editText4);
            seconds = findViewById(R.id.editText5);
        }
        else {
            Toast.makeText(this, "Please input values into all fields", Toast.LENGTH_SHORT).show();
        }

        boolean firstTime = true;
        setDefaultBoolean("@string/firstTime", firstTime, this);

        //authToken
        Uri uri = getIntent().getData();
        if (uri != null && getDefaultBoolean("@string/firstTime", this) == true) { //troubleshoot
            access = uri.getQueryParameter("access_token");
            refresh = uri.getQueryParameter("refresh_token");

            Date d = new Date();
            setDefaultLong( "@string/authDate", d.getTime(),this);

           setDefaultBoolean("@string/firstTime", false, this);
        }

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        Button submit = findViewById(R.id.submitTime);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_time_in_millis = userTimes(hours.getText().toString(), minutes.getText().toString(), seconds.getText().toString());
                totalTime = start_time_in_millis/1000;
                Log.v("Total time", ""+ totalTime);
            }

        });

    }

    /**
     * @param h (hours) inputted
     * @param m (minutes) inputted
     * @param s (seconds) inputted
     * @return time inputting by the user in milliseconds
     */
    private long userTimes(String h, String m, String s) {
        userHours = Long.valueOf(h) * 3600000;
        userMinutes = Long.valueOf(m) * 60000;
        userSeconds = Long.valueOf(s) * 1000;
        return userHours + userMinutes + userSeconds;

    }
    /**
     * @return refresh token
     */
    public static  String getRefreshToken() {
        return refresh;
    }

    /**
     * @param url
     * opens browser to url
     */
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    /**
     * @param key (key for storage)
     * @param context
     * @param l (long being stored)
     * long l is stored, String key is set
     */
    public static void setDefaultLong(String key, Long l, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, l);
        editor.commit();
    }

    /**
     * @param key (key for storage)
     * @param context
     * @param b (boolean being stored)
     * boolean b is stored, String key is set
     */
    public static void setDefaultBoolean(String key, Boolean b, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, b);
        editor.commit();
    }

    /**
     * @param key (key for storage -- specified as an input for the setter)
     * @param context
     * @return the default boolean
     */
    public static Boolean getDefaultBoolean(String key, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, true);
    }

    /**
     * @param key (key for storage -- specified as an input for the setter)
     * @param context
     * @return the default long
     */
    public static Long getDefaultLong(String key, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, 0);
    }

    /**
     * the user's location is requested
     * (permissions needed to bounce location out with alarm call)
     */
    private void requestLocationPermissions() {
        int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }
    }

    /**
     * @return hours inputted by user
     */
    public static long getHours() {
        return userHours;
    }
    /**
     * @return minutes inputted by user
     */
    public static long getMinutes() {
        return userMinutes;
    }
    /**
     * @return seconds inputted by user
     */
    public static long getSeconds() {
        return userSeconds;
    }

    /**
     * triggers the timer's start
     */
    private void startTimer() {

        Log.v("time", "" + mTimeLeftInMillis);
        mTimeLeftInMillis = 1000 * totalTime;

        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }


            @Override
            public void onFinish() {
//                notifyUser();
                mTimerRunning = false;
                updateButtons();
                finish();
                Intent alarmIntent = new Intent(PostLogin.this, AlarmClass.class);
                startActivity(alarmIntent);
            }
        }.start();

        mTimerRunning = true;
        updateButtons();
    }

    /**
     * triggers the timer's stop
     */
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateButtons();
    }

    /**
     * resets the timer
     */
    private void resetTimer() {
        mTimeLeftInMillis = start_time_in_millis;
        updateCountDownText();
        updateButtons();
    }

    /**
     * updates the text of the countdown
     */
    private void updateCountDownText() {
        int hours = 0;
        if (mTimeLeftInMillis % 3600000 > 0) {
            hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        }
        int minutes = (int) (mTimeLeftInMillis - hours * 3600000) / 60000;
        int seconds = (int) (mTimeLeftInMillis - hours * 3600000 - minutes * 60000) / 1000;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        if (first == true) {
            timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d", 0, 0, 0 );
            first = false;
        }
        Log.v("try", timeLeftFormatted);
        mTextViewCountDown.setText(timeLeftFormatted);

    }

    /**
     * swaps pause/start buttons and changes reset visibility
     */
    private void updateButtons() {
        if (mTimerRunning) {
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pause");
        } else {
            mButtonStartPause.setText("Start");

            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }

            if (mTimeLeftInMillis < start_time_in_millis) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
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

        mTimeLeftInMillis = prefs.getLong("@string/TimeLeft", start_time_in_millis);
        mTimerRunning = prefs.getBoolean("@string/IsRunning", false);

        updateCountDownText();
        updateButtons();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("@string/EndTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            }
            else {
                startTimer();
            }
        }
    }
}