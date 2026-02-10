package com.bright.client.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Warehouse;
import com.bright.client.R;

import java.util.List;

public class WarehouseAdapter extends RecyclerView.Adapter<WarehouseAdapter.ViewHolder> {

    private Context context;
    private List<Warehouse> list;

    public WarehouseAdapter(Context context, List<Warehouse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_warehouse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Warehouse warehouse = list.get(position);

        holder.txtName.setText(warehouse.getName());
        holder.txtCode.setText(warehouse.getCode());
        holder.txtLocation.setText(warehouse.getLocation());

        if (warehouse.isStatus()) {
            holder.txtStatus.setText("Active");
            holder.txtStatus.setTextColor(Color.GREEN);
        } else {
            holder.txtStatus.setText("Inactive");
            holder.txtStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtCode, txtLocation, txtStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.warehouse_name);
            txtCode = itemView.findViewById(R.id.warehouse_code);
            txtLocation = itemView.findViewById(R.id.warehouse_location);
            txtStatus = itemView.findViewById(R.id.warehouse_status);
        }
    }
}
