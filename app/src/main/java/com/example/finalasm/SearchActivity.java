package com.example.finalasm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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
    RecyclerView rcvCategory;
    MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Meals
        provider.fetchAllMeal(mealDb, mealList -> {
            for (int i = 0; i < mealList.size(); i++) {
                meal = (Meal) mealList.get(i);
                mainMealList.add(meal);
            }

            mealAdapter.setData(this, mainMealList);
        });

        mealAdapter = new MealAdapter(this, MealAdapter.HORIZONTAL);
        rcvCategory = findViewById(R.id.recycler_search);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvCategory.setLayoutManager(gridLayoutManager);
        rcvCategory.setAdapter(mealAdapter);
    }
}