package com.example.finalasm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    ImageButton back_btn;
    TextView categoryName;
    Meal meal;
    FirebaseDB provider = new FirebaseDB();
    List<Meal> mainMealList = new ArrayList<>();
    DatabaseReference mealDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user").child("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        back_btn = findViewById(R.id.backButtonRecipe);
        categoryName = findViewById(R.id.categoryName);

        Intent intent = getIntent();
        String category = intent.getExtras().getString("name");
        categoryName.setText(category);

        back_btn.setOnClickListener(v -> {finish();});

        //Get all meal
        provider.fetchAllMeal(mealDb, mealList -> getMealByCategory(mealList, category)
        );
    }

    public List<Meal> getMealByCategory(List<Meal> mealList, String categoryName) {
        List<Meal> pickedMeal = new ArrayList<>();
        for (Meal meal : mealList) {
            if (meal.getStrCategory().toLowerCase().trim().equals(categoryName.toLowerCase().trim())) {
                pickedMeal.add(meal);
            }
        }
        List<String> names = new ArrayList<>();
        for (Meal meal : pickedMeal) {
            names.add(meal.getStrMeal());
        }
        Log.d("Number of " + categoryName, String.valueOf(names.size()));
        Log.d("Meal from " + categoryName, names.toString());
        return pickedMeal;
    }
}