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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.DishViewHolder>{
    private List<Meal> mealList;
    Context context;
    Meal meal;
    User user, currentUser;
    FirebaseDB provider = new FirebaseDB();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    DatabaseReference mealDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user").child("users");

    IClickListener iClickListener;

    public interface IClickListener {
        void onClickTransfer(Meal meal,int position);
    }

    public CollectionAdapter(Context context, List<Meal> mealList, IClickListener iClickListener) {
        this.mealList = mealList;
        this.context = context;
        this.iClickListener = iClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterData(Context context, List<Meal> mealList) {
        this.mealList = mealList;
        this.context = context;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        assert view != null;
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, @SuppressLint("RecyclerView") int position) {
        meal = mealList.get(position);
        if (meal == null) {
            return;
        }
        holder.nameDish.setText(meal.getStrMeal());
    }

    @Override
    public int getItemCount() {
        if (mealList != null) {
                return mealList.size();
            }
        return 0;
    }

    public class DishViewHolder extends RecyclerView.ViewHolder {
        private TextView nameDish;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            nameDish = itemView.findViewById(R.id.name_dish_card);
        }
    }
}

