package com.example.codelunch2.api;

import java.util.ArrayList;

public class NutrisliceRequester {
    private String school;
    private String menu;

    public String getSchool() {
        return school;
    }

    public String getMenu() {
        return menu;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    private ArrayList<String> categories;

    public NutrisliceRequester(String school, String menu, ArrayList<String> categories) {
        this.school = school;
        this.menu = menu;
        this.categories = categories;
    }

    public NutrisliceRequester(String school, String menu) {
        this(school, menu, new ArrayList<String>());
    }

    public boolean addCategory(String category) {
        boolean found = false;
        for (String categoryTest : categories) {
            if (categoryTest.equals(category)) {
                found = true;
                break;
            }
        }
        if (!found) {
            categories.add(category);
        }
        return !found;
    }

    public boolean removeCategory(String category) {
        boolean found = false;
        for (String categoryTest : categories) {
            if (categoryTest.equals(category)) {
                categories.remove(categoryTest);
                found = true;
                break;
            }
        }
        return found;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    // TODO Actually be able to request stuff, duh
}
