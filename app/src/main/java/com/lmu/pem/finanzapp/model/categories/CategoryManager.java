package com.lmu.pem.finanzapp.model.categories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;


public class CategoryManager {

    private static CategoryManager instance;

    private ArrayList<String> expCategories;
    private ArrayList<String> incCategories;

    private DatabaseReference db;
    private DatabaseReference catDbRef, expCategoryRef, incCategoryRef;

    public CategoryManager() {
        expCategories = new ArrayList<>();
        incCategories = new ArrayList<>();
        expCategories.add(""); // dummy element
        incCategories.add("");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        catDbRef = db.child("categories");

        expCategoryRef = db.child("expenseCategories");
        incCategoryRef = db.child("incomeCategories");

        //expCategories.addAll(createDefaultExpCategories());
        //incCategories.addAll(createDefaultIncCategories());


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


    public void addExpenseCategory(String category, boolean writeToFB){
        this.expCategories.add(category);
        this.expCategories.remove("Other");
        this.expCategories.remove("Other");  // kann zweimal in der Liste stehen
        this.expCategories.remove("Add");
        this.expCategories.remove("Add");  // kann zweimal in der Liste stehen
        this.expCategories.remove("");
        this.expCategories.remove(""); // kann zweimal in der Liste stehen

        this.expCategories.add("");
        Collections.sort(expCategories, String.CASE_INSENSITIVE_ORDER);
        this.expCategories.add("Other");
        this.expCategories.add("Add");
        if(writeToFB) writeExpenseCategoriestoFB(this.expCategories);
    }

    public void addIncomeCategory(String category, boolean writeToFB){
        incCategories.add(category);
        incCategories.remove("Other");
        incCategories.remove("Add");
        incCategories.remove("Other");  // kann zweimal in der Liste stehen
        incCategories.remove("Add");  // kann zweimal in der Liste stehen
        incCategories.remove("");
        incCategories.remove(""); // kann zweimal in der Liste stehen

        incCategories.add("");
        Collections.sort(incCategories, String.CASE_INSENSITIVE_ORDER);
        incCategories.add("Other");
        incCategories.add("Add");
        if(writeToFB) writeIncomeCategoriestoFB(incCategories);
    }


    /**
     * A temporäre Methode (TODO: retrieving an arraylist from xml file or from database)
     * @return
     */
    public ArrayList<String> createDefaultExpCategories(){
        ArrayList<String> defaultExpCats = new ArrayList<>();
        defaultExpCats.addAll(Arrays.asList("Food","Household","Transportation","Health","Movie", "Beauty", "Apparel", "Party", "Gift", "Education", "Music",
                "Car","Travel", "Other", "Add"));
        for (String cat : defaultExpCats) {
            this.addExpenseCategory(cat, false);
        }
        writeExpenseCategoriestoFB(defaultExpCats);
        return defaultExpCats;
    }

    public ArrayList<String> createDefaultIncCategories(){
        ArrayList<String> defaultIncCats = new ArrayList<>();
        defaultIncCats.addAll(Arrays.asList("Salary","Bonus","Petty cash", "Stock", "Other", "Add"));
        for (String cat : defaultIncCats) {
            this.addIncomeCategory(cat, false);
        }
        writeIncomeCategoriestoFB(defaultIncCats);
        return defaultIncCats;
    }


    public String writeExpenseCategoriestoFB(ArrayList<String> cate){
        String key = expCategoryRef.push().getKey();
        expCategoryRef.setValue(cate);
        return key;
    }

    public String writeIncomeCategoriestoFB(ArrayList<String> cat){
        String key = incCategoryRef.push().getKey();
        incCategoryRef.setValue(cat);
        return key;

    }

    public void updateCategory(String key, String newCat){

    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("expenseCategories", expCategories);
        result.put("incomeCategories", incCategories);
        return result;
    }


}
