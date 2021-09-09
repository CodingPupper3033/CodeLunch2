package com.codelunch.app.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class NutrisliceFinder {
    RequestQueue queue;
    public NutrisliceFinder(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public NutrisliceFinder(RequestQueue queue) {
        this.queue = queue;
    }

    public void makeOrganizationRequest(String searchTerm, Response.Listener listener, Response.ErrorListener errorListener) {
        String url ="https://accounts.nutrislice.com/api/orgs/lookup/" + searchTerm;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        queue.add(jsonObjectRequest);
    }

    public void makeSchoolRequest(String url, Response.Listener listener, Response.ErrorListener errorListener) {
        String urlNew = "https://" + url + "/menu/api/schools";
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, urlNew, null, listener, errorListener);
        queue.add(jsonObjectRequest);
    }
}
