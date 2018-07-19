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

    private ArrayList<String> expCategories, newExpCategories, incCategories, newIncCategories;

    private DatabaseReference db;
    private DatabaseReference expCategoryRef, incCategoryRef;

    private CategoryManager() {
        reset();
    }

    public void reset() {
        expCategories = new ArrayList<>();
        incCategories = new ArrayList<>();
        newExpCategories = new ArrayList<>();
        newIncCategories = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        expCategoryRef = db.child("expenseCategories");
        incCategoryRef = db.child("incomeCategories");
    }


    public static CategoryManager getInstance() {
        if (instance== null) instance = new CategoryManager();
        return instance;
    }


    /**
     * get the Expense Categories in this CategoryManager
     * @return an ArrayList containing the expense categories
     */
    public ArrayList<String> getPureExpCategories() {
        return this.expCategories;
    }

    /**
     * get the Income Categories in this CategoryManager
     * @return an ArrayList containing the income categories
     */
    public ArrayList<String> getPureIncCategories() {
        return this.incCategories;
    }

    /**
     * get the Expense Categories in this CategoryManager, including dummy Categories for Spinners (empty and "Add" items)
     * @return an ArrayList containing the expense categories
     */
    public ArrayList<String> getUIExpCategories() {
        ArrayList<String> result = new ArrayList<>();
        result.add("");
        result.addAll(expCategories);
        result.add("Add");
        return result;
    }

    /**
     * get the Income Categories in this CategoryManager, including dummy Categories for Spinners (empty and "Add" items)
     * @return an ArrayList containing the income categories
     */
    public ArrayList<String> getUIIncCategories() {
        ArrayList<String> result = new ArrayList<>();
        result.add("");
        result.addAll(incCategories);
        result.add("Add");
        return result;
    }

    /**
     * get the user-added expense categories
     * @return an ArrayList containing the user-added expense categories
     */
    public ArrayList<String> getNewExpCategories() {
        return newExpCategories;
    }

    /**
     * get the user-added income categories
     * @return an ArrayList containing the user-added income categories
     */
    public ArrayList<String> getNewIncCategories() {
        return newIncCategories;
    }

    /**
     * Add a new Expense Category to the Category Manager.
     * @param category    the new Category
     * @param newCategory whether this is a new Category added by the user (in this case it will be written to Firebase as well) or it already exists (i.e. added after reading it from Firebase)
     */
    public void addExpenseCategory(String category, boolean newCategory){
        expCategories.add(category);

        boolean otherExists=false;
        if(expCategories.indexOf("Other")>-1) otherExists=true;

        if(otherExists) expCategories.remove("Other");
        Collections.sort(expCategories, String.CASE_INSENSITIVE_ORDER);
        if(otherExists) expCategories.add("Other");

        if(newCategory){
            writeExpenseCategoriestoFB(expCategories);
            newExpCategories.add(category);
        }
    }

    /**
     * Add a new Income Category to the Category Manager.
     * @param category    the new Category
     * @param newCategory whether this is a new Category added by the user (in this case it will be written to Firebase as well) or it already exists (i.e. added after reading it from Firebase)
     */
    public void addIncomeCategory(String category, boolean newCategory){
        incCategories.add(category);

        boolean otherExists=false;
        if(incCategories.indexOf("Other")>-1) otherExists=true;

        if(otherExists) incCategories.remove("Other");
        Collections.sort(incCategories, String.CASE_INSENSITIVE_ORDER);
        if(otherExists) incCategories.add("Other");

        if(newCategory){
            writeIncomeCategoriestoFB(incCategories);
            newIncCategories.add(category);
        }
    }


    /**
     * Create default expense categories and store them in Firebase
     */
    public void createDefaultExpCategories(){
        ArrayList<String> defaultExpCats = new ArrayList<>();
        defaultExpCats.addAll(Arrays.asList("Food","Household","Transportation","Health","Movie", "Beauty", "Apparel", "Party", "Gift", "Education", "Music",
                "Car","Travel", "Other"));
        Collections.sort(defaultExpCats, String.CASE_INSENSITIVE_ORDER);
        expCategories.addAll(defaultExpCats);
        writeExpenseCategoriestoFB(defaultExpCats);
    }

    /**
     * Create default income categories and store them in Firebase
     */
    public void createDefaultIncCategories(){
        ArrayList<String> defaultIncCats = new ArrayList<>();
        defaultIncCats.addAll(Arrays.asList("Salary","Bonus","Petty cash", "Stock", "Other"));
        Collections.sort(defaultIncCats, String.CASE_INSENSITIVE_ORDER);
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
