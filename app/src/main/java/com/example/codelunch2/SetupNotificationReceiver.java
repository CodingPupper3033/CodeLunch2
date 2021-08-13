package com.example.codelunch2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Has the job of setting up the AlarmManager for the recurring food notification
 */
public class SetupNotificationReceiver extends BroadcastReceiver {
    public static final String TAG = "Setup Notif Receiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Starting Alarm"); // TODO 'At x time'
        // TODO Get time from file or something
        Calendar updateTime = Calendar.getInstance();
        updateTime.add(Calendar.MINUTE, 1);

        Intent receiver = new Intent(context, RequestNotificationReceiver.class);
        PendingIntent recurringReceiver = PendingIntent.getBroadcast(context, 0, receiver, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.set(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), recurringReceiver);
    }
}
