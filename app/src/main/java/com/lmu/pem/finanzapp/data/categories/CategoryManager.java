package com.lmu.pem.finanzapp.data.categories;

import java.util.ArrayList;


public class CategoryManager {

    private static CategoryManager instance;

    private final Category[] DEFAULTCATEGORIES = DefaultCategory.values();

    private ArrayList<CustomCategory> customCategories;




    public static CategoryManager getInstance() {
        if (instance== null) instance = new CategoryManager();
        return instance;
    }

    public Category[] getAllCategories() {
        Category[] buffer = new Category[DEFAULTCATEGORIES.length + customCategories.size()];

        for (int i = 0; i < DEFAULTCATEGORIES.length; i++) {
            buffer [i] = DEFAULTCATEGORIES[i];
        }
        for (int i = 0; i < customCategories.size(); i++) {
            buffer[i] = customCategories.get(i);
        }

        return buffer;
    }



}
