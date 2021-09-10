package com.codelunch.app.findEverything;


import android.app.Activity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.codelunch.app.R;
import com.codelunch.app.api.NutrisliceFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchOrganizationHandler implements Response.Listener, SearchView.OnQueryTextListener, Response.ErrorListener {
    Activity activity;

    MenuItem search;

    SearchView searchView;
    ProgressBar progressBar;
    ListView listView;

    RequestQueue queue;
    NutrisliceFinder finder;


    public SearchOrganizationHandler(Activity activity, MenuItem search, int id_progressbar, int id_list_view) {
        this.activity = activity;

        this.search = search;
        this.searchView = (SearchView) search.getActionView();
        progressBar = activity.findViewById(id_progressbar);
        listView = activity.findViewById(id_list_view);

        queue = Volley.newRequestQueue(activity);
        finder = new NutrisliceFinder(activity, queue);

        searchView.setQueryHint(activity.getResources().getString(R.string.find_school_hint)); // Hint

        setListSearch3();

        // Text change update
        searchView.setOnQueryTextListener(this);
    }

    private void runOrganizationSearch(String searchTerm) {
        if (searchTerm.length() > 2) { // Big enough search
            progressBar.setVisibility(View.VISIBLE);
            Log.d("TAG", "runOrganizationSearch: ");
            finder.makeOrganizationRequest(searchTerm, this, this);
        } else {
            setListSearch3();
        }
    }

    @Override
    public void onResponse(Object response) {
        JSONObject jsonResponse = (JSONObject) response; // Response from the request

        try {
            JSONArray resultsArray = jsonResponse.getJSONArray("results"); // Get the results specifically
            if (resultsArray.length() > 0) { // Some results
                setListConvert(resultsArray); // Setup list

                // Setup Selection
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            JSONObject jsonObject = resultsArray.getJSONObject(position);
                            SearchSchoolHandler searchSchoolHandler = new SearchSchoolHandler(activity, finder, jsonObject);

                            // Close Dialogue
                            search.collapseActionView();
                            listView.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } else {
                setListNoResults();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String[] convertDataToNameList(JSONArray resultsArray) throws JSONException {
        String[] resultsNames = new String[resultsArray.length()]; // output
        for (int i = 0; i < resultsArray.length(); i++) { // Add all names to the array
            JSONObject currentResult = resultsArray.getJSONObject(i);

            // Add name to list
            resultsNames[i] = currentResult.getString("name");
        }
        return resultsNames;
    }

    private void setList(String[] list) {
        progressBar.setVisibility(View.INVISIBLE);
        ArrayAdapter adapter = new ArrayAdapter<String>(activity.getApplicationContext(), R.layout.text_view_search_result, list);
        listView.setAdapter(adapter);
    }

    private void setListConvert(JSONArray resultsArray) throws JSONException {
        setList(convertDataToNameList(resultsArray));
    }

    private void setListNoResults() {
        String[] noResultsStringArray = {activity.getResources().getString(R.string.no_schools_found)};
        setList(noResultsStringArray);
    }

    private void setListSearch3() {
        String[] noResultsStringArray = {activity.getResources().getString(R.string.search_after_3)};
        setList(noResultsStringArray);
    }

    private void setListRequestError() {
        String[] noConnectionStringArray = {activity.getResources().getString(R.string.unable_to_connect_to_nutrislice_retry)};
        setList(noConnectionStringArray);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false; // Don't get to submit
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        runOrganizationSearch(newText);
        return true;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        setListRequestError();
    }
}
