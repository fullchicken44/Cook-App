package com.example.finalasm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {
    DatabaseReference mealDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user").child("users");
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private RecyclerView rcvCategory;
    private CategoryAdapter categoryAdapter;
    private TextView name_user;
    ImageButton nav_menu,nav_search,nav_user;
    List<Meal> mainMealList = new ArrayList();
    List<User> mainUserList = new ArrayList<>();
    FirebaseDB provider = new FirebaseDB();
    User user;
    User currentUser;
    Meal meal;
    List<Meal> savedMeal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        name_user = findViewById(R.id.name_user);
        nav_menu = findViewById(R.id.nav_menu);
        nav_search = findViewById(R.id.nav_search);
        nav_user = findViewById(R.id.nav_user);

        rcvCategory = findViewById(R.id.recycler_user);
        categoryAdapter = new CategoryAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);


        rcvCategory.setAdapter(categoryAdapter);

        provider.fetchAllUser(userDb, userList -> {
            for (int i = 0; i < userList.size(); i++) {
                user = (User) userList.get(i);
                mainUserList.add(user);
            }
            for (User user : mainUserList) {
                if (firebaseUser.getEmail().equals(user.getUserEmail())) {
                    currentUser = user;
                    String nameBreakDown = user.getUserName();
                    String[] nameParts = nameBreakDown.split(" ");
                    name_user.setText(nameParts[0]);
                }
            }
            provider.fetchAllMeal(mealDb, mealList -> {
                for (int i = 0; i < mealList.size(); i++) {
                    meal = (Meal) mealList.get(i);
                    mainMealList.add(meal);
                    for (int j = 0; j < currentUser.getCollection().size(); j++) {
                        if (Long.parseLong(currentUser.getCollection().get(j)) == i) {
                            savedMeal.add(mainMealList.get(i));
                        }
                    }
                }
                categoryAdapter.setData(getListCategory());
            });
        });
    }

    private List<Category> getListCategory(){
        List<Category> listCategory = new ArrayList<>();
        listCategory.add(new Category("Saved Collection", savedMeal));
        return listCategory;
    }


}