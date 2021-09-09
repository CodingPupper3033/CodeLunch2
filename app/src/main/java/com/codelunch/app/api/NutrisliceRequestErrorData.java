package com.codelunch.app.api;

import com.android.volley.VolleyError;

public class NutrisliceRequestErrorData {
    private VolleyError error;
    private String schoolName;
    private String menuName;

    public NutrisliceRequestErrorData(VolleyError error, String schoolName, String menuName) {
        this.error = error;
        this.schoolName = schoolName;
        this.menuName = menuName;
    }

    public VolleyError getError() {
        return error;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getMenuName() {
        return menuName;
    }
}
