package com.example.finalasm;

import java.util.List;

public class Category {
    private String nameCategory;
    private List<Meal> mealList;

    public Category(String nameCategory, List<Meal> mealList) {
        this.nameCategory = nameCategory;
        this.mealList = mealList;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public List<Meal> getMealList() {
        return mealList;
    }

    public void setDishList(List<Meal> dishList) {
        this.mealList = dishList;
    }
}