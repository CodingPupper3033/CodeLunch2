package com.example.codelunch2.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.codelunch2.api.NutrisliceMultipleRequester;
import com.example.codelunch2.api.NutrisliceMultipleRequesterListener;
import com.example.codelunch2.api.NutrisliceRequestErrorData;
import com.example.codelunch2.notification.NutrisliceDataConverter;
import com.example.codelunch2.notification.NutrisliceNotificationBuilder;
import com.example.codelunch2.settings.storage.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class RequestNotificationReceiver extends BroadcastReceiver {
     public static final String TAG = "Request Notif Receiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: The call was received");

        NutrisliceMultipleRequester multipleRequester = new NutrisliceMultipleRequester(context);
        // Add School Menu's
        try {
            JSONArray schoolsData = NutrisliceStorage.getData(context);
            for (int i = 0; i < schoolsData.length(); i++) { // Loop through Schools
                JSONObject school = schoolsData.getJSONObject(i);
                String schoolName = school.getString("name");
                if (school.getBoolean("enabled")) {
                    JSONArray menuData = school.getJSONArray("menus");
                    for (int j = 0; j < menuData.length(); j++) { // Loop through Menus
                        JSONObject menu = menuData.getJSONObject(j);
                        String menuName = menu.getString("name");
                        if (menu.getBoolean("enabled")) {
                            multipleRequester.addRequester(schoolName,menuName);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make the requests
        multipleRequester.getFoodFromDay(Calendar.getInstance(), new NutrisliceMultipleRequesterListener() {
            @Override
            public void onReceive(JSONArray successData, ArrayList<NutrisliceRequestErrorData> errors) {
                Log.d(TAG, "Data received from schools: " + successData);
                Log.d(TAG, "Menus with errors: " + errors.size());
                Log.d(TAG, "onReceive: \n" + NutrisliceDataConverter.notificationMaker(context, successData, errors));
                NutrisliceNotificationBuilder notificationBuilder = new NutrisliceNotificationBuilder(context, successData, errors);
                notificationBuilder.sendNotification();
            }
        });

    }
}
