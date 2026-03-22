package com.bright.client.Adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.PaymentLog;
import com.bright.client.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentLogAdapter extends RecyclerView.Adapter<PaymentLogAdapter.ViewHolder> {

    private List<PaymentLog> list;

    public PaymentLogAdapter(List<PaymentLog> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_payment_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentLog log = list.get(position);

        holder.textDate.setText(formatTimestamp(log.getTimestamp()));
        holder.textReason.setText(log.getReason());

        if ("expense".equals(log.getType())) {

            holder.imageType.setImageResource(R.drawable.ic_arrow_up_right);

            // Image tint
            holder.imageType.setColorFilter(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.red)
            );

            // Background tint
            holder.imageType.setBackgroundTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.sold_red)
                    )
            );

            holder.textAmount.setText("-ETB " + log.getAmount());
            holder.textAmount.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.red)
            );

        } else {

            holder.imageType.setImageResource(R.drawable.ic_arrow_left_bottom);

            holder.imageType.setColorFilter(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green)
            );

            holder.imageType.setBackgroundTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.sold_green)
                    )
            );

            holder.textAmount.setText("+ETB " + log.getAmount());
            holder.textAmount.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green)
            );
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textAmount, textDate, textReason;
        ImageView imageType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageType = itemView.findViewById(R.id.image_type);
            textAmount = itemView.findViewById(R.id.text_amount);
            textDate = itemView.findViewById(R.id.text_date);
            textReason = itemView.findViewById(R.id.text_reason);
        }
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}