package com.example.codelunch2.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codelunch2.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Hide the title bar
        try{ this.getSupportActionBar().hide(); } catch (NullPointerException e){}

        // Setups settings fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }
}