package com.example.codelunch2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codelunch2.alarm.SetupNotificationReceiver;
import com.example.codelunch2.findEverything.FindSchoolActivity;
import com.example.codelunch2.settings.NutrisliceStorage;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // TODO Remove
        NutrisliceStorage.resetData(getApplicationContext());


        // TODO Remove | Call the service starter
        Intent setupNotification = new Intent(getApplicationContext(), SetupNotificationReceiver.class);
        Calendar updateTime = Calendar.getInstance();
        AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingSetupNotification = PendingIntent.getBroadcast(getApplicationContext(), 0, setupNotification, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.set(AlarmManager.RTC_WAKEUP,updateTime.getTimeInMillis(),pendingSetupNotification);

        // TODO Remove
        //Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
        //startActivity(settings);

        // TODO Remove
        Intent findSchool = new Intent(getApplicationContext(), FindSchoolActivity.class);
        startActivity(findSchool);

    }
}

// LAND OF FORGOTTEN CODE!!!

/*
        // TODO Remove
        //NutrisliceStorage.resetData(getApplicationContext());
        NutrisliceStorage.addSchool(getApplicationContext(), "XBHS");

        NutrisliceStorage.addMenu(getApplicationContext(), "XBHS","Lunch");
        NutrisliceStorage.addMenu(getApplicationContext(), "XBHS","Dinner");

        NutrisliceStorage.addCategory(getApplicationContext(), "XBHS", "Lunch", "Center Plate");

        NutrisliceStorage.forceAddCategory(getApplicationContext(), "St. Johns", "Din Din", "Middle Plate");

        Log.d("TAG", "onCreate: " + NutrisliceStorage.getData(getApplicationContext()));
 */