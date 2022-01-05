package com.example.finalasm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ImageButton userAvatar;
    TextView username;
    SearchView searchFood;
    ImageButton nav_menu,nav_search,nav_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userAvatar = findViewById(R.id.avatar);
        username = findViewById(R.id.username);
        searchFood = findViewById(R.id.search_food);
        nav_menu = findViewById(R.id.nav_menu);
        nav_search = findViewById(R.id.nav_search);
        nav_user = findViewById(R.id.nav_user);

        userAvatar.setOnClickListener(v -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }


}