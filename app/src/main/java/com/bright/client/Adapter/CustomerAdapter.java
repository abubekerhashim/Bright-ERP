package com.bright.client.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Customer;
import com.bright.client.R;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private Context context;
    private List<Customer> customerList;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        holder.txtName.setText(customer.getName());
        holder.txtType.setText(customer.getType());
        holder.txtAddress.setText(customer.getAddress());


        if (customer.isStatus()) {
            holder.txtStatus.setText("Active");
            holder.txtStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.txtStatus.setText("Inactive");
            holder.txtStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtType, txtAddress, txtStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.customer_name);
            txtType = itemView.findViewById(R.id.customer_type);
            txtAddress = itemView.findViewById(R.id.customer_address);
            txtStatus = itemView.findViewById(R.id.customer_status);
        }
    }
}
