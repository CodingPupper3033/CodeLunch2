package com.codelunch.app.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.codelunch.app.MainActivity;
import com.codelunch.app.R;
import com.codelunch.app.api.requests.NutrisliceRequestErrorData;

import org.json.JSONArray;

import java.util.ArrayList;

public class NutrisliceNotificationBuilder {
    public static final int NOTIFICATION_ICON = R.drawable.ic_notification_icon;
    public static final String CHANNEL_ID = "CodeLunch";
    private static final int importance = NotificationManager.IMPORTANCE_DEFAULT;
    private static final int NOTIFICATION_ID = 1;

    Context context;
    JSONArray successData;
    ArrayList<NutrisliceRequestErrorData> errors;

    public NutrisliceNotificationBuilder(Context context, JSONArray successData, ArrayList<NutrisliceRequestErrorData> errors) {
        this.context = context;
        this.successData = successData;
        this.errors = errors;

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder buildNotification() {
        String text = NutrisliceDataConverterNotification.notificationMaker(context, successData, errors);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(NOTIFICATION_ICON)
                .setColor(16737792)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
    }

    public void sendNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(NOTIFICATION_ID, buildNotification().build());
    }

}
