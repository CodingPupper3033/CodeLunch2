package com.example.codelunch2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codelunch2.alarm.SetupNotificationReceiver;
import com.example.codelunch2.settings.SettingsActivity;
import com.example.codelunch2.settings.storage.NutrisliceStorage;

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

        // TODO Remove
        //Intent findSchool = new Intent(getApplicationContext(), FindSchoolActivity.class);
        //startActivity(findSchool);

        // TODO Remove

        Log.d("TAG", "onCreate: Nutrislice Storage Data: " + NutrisliceStorage.getData(getApplicationContext()));
        /*
        //NutrisliceRequester foo = new NutrisliceRequester(getApplicationContext(),"Xaverian Brothers High School", "Lunch");
        NutrisliceRequester foo = new NutrisliceRequester(getApplicationContext(),"North Ave Dining Hall", "Lunch");
        Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.DAY_OF_MONTH, 13);
        //calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);

        foo.getFoodFromDay(calendar, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.d("TAG", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: ");
            }
        });

         */

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