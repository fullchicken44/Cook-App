package com.example.finalasm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> listCategory;
    int type;

    public CategoryAdapter(int type, Context context) {
        this.context = context;
        this.type = type;
    }

    public void setData(List<Category> list) {
        this.listCategory = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_category,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = listCategory.get(position);
        if(category == null){
            return;
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false);
        holder.mealList.setLayoutManager(linearLayoutManager);

        holder.nameCategory.setText(category.getNameCategory());
        MealAdapter mealAdapter = null;
        if (type == MealAdapter.VERTICAL) {
            mealAdapter = new MealAdapter(context,MealAdapter.VERTICAL);
        }else if (type == MealAdapter.HORIZONTAL) {
            mealAdapter = new MealAdapter(context,MealAdapter.HORIZONTAL);
        }
        else if (type == MealAdapter.VERTICAL_ADD) {
            mealAdapter = new MealAdapter(context,MealAdapter.VERTICAL_ADD);
        }
        else if (type == MealAdapter.VERTICAL_REMOVE) {
            mealAdapter = new MealAdapter(context,MealAdapter.VERTICAL_REMOVE);
        }
        mealAdapter.setData(context, category.getMealList(), new ArrayList<String>());

        holder.mealList.setAdapter(mealAdapter);
    }

    @Override
    public int getItemCount() {
        if (listCategory != null){
            return listCategory.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView nameCategory;
        private RecyclerView mealList;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            mealList = itemView.findViewById(R.id.save_list_user);
            nameCategory = itemView.findViewById(R.id.text_save_user);
        }
    }
}