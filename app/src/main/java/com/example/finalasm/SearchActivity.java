package com.example.finalasm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private final String dbAPI = "https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app";
    DatabaseReference mealDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user").child("users");
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDB provider = new FirebaseDB();
    Meal meal;
    List<Meal> mainMealList = new ArrayList<Meal>();
    List<Meal> secondMealList = new ArrayList<>();
    ImageButton nav_menu, nav_search, nav_user;
    SearchView search_bar;
    RecyclerView rcvCategory;
    MealAdapter mealAdapter;
    MealAdapter cateAdapter;
    RecyclerView rcvMeal;
    Intent intent;
    List<String> cateList = new ArrayList<>();
    String categoryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_bar = findViewById(R.id.search_bar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        nav_menu = findViewById(R.id.nav_menu);
        nav_user = findViewById(R.id.nav_user);
        rcvMeal = findViewById(R.id.recycler_search);


        //Navigation to the main menu
        nav_menu.setOnClickListener(v -> {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        //Navigation to the user page
        nav_user.setOnClickListener(v -> {
            intent = new Intent(this, UserProfile.class);
            startActivity(intent);
            finish();
        });

        // Get all meals
        provider.fetchAllMeal(mealDb, mealList -> {
            for (int i = 0; i < mealList.size(); i++) {
                meal = (Meal) mealList.get(i);
                mainMealList.add(meal);
                secondMealList.add(meal);
                if (!cateList.contains(meal.getStrCategory())) {
                    cateList.add(meal.getStrCategory());
                }
            }
            //Set data to adapter
            Log.d("CATEGORIES", cateList.toString());
            cateAdapter.setData(this, secondMealList, cateList);
            mealAdapter.setData(this, mainMealList, cateList);
        });

        //Meal list displayed by grid
        mealAdapter = new MealAdapter(this, "SearchActivity", MealAdapter.HORIZONTAL);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvMeal.setLayoutManager(gridLayoutManager);
        rcvMeal.setAdapter(mealAdapter);

        //Category list displayed by horizontal
        cateAdapter = new MealAdapter(this, "SearchActivity", MealAdapter.CATEGORY);
        rcvCategory = findViewById(R.id.recycler_cate_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);
        rcvCategory.setAdapter(cateAdapter);
        rcvMeal.setAdapter(mealAdapter);


//        List<Meal> cateFilter = mealAdapter.getMealByCategory(mainMealList,);

        //Search function
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rcvCategory.setVisibility(View.GONE);
                List<Meal> filterlist = mealAdapter.filter(query, mainMealList);
                mealAdapter.setFilterData(SearchActivity.this, filterlist);
                rcvCategory.setAdapter(mealAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rcvCategory.setVisibility(View.GONE);
                if (newText.isEmpty()) {
                    mealAdapter.setFilterData(SearchActivity.this, mainMealList);
                }
                List<Meal> filtering = mealAdapter.filter(newText, mainMealList);
                mealAdapter.setFilterData(SearchActivity.this, filtering);
                rcvCategory.setAdapter(mealAdapter);
                return true;
            }
        });
        //After close then search view dismiss
        search_bar.setOnCloseListener(() -> {
            rcvCategory.setAdapter(cateAdapter);
            rcvCategory.setVisibility(View.VISIBLE);
            return false;
        });
    }

//    @Override
//    public void onBackPressed() {
//        intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
}