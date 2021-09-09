package com.codelunch.app.api;

import org.json.JSONArray;

import java.util.ArrayList;

public interface NutrisliceMultipleRequesterListener {
    public void onReceive(JSONArray successData, ArrayList<NutrisliceRequestErrorData> errors);
}
