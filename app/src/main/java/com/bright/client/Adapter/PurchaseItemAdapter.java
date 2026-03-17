package com.bright.client.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.PurchaseItem;
import com.bright.client.R;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;

public class PurchaseItemAdapter extends RecyclerView.Adapter<PurchaseItemAdapter.MyViewHolder> {

    Context context;
    List<PurchaseItem> list;

    public PurchaseItemAdapter(Context context, List<PurchaseItem> list) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, model, quantity, price;
        ImageView itemImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            model = itemView.findViewById(R.id.item_model);
            quantity = itemView.findViewById(R.id.item_quantity);
            price = itemView.findViewById(R.id.item_price);
            itemImage = itemView.findViewById(R.id.item_image);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_purchase, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PurchaseItem item = list.get(position);

        holder.name.setText(item.getName());
        holder.model.setText("Model: " + item.getModel());
        holder.quantity.setText("Qty: " + item.getQuantity() + item.getUnit());

        NumberFormat formatter = NumberFormat.getInstance();
        holder.price.setText(formatter.format(item.getTotalPrice()) + " Birr");

        Glide.with(context)
                .load(item.getImage())
                .placeholder(R.drawable.ic_loading)
                .into(holder.itemImage);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}