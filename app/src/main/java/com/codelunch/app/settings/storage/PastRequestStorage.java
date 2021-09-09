package com.codelunch.app.settings.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.codelunch.app.api.NutrisliceRequestErrorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PastRequestStorage {
    public static final String STORAGE_KEY = "pastRequestData";
    public static final String MAX_STORE_KEY = "max_previous_request_store";
    public static final SimpleDateFormat SAVE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static JSONArray getAllData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dataString = sharedPreferences.getString(STORAGE_KEY,"[]");
        try {
            return new JSONArray(dataString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static void setData(Context context, JSONArray data) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORAGE_KEY, data.toString());
        editor.apply();
    }

    public static void resetData(Context context) {
        setData(context, new JSONArray());
    }

    public static int getMaxStore(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dataString = sharedPreferences.getString(MAX_STORE_KEY,"4");
        return Integer.parseInt(dataString);
    }

    public static int getRequestsStored(Context context) {
        return getAllData(context).length();
    }

    public static void remove(Context context) {
        remove(context, 0);
    }

    public static boolean remove(Context context, int pos){
        if (getRequestsStored(context) > pos) { // pos exists
            JSONArray data = getAllData(context);

            // Convert to list as that will not cause problems
            ArrayList<String> dataArrayList = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                try {
                    dataArrayList.add(data.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            dataArrayList.remove(pos);
            data = new JSONArray(dataArrayList);
            setData(context, data);
            return true;
        }
        return false;
    }

    public static boolean add(Context context, JSONArray data, ArrayList<NutrisliceRequestErrorData> errors) {
        return add(context, Calendar.getInstance(), data, errors);
    }

    // WARNING Does not put the requests in order, it assumes it already is in order
    public static boolean add(Context context, Calendar time, JSONArray data, ArrayList<NutrisliceRequestErrorData> errors) {
        boolean makeRoom = getRequestsStored(context) == getMaxStore(context);
        if (makeRoom) {
            remove(context);
        }

        JSONObject addObject = new JSONObject();

        try {
            addObject.put("data", data);
            addObject.put("errors", new JSONArray(errors));
            addObject.put("time", SAVE_DATE_FORMAT.format(time.getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setData(context, getAllData(context).put(addObject));

        return makeRoom;
    }

    public static JSONObject get(Context context, int pos) {
        try {
            return getAllData(context).getJSONObject(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject get(Context context) {
        return get(context, getRequestsStored(context)-1);
    }

    public static JSONArray getData(Context context, int pos) {
        try {
            return get(context, pos).getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getData(Context context) {
        return getData(context, getRequestsStored(context)-1);
    }

    public static Calendar getTime(Context context, int pos) {
        Calendar out = Calendar.getInstance();
        try {
            String timeString = get(context, pos).getString("time");
            Log.d("TAG", "getTime: " + timeString);
            out.setTime(SAVE_DATE_FORMAT.parse(timeString));
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        return out;
    }

    public static Calendar getTime(Context context) {
        return getTime(context, getRequestsStored(context)-1);
    }

}
