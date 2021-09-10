package com.codelunch.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codelunch.app.alarm.SetupNotificationReceiver;
import com.codelunch.app.findEverything.SearchOrganizationHandler;
import com.codelunch.app.settings.SettingsActivity;
import com.codelunch.app.settings.storage.NutrisliceStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show my Logo on actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Call the service starter (Just in case it isn't already)
        Intent setupNotification = new Intent(getApplicationContext(), SetupNotificationReceiver.class);
        Calendar updateTime = Calendar.getInstance();
        AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingSetupNotification = PendingIntent.getBroadcast(getApplicationContext(), 0, setupNotification, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.set(AlarmManager.RTC_WAKEUP,updateTime.getTimeInMillis(),pendingSetupNotification);

        // Log Data stored currently, maybe remove?
        Log.d("TAG", "onCreate: Nutrislice Storage Data: " + NutrisliceStorage.getData(getApplicationContext()));

        // Add button to reload
        ((FloatingActionButton)findViewById(R.id.reload_menu_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu_fragment_container)).reload();
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Search View
        MenuItem search = menu.findItem(R.id.app_bar_search);
        ListView listView = findViewById(R.id.listViewSearchResults);

        SearchOrganizationHandler searchOrganizationHandler = new SearchOrganizationHandler(this, search, R.id.searchResultsProgressBar, R.id.listViewSearchResults);

        // Search View On Expand and collapse
        menu.findItem(R.id.app_bar_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Show List
                listView.setVisibility(View.VISIBLE);

                String[] noResultsStringArray = {getResources().getString(R.string.search_after_3)};
                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, noResultsStringArray);
                listView.setAdapter(adapter);

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Hide List
                listView.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings_button:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.about_button:
                intent = new Intent(this, AboutActivity.class);
                this.startActivity(intent);
                break;
        }
        return true;
    }
}