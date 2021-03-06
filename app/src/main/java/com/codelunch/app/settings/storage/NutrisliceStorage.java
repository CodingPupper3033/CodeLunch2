package com.codelunch.app.settings.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NutrisliceStorage {
    public static final String STORAGE_KEY = "nutrisliceData";
    public static final String AUTO_ENABLE_SCHOOL_KEY = "autoEnableSchool";
    public static final String AUTO_ENABLE_MENU_KEY = "autoEnableMenu";
    public static final String AUTO_ENABLE_CATEGORY_KEY = "autoEnableCategory";

    /*
        DATA
     */
    public static JSONArray getData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dataString = sharedPreferences.getString(STORAGE_KEY,"[]");
        try {
            return new JSONArray(dataString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static boolean isEmpty(Context context) {
        return getData(context).length() == 0;
    }

    public static void resetData(Context context) {
        setData(context, new JSONArray());
    }

    public static void setData(Context context, JSONArray data) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORAGE_KEY, data.toString());
        editor.apply();
    }


    /*
        NutriObject
     */
    private static boolean hasNutriObject(JSONArray data, String name) {
        for (int i = 0; i < data.length(); i++) {
            try {
                if (name.equals(data.getJSONObject(i).getString("name"))) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static JSONObject getNutriObjectWithName(JSONArray data, String name) {
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject currentSchool = data.getJSONObject(i);
                if (name.equals(currentSchool.getString("name"))) {
                    return currentSchool;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private static JSONObject makeBlankNutriObject(String name, boolean enabled) {
        JSONObject nutriObject = new JSONObject();
        try {
            nutriObject.put("name",name);
            nutriObject.put("enabled", enabled);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return nutriObject;
    }

    private static boolean isNutriObjectEnabled(JSONArray data, String name) {
        JSONObject nutriObject = getNutriObjectWithName(data, name);
        try {
            return nutriObject.getBoolean("enabled");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static JSONArray moveNutriObject(JSONArray data, int posFrom, int posTo) {
        try {
            JSONObject dataMoving = data.getJSONObject(posFrom);

            // Delete Pos
            ArrayList<JSONObject> list = new ArrayList<JSONObject>();
            for (int i = 0; i < data.length(); i++) {
                try {
                    list.add(data.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            list.remove(posFrom);
            list.add(posTo, dataMoving);

            return new JSONArray(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }


    /*
        School
     */
    public static boolean hasSchool(Context context, String name) {
        JSONArray data = getData(context);
        return hasNutriObject(data, name);
    }

    public static JSONObject getSchool(Context context, String name) {
        JSONArray data = getData(context);
        return getNutriObjectWithName(data, name);
    }

    public static void setSchool(Context context, JSONObject school) {
        JSONArray data = getData(context);
        for (int i = 0; i < data.length(); i++) {
            try {
                if (school.getString("name").equals(data.getJSONObject(i).getString("name"))) {
                    data.put(i, school);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setData(context, data);
    }

    public static void moveSchool(Context context, int posFrom, int posTo) {
        setData(context, moveNutriObject(getData(context), posFrom, posTo));
    }

    public static void deleteSchool(Context context, int pos) {
        JSONArray data = getData(context);

        // Delete Pos
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < data.length(); i++) {
            try {
                list.add(data.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        list.remove(pos);

        data = new JSONArray(list);

        setData(context, data);
    }

    public static boolean addSchool(Context context, String name) {
        JSONArray data = getData(context);
        if (!hasSchool(context, name)) {
            JSONObject school = makeBlankNutriObject(name, true);
            try {
                school.put("menus", new JSONArray());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data.put(school);
            setData(context, data);
            return true;
        }
        return false;
    }

    public static void addSchoolDomain(Context context, String name, String url) {
        JSONObject schoolObject = getSchool(context, name);
        try {
            schoolObject.put("menus_domain",url);
            setSchool(context, schoolObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addSchoolSlug(Context context, String name, String slug) {
        JSONObject schoolObject = getSchool(context, name);
        try {
            schoolObject.put("slug",slug);
            setSchool(context, schoolObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addSchool(Context context, String name, String url, String slug) {
        addSchool(context, name);
        addSchoolDomain(context, name, url);
        addSchoolSlug(context, name, slug);
    }

    public static boolean isSchoolEnabled(Context context, String name) {
        JSONArray data = getData(context);
        return isNutriObjectEnabled(data, name);
    }

    public static void setSchoolEnabled(Context context, String name, boolean enabled) {
        JSONObject school = getSchool(context, name);
        try {
            school.put("enabled", enabled);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setSchool(context, school);
    }


    /*
        Menu
     */
    public static void setMenuData(Context context, String school, JSONArray data) {
        try {
            JSONObject schoolObject = getSchool(context, school);
            schoolObject.put("menus", data);
            setSchool(context, schoolObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getMenuData(Context context, String school) {
        JSONObject schoolObject = getSchool(context, school);
        try {
            return schoolObject.getJSONArray("menus");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static JSONObject getMenu(Context context, String school, String name) {
        try {
            JSONArray data = getSchool(context, school).getJSONArray("menus");
            return getNutriObjectWithName(data, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static boolean hasMenu(Context context, String school, String name) {
        JSONArray data = getMenuData(context, school);
        return hasNutriObject(data, name);
    }

    public static boolean addMenu(Context context, String school, String name) {
        JSONArray data = getMenuData(context, school);
        if (!hasMenu(context, school, name)) {
            JSONObject menu = makeBlankNutriObject(name, true);
            try {
                menu.put("categories", new JSONArray());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data.put(menu);
            setMenuData(context, school, data);
            return true;
        }
        return false;
    }

    public static void addMenu(Context context, String school, String name, JSONObject urls, boolean enabled) {
        addMenu(context, school, name);
        addMenuURLS(context, school, name, urls);
        setMenuEnabled(context, school, name, enabled);
    }

    public static void addMenuURLS(Context context, String school, String name, JSONObject urls) {
        JSONObject menuObject = getMenu(context, school, name);
        try {
            menuObject.put("urls",urls);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setMenu(context, school, menuObject);
    }

    public static void forceAddMenu(Context context, String school, String name) {
        if (!hasSchool(context, school)) {
            addSchool(context, school);
        }
        addMenu(context, school, name);
    }

    public static void setMenu(Context context, String school, JSONObject menu) {
        JSONArray data = getMenuData(context, school);
        for (int i = 0; i < data.length(); i++) {
            try {
                if (menu.getString("name").equals(data.getJSONObject(i).getString("name"))) {
                    data.put(i, menu);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setMenuData(context, school, data);
    }

    public static void moveMenu(Context context, String school, int posFrom, int posTo) {
        setMenuData(context, school, moveNutriObject(getMenuData(context, school), posFrom, posTo));
    }

    public static boolean isMenuEnabled(Context context, String school, String name) {
        JSONArray data = getMenuData(context, school);
        return isNutriObjectEnabled(data, name);
    }

    public static void setMenuEnabled(Context context, String school, String name, boolean enabled) {
        JSONObject menu = getMenu(context, school, name);
        try {
            menu.put("enabled", enabled);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setMenu(context, school, menu);

    }


    /*
        Category
     */
    public static void setCategoryData(Context context, String school, String menu, JSONArray data) {
        try {
            JSONObject menuObject = getMenu(context, school, menu);
            menuObject.put("categories", data);
            setMenu(context, school, menuObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getCategoryData(Context context, String school, String menu) {
        JSONObject menuObject = getMenu(context, school, menu);
        try {
            return menuObject.getJSONArray("categories");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static JSONObject getCategory(Context context, String school, String menu, String name) {
        try {
            JSONArray data = getMenu(context, school, menu).getJSONArray("categories");
            return getNutriObjectWithName(data, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static boolean hasCategory(Context context, String school, String menu, String name) {
        JSONArray data = getCategoryData(context, school, menu);
        return hasNutriObject(data, name);
    }

    public static boolean addCategory(Context context, String school, String menu, String name) {
        JSONArray data = getCategoryData(context, school, menu);
        if (!hasCategory(context, school, menu, name)) {
            JSONObject category = makeBlankNutriObject(name, false);
            data.put(category);
            setCategoryData(context, school, menu, data);
            return true;
        }
        return false;
    }

    public static void addCategory(Context context, String school, String menu, String name, boolean enabled) {
        addCategory(context, school, menu, name);
        setCategoryEnabled(context, school, menu, name, enabled);
    }

    public static void forceAddCategory(Context context, String school, String menu, String name) {
        if (!hasMenu(context, school, menu)) {
            forceAddMenu(context, school, menu);
        }
        addCategory(context, school, menu, name);
    }

    public static void setCategory(Context context, String school, String menu, JSONObject category) {
        JSONArray data = getCategoryData(context, school, menu);
        for (int i = 0; i < data.length(); i++) {
            try {
                if (category.getString("name").equals(data.getJSONObject(i).getString("name"))) {
                    data.put(i, category);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setCategoryData(context, school, menu, data);
    }

    public static void moveCategory(Context context, String school, String menu, int posFrom, int posTo) {
        setCategoryData(context, school, menu, moveNutriObject(getCategoryData(context, school, menu), posFrom, posTo));
    }

    public static void deleteCategory(Context context, String school, String menu, int pos) {
        JSONArray data = getCategoryData(context, school, menu);

        // Delete Pos
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < data.length(); i++) {
            try {
                list.add(data.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        list.remove(pos);

        data = new JSONArray(list);

        setCategoryData(context, school, menu, data);
    }

    public static boolean isCategoryEnabled(Context context, String school, String menu, String name) {
        JSONArray data = getCategoryData(context, school, menu);
        return isNutriObjectEnabled(data, name);
    }

    public static void setCategoryEnabled(Context context, String school, String menu, String name, boolean enabled) {
        JSONObject category = getCategory(context, school, menu, name);
        try {
            category.put("enabled", enabled);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCategory(context, school, menu, category);
    }
}
