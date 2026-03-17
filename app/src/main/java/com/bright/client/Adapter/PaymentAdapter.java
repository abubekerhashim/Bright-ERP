package com.bright.client.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Payment;
import com.bright.client.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Payment> paymentList;

    public PaymentAdapter(Context context, ArrayList<Payment> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_payment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Payment payment = paymentList.get(position);

        holder.name.setText(payment.getAccountName());
        holder.number.setText(payment.getAccountNumber());
        holder.date.setText(payment.getDate());

        DecimalFormat df = new DecimalFormat("#,###");
        holder.amount.setText(df.format(payment.getAmount()) + " Birr");
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, number, amount, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txt_account_name);
            number = itemView.findViewById(R.id.txt_account_number);
            amount = itemView.findViewById(R.id.txt_amount);
            date = itemView.findViewById(R.id.txt_date);
        }
    }
}