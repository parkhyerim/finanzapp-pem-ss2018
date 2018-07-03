package com.lmu.pem.finanzapp.data.categories;

import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CategoryManager {

    private static CategoryManager instance;

    private final Category[] DEFAULTCATEGORIES = DefaultCategory.values();
    private ArrayList<CustomCategory> customCategories;
    private ArrayList<String> defaultCategories;


    private ArrayList<String> expCategories;
    private ArrayList<String> incCategories;

    private DatabaseReference db;

    public CategoryManager() {
        db = FirebaseDatabase.getInstance().getReference().child("categories");
        expCategories = new ArrayList<>();
        incCategories = new ArrayList<>();
        expCategories.addAll(createDefaultExpCategories());
        incCategories.addAll(createDefaultIncCategories());
    }




    public static CategoryManager getInstance() {
        if (instance== null) instance = new CategoryManager();
        return instance;
    }


    public ArrayList<String> getExpCategories() {
        return this.expCategories;
    }
    public ArrayList<String> getIncCategories() {
        return this.incCategories;
    }

    /*
    public String[] getCategoryArray(){
        String[] result = new String[categories.size()];
        ArrayList<String> list = new ArrayList<>(categories.size());
        for(String c: categories){
            list.add(c);
        }
        return list.toArray(result);
    }
    */


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

    public void addExpCategory(String newCat){
        expCategories.add(newCat);

        /*
        String key = db.child("expense").push().getKey();
        db.push().setValue(newCat);
        */
    }


    /**
     * A temporäre Methode (TODO: retrieving an arraylist from xml file or from database)
     * @return
     */
    public ArrayList<String> createDefaultExpCategories(){
        ArrayList<String> defaultExpCats = new ArrayList<>();
        defaultExpCats.addAll(Arrays.asList("", "Food","Household","Transportation","Health","Movie", "Beauty", "Apparel", "Party", "Gift", "Education", "Music",
                "Car","Travel", "Other","Add"));
        return defaultExpCats;
    }

    public ArrayList<String> createDefaultIncCategories(){
        ArrayList<String> defaultIncCats = new ArrayList<>();
        defaultIncCats.addAll(Arrays.asList("","Salary","Bonus","Petty cash", "Stock","Other","Add"));
        return defaultIncCats;
    }




}
