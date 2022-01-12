package com.example.finalasm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.DishViewHolder>{
    private List<Meal> mealList;
    private List<String> cateList;
    private List<Meal> filterList;
    Context context;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL_ADD = 1;
    public static final int VERTICAL_REMOVE = 2;
    public static final int VERTICAL = 3;
    public static final int CATEGORY = 4;
    public static final int HORIZONTAL_ADD = 5;

    int type;

    public MealAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public void setFilterData(Context context, List<Meal> mealList) {
        this.mealList = mealList;
        this.context = context;
        notifyDataSetChanged();
    }

    public void setData(Context context, List<Meal> mealList, List<String> cateList) {
        this.cateList = cateList;
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
        else if (type == CATEGORY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_category_card, parent, false);
        }
        else if (type == HORIZONTAL_ADD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dish_card_horizontal_add, parent, false);
        }
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
            }
        } else {
            String imageURL = "https:\\/\\/www.themealdb.com\\/images\\/media\\/meals\\/ysqupp1511640538.jpg";
            Meal meal = mealList.get(position);
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra("name", mealList.get(position).getStrMeal());
                    context.startActivity(intent);
                }
            });

        }
        //TO DO
        if (type == VERTICAL_ADD) {
            holder.buttonAdd.setOnClickListener(v -> {
                System.out.println("add to save collection/ check thu trong collection da co no chua");
            });
        } else if (type == VERTICAL_REMOVE) {
            holder.buttonRemove.setOnClickListener(v -> {
                System.out.println("delete from save collection");
            });
        } else if (type == HORIZONTAL_ADD) {
            holder.buttonAddHorizontal.setOnClickListener(v -> {
                System.out.println("add to save collection/ check thu trong collection da co no chua");
            });
        }
    }



    @Override
    public int getItemCount() {
        if (type == CATEGORY) {
            if (cateList != null){
                return cateList.size();
            }
            return 0;
        }
        else {
            if (mealList != null) {
                return mealList.size();
            }
            return 0;
        }
    }

    public class DishViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageDish;
        private TextView nameDish;
        private TextView nameCate;
        private ImageButton buttonAdd;
        private ImageButton buttonAddHorizontal;
        private ImageButton buttonRemove;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);

            if (type == CATEGORY) {
                nameCate = itemView.findViewById(R.id.name_category_card);
            }
            else {
                buttonAdd = (ImageButton) itemView.findViewById(R.id.button_add_dish_card);
                buttonAddHorizontal = (ImageButton) itemView.findViewById(R.id.button_dish_card_horizontal);
                buttonRemove = (ImageButton) itemView.findViewById(R.id.button_dish_card_remove);
                imageDish = itemView.findViewById(R.id.menu_food_display);
                nameDish = itemView.findViewById(R.id.name_dish_card);
            }
        }
    }
    public List<Meal> filter(String text, List<Meal> mealList) {
        filterList = new ArrayList<>();
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
}
