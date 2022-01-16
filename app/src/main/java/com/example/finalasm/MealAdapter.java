package com.example.finalasm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.DishViewHolder>{
    private List<Meal> mealList;
    private List<String> cateList;
    Context context;
    Meal meal;
    User user, currentUser;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL_ADD = 1;
    public static final int VERTICAL_REMOVE = 2;
    public static final int VERTICAL = 3;
    public static final int CATEGORY = 4;
    public static final int HORIZONTAL_ADD = 5;
    FirebaseDB provider = new FirebaseDB();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    DatabaseReference mealDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user").child("users");

    int type;
    String key = "0";
    String classType;
    List<Meal> filterList;
    List<Meal> tempList;

    public List<Meal> getTempList() {
        return tempList;
    }

    private List<Meal> categoryList;

    public MealAdapter(Context context, String classType, int type) {
        this.classType = classType;
        this.context = context;
        this.type = type;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterData(Context context, List<Meal> mealList) {
        this.mealList = mealList;
        this.context = context;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(Context context, List<Meal> mealList, List<String> cateList) {
        this.cateList = cateList;
        this.mealList = mealList;
        this.context = context;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterCategory(Context context, List<Meal> categoryList) {
        this.categoryList = categoryList;
//        this.mealList = mealList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (type == HORIZONTAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dish_card_horizontal, parent, false);
        }
        else if (type == VERTICAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dish_card, parent, false);
        }
        else if (type == CATEGORY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_category_card, parent, false);
        }
        else if (type == HORIZONTAL_ADD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dish_card_horizontal_add, parent, false);
        }
        assert view != null;
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (type == CATEGORY) {
            String cate = cateList.get(position);
            if (cate == null) {
                return;
            } else {
                holder.nameCate.setText(cate);
                holder.itemView.setOnClickListener(v->{
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("name", cate);
                    context.startActivity(intent);
                });

            }
        } else {
            meal = mealList.get(position);
            if (meal == null) {
                return;
            } else {
                Picasso.get()
                        .load(meal.getStrMealThumb())
                        .centerCrop()
                        .fit()
                        .into(holder.imageDish);
            }
            holder.nameDish.setText(meal.getStrMeal());
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RecipeActivity.class);
                intent.putExtra("name", mealList.get(position).getStrMeal());
                if (classType.equals("UserProfile")) {
                    intent.putExtra("previousActivity", "UserProfile");
                }
                else if (classType.equals("MainActivity")) {
                    intent.putExtra("previousActivity", "MainActivity");
                }
                else if (classType.equals("SearchActivity")) {
                    intent.putExtra("previousActivity", "SearchActivity");
                }
                context.startActivity(intent);
            });

        }
        if (!(firebaseUser == null)) {
            provider.fetchAllUser(userDb, userList -> {
                for (int i = 0; i < userList.size(); i++) {
                    user = (User) userList.get(i);
                    if (firebaseUser.getEmail().equals(user.getUserEmail())) {
                        currentUser = user;
                        key = String.valueOf(i);
                        break;
                    }
                }
                if (type == HORIZONTAL_ADD) {
                    holder.buttonAddHorizontal.setOnClickListener(v -> {
                        if (currentUser.getCollection().contains(String.valueOf(position))) {
                            Toast.makeText(context, "This meal is already in your collection", Toast.LENGTH_LONG).show();
                        } else {
                            if (currentUser.getCollection().get(0).equals("")) {
                                currentUser.getCollection().remove(0);
                            }
                            meal = mealList.get(position);
                            currentUser.getCollection().add(String.valueOf(position));
                            Log.d("SUCCESS", "Saved " + meal.getStrMeal() + " to your collection");
                            userDb.child(key).setValue(currentUser);
                        }

                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (type == CATEGORY) {
            if (cateList != null){
                return cateList.size();
            }
        }
        else {
            if (mealList != null) {
                return mealList.size();
            }
        }
        return 0;
    }

    public class DishViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageDish;
        private TextView nameDish;
        private TextView nameCate;
        private ImageButton buttonAddHorizontal;


        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            if (type == CATEGORY) {
                RecyclerView rcvCategory = itemView.findViewById(R.id.recycler_search);
                nameCate = itemView.findViewById(R.id.name_category_card);
            }
            else {
                buttonAddHorizontal = itemView.findViewById(R.id.button_dish_card_horizontal);

                imageDish = itemView.findViewById(R.id.menu_food_display);
                nameDish = itemView.findViewById(R.id.name_dish_card);
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public List<Meal> filter(String text, List<Meal> mealList) {
        List<Meal> filterList = new ArrayList<>();
        if (text.isEmpty()) {
            filterList.addAll(mealList);
        } else {
            text = text.toLowerCase();
            for (Meal meal : mealList) {
                if (meal.getStrMeal().toLowerCase().contains(text)) {
                    filterList.add(meal);
                } else
                if (!meal.getStrMeal().toLowerCase().contains(text)) {
                    filterList.remove(meal);
                }
                notifyDataSetChanged();
            }
        }
        return filterList;
    }






//    public void removeAt(int position) {
//        mealList.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, mealList.size());
//    }
}

