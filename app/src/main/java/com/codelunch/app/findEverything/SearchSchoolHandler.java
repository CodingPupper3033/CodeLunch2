package com.codelunch.app.findEverything;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codelunch.app.R;
import com.codelunch.app.api.NutrisliceFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchSchoolHandler implements Response.Listener, Response.ErrorListener {
    Activity activity;

    String name;
    String menus_domain;

    NutrisliceFinder finder;

    public SearchSchoolHandler(Activity activity, NutrisliceFinder finder, JSONObject item) throws JSONException {
        this.activity = activity;
        this.finder = finder;
        this.name = item.getString("name");
        this.menus_domain = item.getString("menus_domain");

        // Make School Request
        finder.makeSchoolRequest(menus_domain, this, this);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(activity, activity.getResources().getString(R.string.unable_connect_retry), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Object response) {
        JSONArray resultsArray = (JSONArray) response;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(name + ':');
        try {
            builder.setItems(convertDataToNameList(resultsArray), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        JSONObject jsonObject = resultsArray.getJSONObject(which);
                        SearchMenusHandler searchMenusHandler = new SearchMenusHandler(activity, finder, menus_domain, jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //


        builder.show();
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

}
