package com.codelunch.app.api.requests;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.codelunch.app.settings.storage.PastRequestStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NutrisliceMultipleRequester {
    private static final String KEY_NAME = "pos";

    private int successCount = 0;
    private JSONArray successData = new JSONArray();
    private int errorCount = 0;
    private ArrayList<NutrisliceRequestErrorData> errorData = new ArrayList<>();
    private ArrayList<NutrisliceRequester> requesters;
    private Context context;
    private RequestQueue requestQueue;

    public NutrisliceMultipleRequester(RequestQueue queue, Context context, ArrayList<NutrisliceRequester> requesters) {
        this.requesters = requesters;
        this.context = context;
        this.requestQueue = queue;
    }

    public NutrisliceMultipleRequester(Context context, ArrayList<NutrisliceRequester> requesters) {
        this(getNewQueue(context), context, requesters);
    }

    public NutrisliceMultipleRequester(Context context) {
        this(context, new ArrayList<>());
    }

    private static RequestQueue getNewQueue(Context context) {
        return Volley.newRequestQueue(context);
    }

    public void addRequester(NutrisliceRequester requester) {
        requesters.add(requester);
    }

    public void addRequester(String school, String menu) {
        addRequester(new NutrisliceRequester(requestQueue,context,school,menu));
    }

    private int hasItemWithName(JSONArray items, String name) {
        for (int i = 0; i < items.length(); i++) {
            try {
                if (name.equals(items.getJSONObject(i).getString("name"))) {
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void getFoodNow(NutrisliceMultipleRequesterListener listener) {
        getFoodFromDay(Calendar.getInstance(), listener);
    }

    public void getFoodFromDay(Calendar date, NutrisliceMultipleRequesterListener listener) {
        for (int i = 0; i < requesters.size(); i++) {
            NutrisliceRequester currentRequester = requesters.get(i);
            int pos = i;
            currentRequester.getFoodFromDay(date, new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    JSONArray JSONArrayResponse = (JSONArray) response;
                    String schoolName = currentRequester.getSchool();
                    String menuName = currentRequester.getMenu();

                    try {
                        // Add School if not yet made
                        if (hasItemWithName(successData, schoolName) == -1) {
                            JSONObject school = new JSONObject();
                            school.put("name", schoolName);
                            school.put("menus", new JSONArray());
                            school.put(KEY_NAME, pos);
                            successData.put(school);
                        }

                        // Get school
                        JSONObject school = successData.getJSONObject(hasItemWithName(successData, schoolName));

                        // Menu Data
                        JSONArray menuData = school.getJSONArray("menus");

                        // Add Menu if not yet made
                        if (hasItemWithName(menuData, menuName) == -1) {
                            JSONObject menu = new JSONObject();
                            menu.put("name", menuName);
                            menu.put("categories", new JSONArray());
                            menuData.put(menu);
                        }

                        // Menu
                        JSONObject menu = menuData.getJSONObject(hasItemWithName(menuData, menuName));
                        menu.put("categories", response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    successCount++;
                    sendBackData(listener);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NutrisliceRequestErrorData requestError = new NutrisliceRequestErrorData(error, currentRequester.getSchool(), currentRequester.getMenu());
                    errorData.add(requestError);
                    errorCount++;
                    sendBackData(listener);
                }
            });
        }
        if (requesters.size() == 0) sendBackData(listener); // Nothing to request so send now
    }

    private void sendBackData(NutrisliceMultipleRequesterListener listener) {
        if (successCount + errorCount >= requesters.size()) {
            sortSuccessData();
            PastRequestStorage.add(context, successData, errorData); // Add to storage
            listener.onReceive(successData, errorData);
        }
    }

    private void sortSuccessData() {
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < successData.length(); i++) {
            try {
                list.add(successData.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(list, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID

            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                try {
                    return Integer.compare(lhs
                            .getInt(KEY_NAME), rhs.getInt(KEY_NAME));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        successData = new JSONArray(list);

        for (int i = 0; i < successData.length(); i++) {
            try {
                successData.getJSONObject(i).remove(KEY_NAME);
            } catch (JSONException e) {
            }
        }
    }
}
