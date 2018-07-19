package com.lmu.pem.finanzapp.model.transactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lmu.pem.finanzapp.R;


public class CategoryManager {

    private static CategoryManager instance;

    private ArrayList<String> expCategories;
    private ArrayList<String> incCategories;

    private DatabaseReference db;
    private DatabaseReference expCategoryRef, incCategoryRef;

    private CategoryManager() {
        reset();
    }

    public void reset() {
        expCategories = new ArrayList<>();
        incCategories = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        expCategoryRef = db.child("expenseCategories");
        incCategoryRef = db.child("incomeCategories");
    }


    public static CategoryManager getInstance() {
        if (instance== null) instance = new CategoryManager();
        return instance;
    }


    public ArrayList<String> getPureExpCategories() {
        return this.expCategories;
    }
    public ArrayList<String> getPureIncCategories() {
        return this.incCategories;
    }

    public ArrayList<String> getUIExpCategories() {
        ArrayList<String> result = new ArrayList<>();
        result.add("");
        result.addAll(expCategories);
        result.add("Add");
        return result;
    }
    public ArrayList<String> getUIIncCategories() {
        ArrayList<String> result = new ArrayList<>();
        result.add("");
        result.addAll(incCategories);
        result.add("Add");
        return result;
    }

    public void addExpenseCategory(String category, boolean writeToFB){
        this.expCategories.add(category);
        this.expCategories.remove("Other");

        Collections.sort(expCategories, String.CASE_INSENSITIVE_ORDER);
        this.expCategories.add("Other");
        if(writeToFB) writeExpenseCategoriestoFB(this.expCategories);
    }

    public void addIncomeCategory(String category, boolean writeToFB){
        incCategories.add(category);
        incCategories.remove("Other");

        Collections.sort(incCategories, String.CASE_INSENSITIVE_ORDER);
        incCategories.add("Other");
        if(writeToFB) writeIncomeCategoriestoFB(incCategories);
    }


    /**
     * Create default expense categories and store them in Firebase
     */
    public void createDefaultExpCategories(){
        ArrayList<String> defaultExpCats = new ArrayList<>();
        defaultExpCats.addAll(Arrays.asList("Food","Household","Transportation","Health","Movie", "Beauty", "Apparel", "Party", "Gift", "Education", "Music",
                "Car","Travel"));
        Collections.sort(defaultExpCats, String.CASE_INSENSITIVE_ORDER);
        defaultExpCats.add("Other");
        expCategories.addAll(defaultExpCats);
        writeExpenseCategoriestoFB(defaultExpCats);
    }

    /**
     * Create default income categories and store them in Firebase
     */
    public void createDefaultIncCategories(){
        ArrayList<String> defaultIncCats = new ArrayList<>();
        defaultIncCats.addAll(Arrays.asList("Salary","Bonus","Petty cash", "Stock"));
        Collections.sort(defaultIncCats, String.CASE_INSENSITIVE_ORDER);
        defaultIncCats.add("Other");
        incCategories.addAll(defaultIncCats);
        writeIncomeCategoriestoFB(defaultIncCats);
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

}
