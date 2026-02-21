package com.bright.client.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Product;
import com.bright.client.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    Context context;
    List<Product> productList;

    public ProductListAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.productId.setText(product.getProdId());
        holder.productName.setText(product.getName());
        holder.productModel.setText(" - " + product.getModel());
        holder.productCategory.setText(product.getCategory());
        holder.productColor.setText("● " + product.getColor());
        holder.productQuantity.setText("Qty: " + product.getTotalQty());

        holder.productCostPrice.setText("▼ Br " + product.getCostPrice());
        holder.productSellPrice.setText("▲ Br " + product.getSellPrice());

        if (product.isStatus()) {
            holder.productStatus.setText("Active");
            holder.productStatus.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.productStatus.setText("Inactive");
            holder.productStatus.setTextColor(context.getResources().getColor(R.color.red));
        }

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_loading)
                .into(holder.productPhoto);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productPhoto;
        TextView productId, productName, productModel, productCategory,
                productColor, productQuantity, productStatus,
                productCostPrice, productSellPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productPhoto = itemView.findViewById(R.id.product_photo);
            productId = itemView.findViewById(R.id.product_id);
            productName = itemView.findViewById(R.id.product_name);
            productModel = itemView.findViewById(R.id.product_model);
            productCategory = itemView.findViewById(R.id.product_category);
            productColor = itemView.findViewById(R.id.product_color);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productStatus = itemView.findViewById(R.id.product_status);
            productCostPrice = itemView.findViewById(R.id.product_cost_price);
            productSellPrice = itemView.findViewById(R.id.product_sell_price);
        }
    }
}
