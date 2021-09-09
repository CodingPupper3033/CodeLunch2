package com.codelunch.app.settings;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.codelunch.app.R;
import com.codelunch.app.settings.storage.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;

public class DeveloperSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.developer_settings_preference, rootKey);

        // Set Data
        EditTextPreference setDataEditPreference = findPreference("nutrisliceDataViewer");
        setDataEditPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                JSONArray data = NutrisliceStorage.getData(getContext());
                String text = "";
                try {
                    text = data.toString(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                text = text.replace("\\", "");
                setDataEditPreference.setText(text);
                return true;
            }
        });

        setDataEditPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    JSONArray data = new JSONArray(newValue.toString());
                    NutrisliceStorage.setData(getContext(), data);
                    return true;
                } catch (JSONException e) {
                    Toast toast = new Toast(getContext());
                    toast.setText(R.string.nutrislice_storage_invalid_input);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                    return false;
                }
            }
        });

        // Only allow numbers for max previous storage
        EditTextPreference maxStorePreviousPreference = findPreference("max_previous_request_store");
        maxStorePreviousPreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });
    }
}