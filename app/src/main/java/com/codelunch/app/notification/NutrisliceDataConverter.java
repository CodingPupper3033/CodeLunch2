package com.codelunch.app.notification;

import android.content.Context;

import com.codelunch.app.R;
import com.codelunch.app.api.NutrisliceRequestErrorData;
import com.codelunch.app.settings.storage.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NutrisliceDataConverter {
    public static String requestDataToNotifString(Context context, JSONArray data) {
        StringBuffer out = new StringBuffer();

        try {
            for (int i = 0; i < data.length(); i++) { // Loop Through Schools
                JSONObject school = data.getJSONObject(i);
                String schoolName = school.getString("name");

                if (NutrisliceStorage.isSchoolEnabled(context, schoolName)) {
                    JSONArray menuData = school.getJSONArray("menus");
                    for (int j = 0; j < menuData.length(); j++) { // Loop Through Menus
                        JSONObject menu = menuData.getJSONObject(j);
                        String menuName = menu.getString("name");

                        if (NutrisliceStorage.isMenuEnabled(context, schoolName, menuName)) {
                            JSONArray categoryData = menu.getJSONArray("categories");
                            if (categoryData.length() > 0) {
                                out.append("┣━ " + schoolName + " - " + menuName + '\n');
                                for (int k = 0; k < categoryData.length(); k++) { // Loop through categories
                                    JSONObject category = categoryData.getJSONObject(k);
                                    String categoryName = category.getString("name");

                                    if (NutrisliceStorage.isCategoryEnabled(context, schoolName, menuName, categoryName)) {
                                        JSONArray menuItemsData = category.getJSONArray("menu_items");

                                        if (menuItemsData.length() > 0) {
                                            out.append("┃  " + "╠═ " + categoryName + '\n');
                                            for (int l = 0; l < menuItemsData.length(); l++) { // Loop through menu item
                                                String menuItemName = menuItemsData.getString(l);
                                                out.append("┃  " + "║  " + "├─ " + menuItemName + '\n');
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    public static String errorDataToNotifString(Context context, ArrayList<NutrisliceRequestErrorData> errors) {
        StringBuffer out = new StringBuffer();
        if (errors.size() > 0) {
            out.append("┣━ " + context.getResources().getString(R.string.notification_schools_unable) + '\n');

            for (int i = 0; i < errors.size(); i++) {
                NutrisliceRequestErrorData requestErrorData = errors.get(i);
                out.append("┃  " + "╠═ " + requestErrorData.getSchoolName() + " - " + requestErrorData.getMenuName() + '\n');
            }
        }
        return out.toString();
    }

    public static String notificationMaker(Context context, JSONArray successData, ArrayList<NutrisliceRequestErrorData> errorData) {
        return requestDataToNotifString(context, successData) + errorDataToNotifString(context, errorData);
    }
}

