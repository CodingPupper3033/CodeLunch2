package com.example.codelunch2.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class NutrisliceFinder {

    /**
     * IT NEEDS TO BE AT LEAST 3 CHARACTERS LONG FOR A SEARCH
     * @param context
     * @param searchTerm
     * @param listener
     * @param errorListener
     */
    public static void makeOrganizationRequest(Context context, String searchTerm, Response.Listener listener, Response.ErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="https://accounts.nutrislice.com/api/orgs/lookup/" + searchTerm;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        queue.add(jsonObjectRequest);
    }

    public static void makeSchoolRequest(Context context, String url, Response.Listener listener, Response.ErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String urlNew = "https://" + url + "/menu/api/schools";
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, urlNew, null, listener, errorListener);
        queue.add(jsonObjectRequest);
    }
}
