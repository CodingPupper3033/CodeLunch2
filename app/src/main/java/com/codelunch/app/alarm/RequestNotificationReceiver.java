package com.codelunch.app.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.codelunch.app.api.requests.NutrisliceMultipleRequester;
import com.codelunch.app.api.requests.NutrisliceMultipleRequesterListener;
import com.codelunch.app.api.requests.NutrisliceMultipleRequesterMaker;
import com.codelunch.app.api.requests.NutrisliceRequestErrorData;
import com.codelunch.app.notification.NutrisliceDataConverterNotification;
import com.codelunch.app.notification.NutrisliceNotificationBuilder;

import org.json.JSONArray;

import java.util.ArrayList;

public class RequestNotificationReceiver extends BroadcastReceiver {
     public static final String TAG = "Request Notif Receiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: The call was received");

        NutrisliceMultipleRequester multipleRequester = NutrisliceMultipleRequesterMaker.make(context);

        // Make the requests
        multipleRequester.getFoodNow(new NutrisliceMultipleRequesterListener() {
            @Override
            public void onReceive(JSONArray successData, ArrayList<NutrisliceRequestErrorData> errors) {
                Log.d(TAG, "Data received from schools: " + successData);
                Log.d(TAG, "Menus with errors: " + errors.size());
                Log.d(TAG, "onReceive: \n" + NutrisliceDataConverterNotification.notificationMaker(context, successData, errors));
                NutrisliceNotificationBuilder notificationBuilder = new NutrisliceNotificationBuilder(context, successData, errors);
                notificationBuilder.sendNotification();
            }
        });

    }
}
