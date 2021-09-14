package com.codelunch.app.searchHandlers;


import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.codelunch.app.R;
import com.codelunch.app.api.finder.NutrisliceFinder;
import com.codelunch.app.api.finder.NutrisliceFinderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchOrganizationHandler implements Response.Listener, SearchView.OnQueryTextListener, Response.ErrorListener {
    Activity activity;

    private RecyclerView reloadRecyclerView;
    MenuItem search;

    SearchView searchView;
    ProgressBar progressBar;
    RecyclerView recyclerView;

    RequestQueue queue;
    NutrisliceFinder finder;


    public SearchOrganizationHandler(Activity activity, RecyclerView reloadRecyclerView, MenuItem search, int id_progressbar, int id_recycler_view) {
        this.activity = activity;
        this.reloadRecyclerView = reloadRecyclerView;

        this.search = search;
        this.searchView = (SearchView) search.getActionView();
        progressBar = activity.findViewById(id_progressbar);
        recyclerView = activity.findViewById(id_recycler_view);

        queue = Volley.newRequestQueue(activity);
        finder = new NutrisliceFinder(activity, queue);

        searchView.setQueryHint(activity.getResources().getString(R.string.find_school_hint)); // Hint
        // Search View On Expand and collapse
        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Show List
                recyclerView.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Hide List
                recyclerView.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        setListSearch3();

        // Text change update
        searchView.setOnQueryTextListener(this);
    }

    public SearchOrganizationHandler(Activity activity, MenuItem search, int id_progressbar, int id_recycler_view) {
        this(activity, null, search, id_progressbar, id_recycler_view);
    }

    private void runOrganizationSearch(String searchTerm) {
        if (searchTerm.length() > 2) { // Big enough search
            progressBar.setVisibility(View.VISIBLE);
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
                setListResults(resultsArray);

            } else {
                setListNoResults();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListResults(JSONArray data) {
        progressBar.setVisibility(View.INVISIBLE);
        NutrisliceFinderAdapter adapter = new NutrisliceFinderAdapter(activity, search, recyclerView, reloadRecyclerView, data, finder);
        recyclerView.setAdapter(adapter);
    }

    private void setListAnnounce(String text) {
        progressBar.setVisibility(View.INVISIBLE);
        NutrisliceFinderAdapter adapter = new NutrisliceFinderAdapter(text);
        recyclerView.setAdapter(adapter);
    }


    private void setListNoResults() {
        String text = activity.getResources().getString(R.string.no_schools_found);
        setListAnnounce(text);
    }

    private void setListSearch3() {
        String text = activity.getResources().getString(R.string.search_after_3);
        setListAnnounce(text);
    }

    private void setListRequestError() {
        String text = activity.getResources().getString(R.string.unable_to_connect_to_nutrislice_retry);
        setListAnnounce(text);
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
