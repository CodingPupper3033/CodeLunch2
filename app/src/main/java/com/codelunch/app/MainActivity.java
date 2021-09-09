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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.codelunch.app.alarm.SetupNotificationReceiver;
import com.codelunch.app.api.NutrisliceFinder;
import com.codelunch.app.findEverything.SelectSchoolActivity;
import com.codelunch.app.settings.SettingsActivity;
import com.codelunch.app.settings.storage.NutrisliceStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    ProgressBar searchResultsProgressBar;
    RequestQueue queue;

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

        // Setup search Results
        searchResultsProgressBar = findViewById(R.id.schoolSearchProgressBar);

        // Setup queue
        queue = Volley.newRequestQueue(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Search View
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.find_school_hint)); // Hint
        ListView listView = findViewById(R.id.listViewSearchResults);

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

        // Setup text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Thou shall not submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    ProgressBar progressBarSearchResults = findViewById(R.id.searchResultsProgressBar);
                    ListView resultsListView = findViewById(R.id.listViewSearchResults);

                    // Make requester
                    Response.Listener listener = new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            JSONObject jsonResponse = (JSONObject) response; // Response from the request
                            try {
                                JSONArray resultsArray = jsonResponse.getJSONArray("results"); // Get the results specifically
                                if (resultsArray.length() > 0) { // Some results
                                    String[] resultsNames = new String[resultsArray.length()]; // output
                                    for (int i = 0; i < resultsArray.length(); i++) { // Add all names to the array
                                        JSONObject currentResult = resultsArray.getJSONObject(i);

                                        // Add name to list
                                        resultsNames[i] = currentResult.getString("name");
                                    }

                                    // Show Results
                                    ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, resultsNames);
                                    resultsListView.setAdapter(adapter);
                                    resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            try {
                                                JSONObject selectedItem = resultsArray.getJSONObject(position);

                                                // Start activity to select School
                                                Intent intent = new Intent(getApplicationContext(), SelectSchoolActivity.class);
                                                intent.putExtra("object", selectedItem.toString());
                                                startActivity(intent);
                                                finish();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else { // No results
                                    String[] noResultsStringArray = {getResources().getString(R.string.no_schools_found)};
                                    ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, noResultsStringArray);
                                    resultsListView.setAdapter(adapter);
                                    resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        }
                                    });
                                }


                                // Stop loading icon
                                progressBarSearchResults.setVisibility(View.INVISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    // Make request
                    NutrisliceFinder finder = new NutrisliceFinder(queue);
                    finder.makeOrganizationRequest(newText, listener, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO
                        }
                    });
                } else {
                    String[] noResultsStringArray = {getResources().getString(R.string.search_after_3)};
                    ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, noResultsStringArray);
                    listView.setAdapter(adapter);
                }
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