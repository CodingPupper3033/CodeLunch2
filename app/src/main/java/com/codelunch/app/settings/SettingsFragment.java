package com.codelunch.app.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.codelunch.app.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Add Event Listener to show/hide developer options
        SwitchPreference enableDeveloperOptionsSwitch = findPreference("developer");
        Preference openDeveloperOptionsPreference = findPreference("developerPreference");
        openDeveloperOptionsPreference.setVisible(getPreferenceManager().getSharedPreferences().getBoolean("developer", false));
        enableDeveloperOptionsSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean value = (Boolean) newValue;
                openDeveloperOptionsPreference.setVisible(value);
                return true;
            }
        });
    }
}
