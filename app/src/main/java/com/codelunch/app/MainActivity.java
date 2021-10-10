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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codelunch.app.alarm.SetupNotificationReceiver;
import com.codelunch.app.searchHandlers.SearchOrganizationHandler;
import com.codelunch.app.settings.SettingsActivity;
import com.codelunch.app.settings.customizeNotifications.CustomizeNotificationsSchoolActivity;
import com.codelunch.app.settings.storage.NutrisliceStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (NutrisliceStorage.isEmpty(getApplicationContext())) { // First Time Setup Activity
            Intent setup = new Intent(getApplicationContext(), FirstTimeActivity.class);
            startActivity(setup);
            finish();
            return;
        }

        // Show my Logo on actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Call the service starter (Just in case it isn't already)
        Intent setupNotification = new Intent(getApplicationContext(), SetupNotificationReceiver.class);
        Calendar updateTime = Calendar.getInstance();
        AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingSetupNotification = PendingIntent.getBroadcast(getApplicationContext(), 0, setupNotification, PendingIntent.FLAG_UPDATE_CURRENT);
        alarms.set(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), pendingSetupNotification);

        // Log Data stored currently, maybe remove?
        Log.d("TAG", "onCreate: Nutrislice Storage Data: " + NutrisliceStorage.getData(getApplicationContext()));

        // Add button to reload
        ((FloatingActionButton) findViewById(R.id.reload_menu_button)).setOnClickListener(new View.OnClickListener() {
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
        RecyclerView recyclerView = findViewById(R.id.listViewSearchResults);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        SearchOrganizationHandler searchOrganizationHandler = new SearchOrganizationHandler(this, search, R.id.searchResultsProgressBar, R.id.listViewSearchResults);

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
            case R.id.notification_button:
                intent = new Intent(this, CustomizeNotificationsSchoolActivity.class);
                this.startActivity(intent);
                break;
        }
        return true;
    }
}