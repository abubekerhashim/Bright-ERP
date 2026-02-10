package com.bright.client.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Category;
import com.bright.client.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categoryList;

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_categories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.id.setText(category.getId());
        holder.name.setText(category.getName());

        // Set the icon
        holder.categoryPhoto.setImageResource(category.getIcon());

        if (category.isStatus()) {
            holder.status.setText("Active");
            holder.status.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.status.setText("Inactive");
            holder.status.setTextColor(Color.RED);
        }
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, status, id;
        ImageView categoryPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            status = itemView.findViewById(R.id.category_status);
            id = itemView.findViewById(R.id.category_id);
            categoryPhoto = itemView.findViewById(R.id.category_photo);
        }
    }
}
