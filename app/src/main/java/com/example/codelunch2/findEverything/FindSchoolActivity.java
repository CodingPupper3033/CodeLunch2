package com.example.codelunch2.findEverything;

import android.os.Bundle;
import android.util.Log;
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
import com.example.codelunch2.settings.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FindSchoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_school);
        Log.d("TAG", "onCreate: ");

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
                            // TODO
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
                                                String name = selectedItem.getString("name");
                                                String url = selectedItem.getString("menus_domain");

                                                // Add School
                                                NutrisliceStorage.addSchool(getApplicationContext(), name, url);
                                                Log.d("TAG", "onItemClick: " + NutrisliceStorage.getData(getApplicationContext()));

                                                NutrisliceFinder.makeSchoolRequest(getApplicationContext(), url, new Response.Listener() {
                                                    @Override
                                                    public void onResponse(Object response) {
                                                        JSONArray menusArray = (JSONArray) response;
                                                        Log.d("TAG", "onResponse: " + menusArray);
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.d("TAG", "onErrorResponse: " + error);
                                                        // TODO
                                                    }
                                                });

                                                // TODO Go from here


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else { // No results
                                    String[] noResultsStringArray = new String[1];
                                    noResultsStringArray[0] = "No schools with this name found"; // TODO From string resource
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

                            String[] noConnectionStringArray = new String[1];
                            noConnectionStringArray[0] = "Unable to contact Nutrislice, check you internet connection"; // TODO From string resource
                            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_search_result, noConnectionStringArray);
                            resultsListView.setAdapter(adapter);
                            resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            });
                        }
                    };

                    NutrisliceFinder.makeOrganizationRequest(getApplicationContext(), newText, listener, errorListener);
                } else {
                    String[] noResultsStringArray = new String[1];
                    noResultsStringArray[0] = "Will start searching after 3 characters are entered"; // TODO From string resource
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