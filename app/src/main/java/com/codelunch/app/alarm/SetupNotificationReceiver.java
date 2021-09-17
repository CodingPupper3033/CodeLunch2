package com.codelunch.app.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Has the job of setting up the AlarmManager for the recurring food notification
 */
public class SetupNotificationReceiver extends BroadcastReceiver {
    public static final String TAG = "Setup Notif Receiver";
    public static final String TIME_KEY = "time";
    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarm(context);
    }

    public static void updateAlarm(Context context) {
        Calendar updateTime = getTime(context);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Log.d(TAG, "Starting Alarm, time set to " + format.format(updateTime.getTime()));

        Intent receiver = new Intent(context, RequestNotificationReceiver.class);
        PendingIntent recurringReceiver = PendingIntent.getBroadcast(context, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, recurringReceiver); // TODO User changeable interval
    }

    public static void setAlarm(Context context) {
        // Not already around
        if(PendingIntent.getBroadcast(context, 0, new Intent(context, RequestNotificationReceiver.class), PendingIntent.FLAG_NO_CREATE) == null) {
            updateAlarm(context);
        } else {
            Log.d(TAG, "Alarm already set, not starting ");
        }
    }

    public static Calendar getTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timeString = sharedPreferences.getString(TIME_KEY,"00:00");
        String[] timeArray = timeString.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int min = Integer.parseInt(timeArray[1]);

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, min);

        return time;
    }
}
