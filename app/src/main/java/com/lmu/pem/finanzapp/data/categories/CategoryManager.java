package com.lmu.pem.finanzapp.data.categories;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class CategoryManager {

    private static CategoryManager instance;

    private final Category[] DEFAULTCATEGORIES = DefaultCategory.values();
    private ArrayList<CustomCategory> customCategories;
    private ArrayList<String> defaultCategories;


    private ArrayList<String> expCategories;
    private ArrayList<String> incCategories;

    private CustomCategory categoryList;


    private DatabaseReference db;
    private DatabaseReference catDbRef, expCategoryRef, incCategoryRef;
    private String expKey;
    private String incKey;

    public CategoryManager() {
        expCategories = new ArrayList<>();
        incCategories = new ArrayList<>();




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        catDbRef = db.child("categories");

        expCategoryRef = db.child("expenseCategories");
        incCategoryRef = db.child("incomeCategories");



        expCategories.addAll(createDefaultExpCategories());
        incCategories.addAll(createDefaultIncCategories());
        //categoryList = new CustomCategory(expCategories, incCategories);



        /*

        expCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> cats = new ArrayList<>();

                for(DataSnapshot data: dataSnapshot.getChildren()){
                    final String item = data.getValue(String.class);
                    cats.add(item);


                    for(int i = 0 ; i < cats.size(); i++){
                        expCategories.set(i, cats.get(i));
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

*/

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


    /*
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
    */

    public void addExpenseCategory(String category){
        this.expCategories.add(category);
        this.expCategories.remove("Other");
        this.expCategories.remove("Add");
        Collections.sort(expCategories);
        this.expCategories.add("Other");
        this.expCategories.add("Add");
        expKey = writeDefaultExpenseCategory(this.expCategories);



        /*
        String key = db.child("expense").push().getKey();
        db.push().setValue(newCat);
        */
    }



    public void addIncomeCategory(String category){
        incCategories.add(category);
        incCategories.remove("Other");
        incCategories.remove("Add");
        Collections.sort(incCategories);
        incCategories.add("Other");
        incCategories.add("Add");
        incKey = writeDefaultIncomeCategory(incCategories);
    }


    public void addCategoriesFromFirebase(String id, ArrayList<String> categories){

    }


    /**
     * A temporäre Methode (TODO: retrieving an arraylist from xml file or from database)
     * @return
     */
    public ArrayList<String> createDefaultExpCategories(){
        ArrayList<String> defaultExpCats = new ArrayList<>();
        defaultExpCats.addAll(Arrays.asList("Food","Household","Transportation","Health","Movie", "Beauty", "Apparel", "Party", "Gift", "Education", "Music",
                "Car","Travel"));
        Collections.sort(defaultExpCats);
        defaultExpCats.add("Other");
        defaultExpCats.add("Add");
        writeDefaultExpenseCategory(defaultExpCats);
        /*
        for(int i =0; i<defaultExpCats.size(); i++){
            writeNewCategoryToFB(defaultExpCats.get(i));
        }
        */
        return defaultExpCats;
    }

    public ArrayList<String> createDefaultIncCategories(){
        ArrayList<String> defaultIncCats = new ArrayList<>();
        defaultIncCats.addAll(Arrays.asList("Salary","Bonus","Petty cash", "Stock"));
        Collections.sort(defaultIncCats);
        defaultIncCats.add(0,"");
        defaultIncCats.add("Other");
        defaultIncCats.add("Add");
        writeDefaultIncomeCategory(defaultIncCats);
        return defaultIncCats;
    }

    public String writeCategoryToFB(ArrayList<String> categories){
        String key = catDbRef.push().getKey();
        catDbRef.setValue(categories);
        return key;



    }

    public String writeDefaultExpenseCategory(ArrayList<String> cate){
        String key = expCategoryRef.push().getKey();
        expCategoryRef.setValue(cate);
        return key;
    }

    public String writeDefaultIncomeCategory(ArrayList<String> cat){
        String key = incCategoryRef.push().getKey();
        incCategoryRef.setValue(cat);
        return key;
    }



    public String writeNewCategoryToFB(String cat){
        String key = expCategoryRef.push().getKey();

       // Map<String, Object> categoryValue = expCategories.to

        //categoryRef.push().setValue(cat);
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
