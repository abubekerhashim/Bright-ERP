package com.bright.client.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.BankAccount;
import com.bright.client.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.MyViewHolder> {

    Context context;
    List<BankAccount> list;

    public BankAccountAdapter(Context context, List<BankAccount> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_bank_account, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        BankAccount account = list.get(position);

        holder.id.setText(account.getAccountId());
        holder.bank.setText(account.getBankName());
        holder.name.setText(account.getAccountName());
        holder.number.setText(account.getAccountNumber());

        DecimalFormat df = new DecimalFormat("#,##0.00");
        holder.balance.setText("ETB " + df.format(account.getBalance()));

        if (account.isStatus()) {
            holder.status.setText("Active");
            holder.status.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.status.setText("Inactive");
            holder.status.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView bank, name, number, balance,id, status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.account_id);
            bank = itemView.findViewById(R.id.bank_name);
            name = itemView.findViewById(R.id.account_name);
            number = itemView.findViewById(R.id.account_number);
            balance = itemView.findViewById(R.id.account_balance);
            status = itemView.findViewById(R.id.account_status);
        }
    }
}