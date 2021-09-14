package com.codelunch.app.api.requests;

import android.content.Context;

import com.codelunch.app.settings.storage.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NutrisliceMultipleRequesterMaker {
    public static NutrisliceMultipleRequester make(Context context) {
        NutrisliceMultipleRequester multipleRequester = new NutrisliceMultipleRequester(context);
        // Add School Menu's
        try {
            JSONArray schoolsData = NutrisliceStorage.getData(context);
            for (int i = 0; i < schoolsData.length(); i++) { // Loop through Schools
                JSONObject school = schoolsData.getJSONObject(i);
                String schoolName = school.getString("name");
                if (school.getBoolean("enabled")) {
                    JSONArray menuData = school.getJSONArray("menus");
                    for (int j = 0; j < menuData.length(); j++) { // Loop through Menus
                        JSONObject menu = menuData.getJSONObject(j);
                        String menuName = menu.getString("name");
                        if (menu.getBoolean("enabled")) {
                            multipleRequester.addRequester(schoolName,menuName);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return multipleRequester;
    }
}
