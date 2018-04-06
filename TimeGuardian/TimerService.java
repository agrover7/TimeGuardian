package com.example.jackbriody.TimeGuardian;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class TimerService extends IntentService {

    public TimerService() {

        super("Timer Service");
    }
    private int notificationId = 0;
    private int i;


    @Override
    public void onCreate(){
        super.onCreate();
        //Log.v("timer", "Timer service has started.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int time = intent.getIntExtra("time", 0);
       // Log.v("import time is ", "" + time);
        for (i=0; i < time; ++i) {
            Log.v("timer", "i = " + i);

            try {
                Thread.sleep(1000);
            }
            catch(Exception e) {
            }
        }

        if (i==300) {
            String chanID = "warningChan";
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.timer_channel);
                String description = getString(R.string.timerChanDescription);
                NotificationChannel channel = new NotificationChannel(chanID, name, NotificationManager.IMPORTANCE_HIGH);
                channel.enableVibration(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setDescription(description);
                NotificationManager manage = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manage.createNotificationChannel(channel);
            }

            Intent alarm = new Intent(this, AlarmClass.class);
            PendingIntent alarmPendingIntent = PendingIntent.getActivity(this, 0, alarm, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, chanID)
                    .setSmallIcon(R.drawable.exclamation)
                    .setContentTitle("App_Name")
                    .setContentText("TIMER ENDS IN 5 MINUTES")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(alarmPendingIntent);

            notificationManager.notify(notificationId, mBuilder.build());
        }
        if (i==time) {
            String chanID = "timerChan";
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.timer_channel);
                String description = getString(R.string.timerChanDescription);
                NotificationChannel channel = new NotificationChannel(chanID, name, NotificationManager.IMPORTANCE_HIGH);
                channel.enableVibration(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setDescription(description);
                NotificationManager manage = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manage.createNotificationChannel(channel);
            }

            Intent alarm = new Intent(this, AlarmClass.class);
            PendingIntent alarmPendingIntent = PendingIntent.getActivity(this, 0, alarm, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, chanID)
                    .setSmallIcon(R.drawable.exclamation)
                    .setContentTitle("App_Name")
                    .setContentText("TIMER ENDED: OPEN APP")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(alarmPendingIntent);

            notificationManager.notify(notificationId, mBuilder.build());
        }
    }

}
