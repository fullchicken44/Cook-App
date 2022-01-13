package com.example.finalasm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String dbAPI = "https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app";
    DatabaseReference mealDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user").child("users");
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser = null;
    ImageView userAvatar;
    TextView username;
    SearchView searchFood;
    ImageButton nav_menu,nav_search,nav_user;
    Meal meal;
    User user;
    List<Meal> mainMealList = new ArrayList<>();
    List<User> mainUserList = new ArrayList<>();
    FirebaseDB provider = new FirebaseDB();
    RecyclerView rcvCategory;
    MealAdapter mealAdapter;


    @Override
    protected void onResume() {
        super.onResume();
        rcvCategory = findViewById(R.id.recycler_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        provider.fetchAllUser(userDb,userList -> {
            for (int i = 0; i < userList.size(); i++) {
                user = (User) userList.get(i);
                mainUserList.add(user);
            }
            if (firebaseUser == null) {
                mealAdapter = new MealAdapter(this, MealAdapter.HORIZONTAL);
                username.setText("Guest");
            } else {
                for (User user : mainUserList) {
                    if (firebaseUser.getEmail().equals(user.getUserEmail())) {
                        mealAdapter = new MealAdapter(this, MealAdapter.HORIZONTAL_ADD);
                        username.setText(user.getUserName());
                    }
                }
            }
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            rcvCategory.setLayoutManager(gridLayoutManager);
            rcvCategory.setAdapter(mealAdapter);

            provider.fetchAllMeal(mealDb, mealList -> {
                for (int i = 0; i < mealList.size(); i++) {
                    meal = (Meal) mealList.get(i);
                    mainMealList.add(meal);
                }
                mealAdapter.setData(this,mainMealList,new ArrayList<>());
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        rcvCategory = findViewById(R.id.recycler_main);

        provider.fetchAllUser(userDb,userList -> {
            for (int i = 0; i < userList.size(); i++) {
                user = (User) userList.get(i);
                mainUserList.add(user);
            }
            if (firebaseUser == null) {
                mealAdapter = new MealAdapter(this, MealAdapter.HORIZONTAL);
                username.setText("Guest");
            } else {
                for (User user : mainUserList) {
                    if (firebaseUser.getEmail().equals(user.getUserEmail())) {
                        mealAdapter = new MealAdapter(this, MealAdapter.HORIZONTAL_ADD);
                        username.setText(user.getUserName());
                    }
                }
            }
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            rcvCategory.setLayoutManager(gridLayoutManager);
            rcvCategory.setAdapter(mealAdapter);

            provider.fetchAllMeal(mealDb, mealList -> {
                for (int i = 0; i < mealList.size(); i++) {
                    meal = (Meal) mealList.get(i);
                    mainMealList.add(meal);
                }
                mealAdapter.setData(this,mainMealList,new ArrayList<>());
            });
        });



        userAvatar = findViewById(R.id.avatar);
        username = findViewById(R.id.username);
        searchFood = findViewById(R.id.search_food);
        nav_menu = findViewById(R.id.nav_menu);
        nav_search = findViewById(R.id.nav_search);
        nav_user = findViewById(R.id.nav_user);

        nav_user.setOnClickListener(v -> {
            Intent intent;
            if (firebaseUser == null) {
                intent = new Intent(MainActivity.this, LoginActivity.class);
            } else {
                intent = new Intent(MainActivity.this, UserProfile.class);
            }
            startActivity(intent);
        });

        nav_search.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private int randomInt(int size) {
        int min = 0;
        return min;
    }

    public interface firebaseCallback {
        void call(List list);
    }
}