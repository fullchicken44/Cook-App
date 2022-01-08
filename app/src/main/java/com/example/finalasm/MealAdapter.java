package com.example.finalasm;

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

    public void setData(List<Meal> mealList) {
        this.mealList = mealList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dish_card,parent,false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
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
//        holder.imageDish.setImageResource(R.drawable.slack_pic);
        holder.nameDish.setText(meal.getStrMeal());
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