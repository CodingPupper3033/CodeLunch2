package com.example.codelunch2.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.example.codelunch2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO Setup it auto guess what categories are there

public class NotificationSettingsFragment extends PreferenceFragmentCompat {
    public static final String SWITCH_SUFFIX = "SchoolSwitch";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.notification_preference, rootKey);

        PreferenceScreen preferenceScreen = this.getPreferenceScreen();

        JSONArray data = NutrisliceStorage.getData(getContext());

        for (int i = 0; i < data.length(); i++) {
            // Make it it's own category
            PreferenceCategory preferenceCategory = new PreferenceCategory(preferenceScreen.getContext());
            try {
                JSONObject schoolData = data.getJSONObject(i);

                String name = schoolData.getString("name");
                boolean enabled = schoolData.getBoolean("enabled");

                // Setup the category
                preferenceCategory.setTitle(name);
                preferenceScreen.addPreference(preferenceCategory);

                // Setup items to go in the category
                String enabledSwitchKey = name + SWITCH_SUFFIX;
                SwitchPreference enabledSwitch = new SwitchPreference(preferenceCategory.getContext());
                preferenceCategory.addPreference(enabledSwitch);
                enabledSwitch.setTitle("Enabled"); // TODO String value from resource
                enabledSwitch.setChecked(enabled);
                enabledSwitch.setKey(enabledSwitchKey);
                enabledSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        JSONObject schoolData = NutrisliceStorage.getSchool(getContext(), name);
                        try {
                            schoolData.put("enabled", (Boolean) newValue);
                            NutrisliceStorage.setSchool(getContext(), schoolData);
                            return true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });

                Preference sendToMenus = new Preference(preferenceCategory.getContext());
                preferenceCategory.addPreference(sendToMenus);
                sendToMenus.setTitle("Customize"); // TODO String value from resource
                sendToMenus.setFragment("com.example.codelunch2.settings.MenuNotificationSettingsFragment");
                sendToMenus.setDependency(enabledSwitchKey);
                sendToMenus.getExtras().putString("school", name);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
