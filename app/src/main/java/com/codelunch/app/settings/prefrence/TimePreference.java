package com.codelunch.app.settings.prefrence;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.codelunch.app.alarm.SetupNotificationReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePreference extends Preference {
    public static final String TAG = "Time Preference";

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        updateSummary();
    }
    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateSummary();
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        updateSummary();
    }

    public void updateSummary() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Calendar time = getTime();

        String timeString;
        if(DateFormat.is24HourFormat(this.getContext())) { // 24 HR Time in Android
            timeString = formatTime24HR(time);
        } else {
            timeString = formatTime12HR(time);
        }

        setSummary(timeString);
    }

    public void setTime(Calendar time) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Log.d(TAG, "Time set to " + formatTime24HR(time));
        editor.putString(getKey(), formatTime24HR(time));
        editor.apply();

        updateSummary();
    }

    private String formatTime24HR(Calendar time){
        Date date = time.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        return format.format(date);
    }

    private String formatTime12HR(Calendar time){
        Date date = time.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm a");

        return format.format(date);
    }

    public Calendar getTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String timeString = sharedPreferences.getString(getKey(),"00:00");
        String[] timeArray = timeString.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int min = Integer.parseInt(timeArray[1]);

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, min);

        return time;
    }

    public void onClick() {
        super.onClick();

        TimePickerDialog dialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            time.set(Calendar.MINUTE, minute);
            setTime(time);

            // Update Notification
            SetupNotificationReceiver.updateAlarm(view.getContext());
        },getTime().get(Calendar.HOUR_OF_DAY),getTime().get(Calendar.MINUTE), DateFormat.is24HourFormat(this.getContext()));
        dialog.show();
    }

}
