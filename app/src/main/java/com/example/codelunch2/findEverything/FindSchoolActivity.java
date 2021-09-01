package com.example.codelunch2.findEverything;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.codelunch2.R;
import com.example.codelunch2.api.NutrisliceFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FindSchoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_school);

        ListView resultsListView = findViewById(R.id.schoolResultsListView);

        SearchView searchView = findViewById(R.id.schoolSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 3) { // Long enough Name
                    // Start loading icon
                    findViewById(R.id.schoolSearchProgressBar).setVisibility(View.VISIBLE);

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
                                findViewById(R.id.schoolSearchProgressBar).setVisibility(View.INVISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Stop loading icon
                            findViewById(R.id.schoolSearchProgressBar).setVisibility(View.INVISIBLE);

                            String[] noConnectionStringArray = {getResources().getString(R.string.unable_to_connect_to_nutrislice_retry)};
                            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, noConnectionStringArray);
                            resultsListView.setAdapter(adapter);
                            resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    // Retry
                                    onQueryTextChange(newText);
                                }
                            });
                        }
                    };

                    NutrisliceFinder.makeOrganizationRequest(getApplicationContext(), newText, listener, errorListener);
                } else {
                    String[] noResultsStringArray = {getResources().getString(R.string.search_after_3)};
                    ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, noResultsStringArray);
                    resultsListView.setAdapter(adapter);
                    resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    });
                }
                return true;
            }
        });
    }
}