package com.codelunch.app.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codelunch.app.settings.storage.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NutrisliceRequester {
    private Context context;
    private String school;
    private String menu;
    RequestQueue queue;

    public String getSchool() {
        return school;
    }

    public String getMenu() {
        return menu;
    }

    public NutrisliceRequester(RequestQueue queue, Context context, String school, String menu) {
        this.context = context;
        this.school = school;
        this.menu = menu;
        this.queue = queue;
    }

    public NutrisliceRequester(Context context, String school, String menu) {
        this(Volley.newRequestQueue(context), context, school, menu);
    }

    public String getDomain() throws JSONException {
        return NutrisliceStorage.getSchool(context, school).getString("menus_domain");
    }

    public String getFullMenuURL() throws JSONException {
        return NutrisliceStorage.getMenu(context, school, menu).getJSONObject("urls").getString("full_menu_by_date_api_url_template");
    }

    public String getFullURL(Calendar date) throws JSONException {
        String fullUrl = "https://" + getDomain() + getFullMenuURL();
        fullUrl = fullUrl
                .replace("{year}", String.valueOf(date.get(Calendar.YEAR)))
                .replace("{month}", String.valueOf(date.get(Calendar.MONTH)+1))
                .replace("{day}", String.valueOf(date.get(Calendar.DAY_OF_MONTH)));
        return fullUrl;
    }

    private String getDateFormatted(Calendar date) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(date.getTime());
    }

    public void getCategoryList(Calendar date, Response.Listener listener, Response.ErrorListener errorListener) {
        try {
            String fullUrl = getFullURL(date);

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

    private JSONArray convertAPIDataToCategoryList(JSONObject day) {
        try {
            JSONArray output = new JSONArray();

            JSONArray menuItems = day.getJSONArray("menu_items");

            // Loop through menu Items
            for (int i = 0; i < menuItems.length(); i++) {
                JSONObject currentMenuItem = menuItems.getJSONObject(i);

                if (currentMenuItem.getBoolean("is_section_title")) { // If it is a section header
                    JSONObject category = new JSONObject();
                    category.put("name", currentMenuItem.getString("text"));
                    category.put("menu_items", new JSONArray());
                    // Append category to storage
                    NutrisliceStorage.forceAddCategory(context, school, menu, currentMenuItem.getString("text"));

                    output.put(category);
                } else if (!currentMenuItem.getBoolean("is_holiday") && !currentMenuItem.getBoolean("is_station_header") && !currentMenuItem.getBoolean("blank_line")) { // If it is an actual food
                    // Get food name
                    String foodName = "";
                    if (currentMenuItem.isNull("food"))
                        foodName = currentMenuItem.getString("text");
                    else
                        foodName = currentMenuItem.getJSONObject("food").getString("name");

                    // Add it to the most recent category
                    if (output.length() > 0) {
                        JSONObject outCategory = output.getJSONObject(output.length() - 1);
                        JSONArray outMenuItems = outCategory.getJSONArray("menu_items");
                        outMenuItems.put(foodName);
                        outCategory.put("menu_items", outMenuItems);
                        output.put(output.length() - 1, outCategory);
                    }
                }
            }
            return output;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getFoodFromDay(Calendar date, Response.Listener listener, Response.ErrorListener errorListener) {
        try {
            String fullUrl = getFullURL(date);
            String daySearching = getDateFormatted(date);
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray days = response.getJSONArray("days");
                        JSONObject currentDay = new JSONObject();
                        for (int i = 0; i < days.length(); i++) {
                            if (days.getJSONObject(i).getString("date").equals(daySearching)) {
                                currentDay = days.getJSONObject(i);
                            }
                        }

                        listener.onResponse(convertAPIDataToCategoryList(currentDay));
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
}
