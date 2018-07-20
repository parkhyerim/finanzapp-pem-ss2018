package com.lmu.pem.finanzapp.model.transactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CategoryManager {

    private static CategoryManager instance;

    private ArrayList<String> expCategories, incCategories;

    private DatabaseReference db;
    private DatabaseReference expCategoryRef, incCategoryRef;

    private CategoryManager() {
        reset();
    }

    public void reset() {
        if (expCategories != null)
            expCategories.clear();
        else
            expCategories = new ArrayList<>();

        if (incCategories != null)
            incCategories.clear();
        else
            incCategories = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        expCategoryRef = db.child("expenseCategories");
        incCategoryRef = db.child("incomeCategories");
    }


    public static CategoryManager getInstance() {
        if (instance == null) instance = new CategoryManager();
        return instance;
    }


    /**
     * get the Expense Categories in this CategoryManager
     *
     * @return an ArrayList containing the expense categories
     */
    public ArrayList<String> getPureExpCategories() {
        return this.expCategories;
    }

    /**
     * get the Income Categories in this CategoryManager
     *
     * @return an ArrayList containing the income categories
     */
    public ArrayList<String> getPureIncCategories() {
        return this.incCategories;
    }

    /**
     * get the Expense Categories in this CategoryManager, including dummy Categories for Spinners (empty and "Add" items)
     *
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
     *
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
     * Add a new Expense Category to the Category Manager.
     *
     * @param category    the new Category
     * @param newCategory whether this is a new Category added by the user (in this case it will be written to Firebase as well) or it already exists (i.e. added after reading it from Firebase)
     */
    public void addExpenseCategory(String category, boolean newCategory) {
        expCategories.add(category);

        boolean otherExists = false;
        if (expCategories.indexOf("Other") > -1) otherExists = true;

        if (otherExists) expCategories.remove("Other");
        Collections.sort(expCategories, String.CASE_INSENSITIVE_ORDER);
        if (otherExists) expCategories.add("Other");

        if (newCategory) {
            writeExpenseCategoriesToFB();
        }
    }

    /**
     * Add a new Income Category to the Category Manager.
     *
     * @param category    the new Category
     * @param newCategory whether this is a new Category added by the user (in this case it will be written to Firebase as well) or it already exists (i.e. added after reading it from Firebase)
     */
    public void addIncomeCategory(String category, boolean newCategory) {
        incCategories.add(category);

        boolean otherExists = false;
        if (incCategories.indexOf("Other") > -1) otherExists = true;

        if (otherExists) incCategories.remove("Other");
        Collections.sort(incCategories, String.CASE_INSENSITIVE_ORDER);
        if (otherExists) incCategories.add("Other");

        if (newCategory) {
            writeIncomeCategoriesToFB();
        }
    }

    /**
     * Create default expense categories and store them in Firebase
     */
    public void createDefaultExpCategories() {
        expCategories.addAll(Arrays.asList("Food", "Household", "Transportation", "Health", "Movie", "Beauty", "Apparel", "Party", "Gift", "Education", "Music",
                "Car", "Travel"));
        Collections.sort(expCategories, String.CASE_INSENSITIVE_ORDER);
        expCategories.add("Other");
        writeExpenseCategoriesToFB();
    }

    /**
     * Create default income categories and store them in Firebase
     */
    public void createDefaultIncCategories() {
        incCategories.addAll(Arrays.asList("Salary", "Bonus", "Petty cash", "Stock"));
        Collections.sort(incCategories, String.CASE_INSENSITIVE_ORDER);
        incCategories.add("Other");
        writeIncomeCategoriesToFB();
    }

    /**
     * Write all expense categories managed by this CategoryManager to Firebase
     */
    public void writeExpenseCategoriesToFB() {
        expCategoryRef.setValue(expCategories);
    }

    /**
     * Write all income categories managed by this CategoryManager to Firebase
     */
    public void writeIncomeCategoriesToFB() {
        incCategoryRef.setValue(incCategories);
    }

    /**
     * Delete an expense category from this CategoryManager and from Firebase as well.
     *
     * @param category The category to be removed
     */
    public void deleteExpenseCategory(String category) {
        if (expCategories.indexOf(category) > -1) {
            expCategories.remove(category);
            writeExpenseCategoriesToFB();
        }
    }

    /**
     * Delete an income category from this CategoryManager and from Firebase as well.
     *
     * @param category The category to be removed
     */
    public void deleteIncomeCategory(String category) {
        if (incCategories.indexOf(category) > -1) {
            incCategories.remove(category);
            writeIncomeCategoriesToFB();
        }
    }

}
