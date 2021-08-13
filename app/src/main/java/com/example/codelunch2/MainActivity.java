package com.example.codelunch2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


import com.example.codelunch2.settings.SettingsActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // TODO Remove | Call the service starter
        Intent setupNotification = new Intent(getApplicationContext(), SetupNotificationReceiver.class);
        Calendar updateTime = Calendar.getInstance();
        AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingSetupNotification = PendingIntent.getBroadcast(getApplicationContext(), 0, setupNotification, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.set(AlarmManager.RTC_WAKEUP,updateTime.getTimeInMillis(),pendingSetupNotification);

        // TODO Remove
        Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(settings);

    }
}