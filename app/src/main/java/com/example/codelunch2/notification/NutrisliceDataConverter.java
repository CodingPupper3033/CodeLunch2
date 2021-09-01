package com.example.codelunch2.notification;

import android.content.Context;

import com.example.codelunch2.api.NutrisliceRequestErrorData;
import com.example.codelunch2.settings.storage.NutrisliceStorage;

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
                    for (int j = 0; j < menuData.length(); j++) {
                        JSONObject menu = menuData.getJSONObject(j);
                        String menuName = menu.getString("name");

                        if (NutrisliceStorage.isMenuEnabled(context, schoolName, menuName)) {
                            out.append("┣━ " + schoolName + " - " + menuName + '\n');
                            JSONArray categoryData = menu.getJSONArray("categories");
                            for (int k = 0; k < categoryData.length(); k++) {
                                JSONObject category = categoryData.getJSONObject(k);
                                String categoryName = category.getString("name");

                                if (NutrisliceStorage.isCategoryEnabled(context, schoolName, menuName, categoryName)) {
                                    out.append("┃  " + "╠═ " + categoryName + '\n');
                                    JSONArray menuItemsData = category.getJSONArray("menu_items");
                                    for (int l = 0; l < menuItemsData.length(); l++) {
                                        // TODO Items inside category shown or just category
                                        String menuItemName = menuItemsData.getString(l);
                                        out.append("┃  " + "║  " + "├─ " + menuItemName + '\n');
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

    public static String errorDataToNotifString(ArrayList<NutrisliceRequestErrorData> errors) {
        StringBuffer out = new StringBuffer();
        if (errors.size() > 0) {
            out.append("┣━ " + "Schools unable to get menu from" + '\n'); // TODO From @R.String

            for (int i = 0; i < errors.size(); i++) {
                NutrisliceRequestErrorData requestErrorData = errors.get(i);
                out.append("┃  " + "╠═ " + requestErrorData.getSchoolName() + " - " + requestErrorData.getMenuName() + '\n');
            }
        }
        return out.toString();
    }

    public static String notificationMaker(Context context, JSONArray successData, ArrayList<NutrisliceRequestErrorData> errorData) {
        return requestDataToNotifString(context, successData) + errorDataToNotifString(errorData);
    }
}

