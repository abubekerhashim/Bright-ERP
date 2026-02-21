package com.bright.client.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Inventory.Warehouses; // Import your activity
import com.bright.client.Model.Warehouse;
import com.bright.client.R;

import java.util.List;

public class WarehouseAdapter extends RecyclerView.Adapter<WarehouseAdapter.ViewHolder> {

    private final Context context;
    private final List<Warehouse> list;

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

        // 🔹 Call activity methods on button click
        holder.btnRaw.setOnClickListener(v -> {
            if (context instanceof Warehouses) {
                ((Warehouses) context).onRawMaterialClick(warehouse);
            }
        });

        holder.btnWarehouse.setOnClickListener(v -> {
            if (context instanceof Warehouses) {
                ((Warehouses) context).onWarehouseClick(warehouse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtCode, txtLocation, txtStatus;
        LinearLayout btnRaw, btnWarehouse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.warehouse_name);
            txtCode = itemView.findViewById(R.id.warehouse_code);
            txtLocation = itemView.findViewById(R.id.warehouse_location);
            txtStatus = itemView.findViewById(R.id.warehouse_status);

            btnRaw = itemView.findViewById(R.id.btn_raw_material);
            btnWarehouse = itemView.findViewById(R.id.btn_warehouse);
        }
    }
}
