package com.example.codelunch2.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.codelunch2.settings.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class NutrisliceRequester {
    private Context context;
    private String school;
    private String menu;

    public String getSchool() {
        return school;
    }

    public String getMenu() {
        return menu;
    }

    public NutrisliceRequester(Context context, String school, String menu) {
        this.context = context;
        this.school = school;
        this.menu = menu;
    }

    public void getCategoryList(Calendar date, Response.Listener listener, Response.ErrorListener errorListener) {
        try {
            String domain = NutrisliceStorage.getSchool(context, school).getString("menus_domain");
            String url = NutrisliceStorage.getMenu(context, school, menu).getJSONObject("urls").getString("full_menu_by_date_api_url_template");
            String fullUrl = "https://" + domain + url;
            fullUrl = fullUrl
                    .replace("{year}", String.valueOf(date.get(Calendar.YEAR)))
                    .replace("{month}", String.valueOf(date.get(Calendar.MONTH)))
                    .replace("{day}", String.valueOf(date.get(Calendar.DAY_OF_MONTH)));

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray days = response.getJSONArray("days");
                        ArrayList<String> categoryNames = new ArrayList<>();
                        for (int i = 0; i < days.length(); i++) {
                            JSONObject currentDay = days.getJSONObject(i);
                            JSONArray menuItems = currentDay.getJSONArray("menu_items");
                            for (int j = 0; j < menuItems.length(); j++) {
                                JSONObject currentMenuItem =menuItems.getJSONObject(j);
                                boolean isSectionTitle = currentMenuItem.getBoolean("is_section_title");
                                String name = currentMenuItem.getString("text");
                                if (isSectionTitle && !categoryNames.contains(name)) {
                                    categoryNames.add(name);
                                }
                            }
                        }
                        listener.onResponse(categoryNames);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, errorListener);
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // TODO Actually be able to request stuff, duh
}
