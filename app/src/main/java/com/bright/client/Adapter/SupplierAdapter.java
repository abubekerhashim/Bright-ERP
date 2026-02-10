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

import com.bright.client.Model.Suppliers;
import com.bright.client.R;

import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {

    private Context context;
    private List<Suppliers> supplierList;

    public SupplierAdapter(Context context, List<Suppliers> supplierList) {
        this.context = context;
        this.supplierList = supplierList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_supplier, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Suppliers supplier = supplierList.get(position);

        holder.txtName.setText(supplier.getName());
        holder.txtCategories.setText(supplier.getCategories());
        holder.txtAddress.setText(supplier.getAddress());

        if (supplier.isStatus()) {
            holder.txtStatus.setText("Active");
            holder.txtStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.txtStatus.setText("Inactive");
            holder.txtStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtCategories, txtAddress, txtStatus;
        LinearLayout btnCall, btnCopyAcc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.supplier_name);
            txtCategories = itemView.findViewById(R.id.supplier_categories);
            txtAddress = itemView.findViewById(R.id.supplier_address);
            txtStatus = itemView.findViewById(R.id.supplier_status);

            btnCall = itemView.findViewById(R.id.btn_call);
            btnCopyAcc = itemView.findViewById(R.id.btn_copy_acc);


        }
    }
}
