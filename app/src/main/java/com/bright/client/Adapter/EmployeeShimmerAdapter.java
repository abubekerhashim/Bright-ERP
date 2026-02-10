package com.bright.client.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.R;


public class EmployeeShimmerAdapter
        extends RecyclerView.Adapter<EmployeeShimmerAdapter.ShimmerVH> {

    private int itemCount = 6; // number of shimmer rows

    @NonNull
    @Override
    public ShimmerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_employee_shimmer, parent, false);
        return new ShimmerVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShimmerVH holder, int position) { }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    static class ShimmerVH extends RecyclerView.ViewHolder {
        ShimmerVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
