package com.codelunch.app.api.finder;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codelunch.app.api.requests.NutrisliceRequester;

import java.util.Calendar;

public class NutrisliceFinder {
    private static final String REQUEST_TAG = "searchTag";

    RequestQueue queue;
    Context context;

    public NutrisliceFinder(Context context) {
        this.queue = Volley.newRequestQueue(context);
        this.context = context;
    }

    public NutrisliceFinder(Context context, RequestQueue queue) {
        this.queue = queue;
        this.context = context;
    }

    public void makeOrganizationRequest(String searchTerm, Response.Listener listener, Response.ErrorListener errorListener) {
        String url ="https://accounts.nutrislice.com/api/orgs/lookup/" + searchTerm;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        jsonObjectRequest.setTag(REQUEST_TAG);
        queue.cancelAll(REQUEST_TAG);
        queue.add(jsonObjectRequest);
    }

    public void makeSchoolRequest(String url, Response.Listener listener, Response.ErrorListener errorListener) {
        String urlNew = "https://" + url + "/menu/api/schools";
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, urlNew, null, listener, errorListener);
        queue.add(jsonObjectRequest);
    }

    public void makeCategoryRequest(String schoolName, String menuName, Response.Listener listener, Response.ErrorListener errorListener) {
        NutrisliceRequester requester = new NutrisliceRequester(queue, context, schoolName, menuName);
        requester.getCategoryList(Calendar.getInstance(), listener, errorListener);
    }
}
