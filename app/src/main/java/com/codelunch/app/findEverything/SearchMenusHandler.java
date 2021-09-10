package com.codelunch.app.findEverything;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.codelunch.app.R;
import com.codelunch.app.api.NutrisliceFinder;
import com.codelunch.app.settings.storage.NutrisliceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchMenusHandler {
    private final NutrisliceFinder finder;
    private final Activity activity;
    private final JSONObject object;

    private final String menus_domain;
    private final String schoolName;
    private final String slug;

    public SearchMenusHandler(Activity activity, NutrisliceFinder finder, String menus_domain, JSONObject item) throws JSONException {
        this.activity = activity;
        this.object = item;
        this.finder = finder;

        this.menus_domain = menus_domain;
        this.schoolName = object.getString("name");
        this. slug = object.getString("slug");

        NutrisliceStorage.addSchool(activity, schoolName, menus_domain, slug);

        // Add Days the menu is enabled
        JSONObject schoolObject = NutrisliceStorage.getSchool(activity,schoolName);
        String[] enables_to_add = {"mon_enabled","tue_enabled","wed_enabled","thu_enabled","fri_enabled","sat_enabled","sun_enabled"};
        for (int i = 0; i < enables_to_add.length; i++) {
            String enableNow = enables_to_add[i];
            schoolObject.put(enableNow, object.getBoolean(enableNow));
        }
        NutrisliceStorage.setSchool(activity, schoolObject);


        JSONArray menusArray = object.getJSONArray("active_menu_types");
        String[] menuNames = convertDataToNameList(menusArray);
        boolean[] enabled = new boolean[menusArray.length()];

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(schoolName); // Set Title
        builder.setMultiChoiceItems(menuNames, enabled, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                enabled[which] = isChecked;
            }
        });

        builder.setPositiveButton(R.string.continueText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    // Add Found Menus
                    for (int i = 0; i < menusArray.length(); i++) {
                        JSONObject menuObject = menusArray.getJSONObject(i);
                        String menuName = menuObject.getString("name");
                        JSONObject urls = menuObject.getJSONObject("urls");
                        boolean thisEnabled = enabled[i];

                        // Add to Nutrislice storage
                        NutrisliceStorage.addMenu(activity, schoolName, menuName, urls, thisEnabled);

                        if (thisEnabled) {
                            // Make show categories
                            SearchCategoriesHandler searchCategoriesHandler = new SearchCategoriesHandler(activity, finder, schoolName, menuName);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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
