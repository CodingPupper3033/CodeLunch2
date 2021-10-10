package com.codelunch.app.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.codelunch.app.R;


public class NotificationsSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.notification_settings_preference, rootKey);
    }
}