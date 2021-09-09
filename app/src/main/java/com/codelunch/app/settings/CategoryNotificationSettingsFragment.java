package com.codelunch.app.settings;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.codelunch.app.R;
import com.codelunch.app.settings.storage.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryNotificationSettingsFragment extends PreferenceFragmentCompat  {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.notification_preference, rootKey);
        String school = getArguments().getString("school");
        String menu = getArguments().getString("menu");

        PreferenceScreen preferenceScreen = this.getPreferenceScreen(); // Screen
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext()); // Category for them all

        // Setup the category
        preferenceCategory.setTitle(school + "; " + menu);
        preferenceScreen.addPreference(preferenceCategory);

        JSONArray categories = NutrisliceStorage.getCategoryData(getContext(), school, menu);

        // Add all the categories to the screen
        try {
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                String name = category.getString("name");
                boolean enabled = category.getBoolean("enabled");

                SwitchPreference switchPreference = new SwitchPreference(getContext());
                switchPreference.setTitle(name);
                switchPreference.setChecked(enabled);
                switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        JSONObject category = NutrisliceStorage.getCategory(getContext(),school,menu,name);
                        try {
                            category.put("enabled", (Boolean)newValue);
                            NutrisliceStorage.setCategory(getContext(), school, menu, category);
                            Log.d("TAG", "onPreferenceChange: " + NutrisliceStorage.getData(getContext()));
                            return true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return false;
                    }
                });
                preferenceCategory.addPreference(switchPreference);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
