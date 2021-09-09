package com.codelunch.app.settings;

import android.os.Bundle;

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

public class MenuNotificationSettingsFragment extends PreferenceFragmentCompat {
    public static final String SWITCH_SUFFIX = "MenuSwitch";
    private String school;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.notification_preference, rootKey);
        try {
            school = getArguments().getString("school");
            JSONArray menuData = NutrisliceStorage.getMenuData(getContext(),school);
            PreferenceScreen preferenceScreen = this.getPreferenceScreen();

            // Show all stored menus
            for (int i = 0; i < menuData.length(); i++) {
                JSONObject menuObject = menuData.getJSONObject(i);

                String name = menuObject.getString("name");
                boolean enabled = menuObject.getBoolean("enabled");

                // Make it it's own category
                PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());

                // Setup the category
                preferenceCategory.setTitle(name);
                preferenceScreen.addPreference(preferenceCategory);

                // Setup items to go in the category

                String enabledSwitchKey = name + SWITCH_SUFFIX;
                SwitchPreference enabledSwitch = new SwitchPreference(preferenceCategory.getContext());
                preferenceCategory.addPreference(enabledSwitch);
                enabledSwitch.setTitle(R.string.enabled);
                enabledSwitch.setChecked(enabled);
                enabledSwitch.setKey(enabledSwitchKey);
                enabledSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        JSONObject menuData = NutrisliceStorage.getMenu(getContext(), school, name);
                        try {
                            menuData.put("enabled", (Boolean) newValue);
                            NutrisliceStorage.setMenu(getContext(), school, menuData);
                            return true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });


                Preference sendToCategories = new Preference(preferenceCategory.getContext());
                preferenceCategory.addPreference(sendToCategories);
                sendToCategories.setTitle(R.string.customize);
                sendToCategories.setFragment("com.codelunch.app.settings.CategoryNotificationSettingsFragment");
                sendToCategories.setDependency(enabledSwitchKey);
                sendToCategories.getExtras().putString("school", school);
                sendToCategories.getExtras().putString("menu", name);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
