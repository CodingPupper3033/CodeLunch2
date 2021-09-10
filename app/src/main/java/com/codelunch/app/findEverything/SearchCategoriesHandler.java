package com.codelunch.app.findEverything;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codelunch.app.R;
import com.codelunch.app.api.NutrisliceFinder;
import com.codelunch.app.settings.storage.NutrisliceStorage;

import java.util.ArrayList;

public class SearchCategoriesHandler implements Response.Listener, Response.ErrorListener {
    private final Activity activity;
    private final NutrisliceFinder finder;
    private final String schoolName;
    private final String menuName;

    public SearchCategoriesHandler(Activity activity, NutrisliceFinder finder, String schoolName, String menuName) {
        this.activity = activity;
        this.finder = finder;

        this.schoolName = schoolName;
        this.menuName = menuName;

        finder.makeCategoryRequest(schoolName, menuName, this, this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // Todo
    }

    @Override
    public void onResponse(Object response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(menuName + ':');

        ArrayList<String> categoriesArrayList = (ArrayList<String>)response;
        String[] categoriesOutline = new String[categoriesArrayList.size()];
        String[] categories = categoriesArrayList.toArray(categoriesOutline);

        boolean[] enabled = new boolean[categories.length];
        builder.setMultiChoiceItems(categories, enabled, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                enabled[which] = isChecked;
            }
        });

        builder.setPositiveButton(R.string.continueText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < categories.length; i++) {
                    NutrisliceStorage.addCategory(activity, schoolName, menuName, categories[i], enabled[i]);
                }
            }
        });

        builder.show();
    }
}
