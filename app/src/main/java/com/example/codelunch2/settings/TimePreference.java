package com.example.codelunch2.settings;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TimePicker;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

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

        String timeString = "";
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
        editor.commit();

        updateSummary();
    }

    private String formatTime24HR(Calendar time){
        Date date = time.getTime();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        return format.format(date);
    }

    private String formatTime12HR(Calendar time){
        Date date = time.getTime();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");

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

        TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);
                setTime(time);
            }
        },getTime().get(Calendar.HOUR_OF_DAY),getTime().get(Calendar.MINUTE), DateFormat.is24HourFormat(this.getContext()));
        dialog.show();
    }

}
