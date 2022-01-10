package com.example.finalasm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.DishViewHolder>{
    private List<Meal> mealList;
    Context context;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL_ADD = 1;
    public static final int VERTICAL_REMOVE = 2;
    public static final int VERTICAL = 3;
    int type;

    public MealAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }


    public void setData(Context context, List<Meal> mealList) {
        this.mealList = mealList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (type == VERTICAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dish_card, parent, false);
        }else if (type == HORIZONTAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dish_card_horizontal, parent, false);
        }
        else if (type == VERTICAL_ADD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dish_card_add, parent, false);
        }
        else if (type == VERTICAL_REMOVE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dish_card_remove, parent, false);
        }
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String imageURL = "https:\\/\\/www.themealdb.com\\/images\\/media\\/meals\\/ysqupp1511640538.jpg";
        Meal meal = mealList.get(position);
        if (meal == null){
            return;
        }
        else  {
            Picasso.get()
                    .load(meal.getStrMealThumb())
                    .centerCrop()
                    .fit()
                    .into(holder.imageDish);
        }
        holder.nameDish.setText(meal.getStrMeal());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipeActivity.class);
                intent.putExtra("name",mealList.get(position).getStrMeal());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mealList != null){
            return mealList.size();
        }
        return 0;
    }

    public class DishViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageDish;
        private TextView nameDish;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);

            imageDish = itemView.findViewById(R.id.menu_food_display);
            nameDish = itemView.findViewById(R.id.name_dish_card);
        }
    }
}
