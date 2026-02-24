package com.bright.client.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Product;
import com.bright.client.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ChoosePurchaseListAdapter extends RecyclerView.Adapter<ChoosePurchaseListAdapter.ProductViewHolder> {

    Context context;
    List<Product> productList;

    public ChoosePurchaseListAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_choose_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.productId.setText(product.getProdId());
        holder.productName.setText(product.getName());
        holder.productModel.setText(product.getModel());
        holder.productColor.setText("●  " + product.getColor());

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_loading)
                .into(holder.productPhoto);

        //Quantity Button
        functionQuantityBtn(holder);


    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productPhoto;
        TextView productId, productName, productModel, productColor;

        ImageView btnMinus, btnPlus;
        EditText quantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productPhoto = itemView.findViewById(R.id.product_photo);
            productId = itemView.findViewById(R.id.product_id);
            productName = itemView.findViewById(R.id.product_name);
            productModel = itemView.findViewById(R.id.product_model);
            productColor = itemView.findViewById(R.id.product_color);

            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            quantity = itemView.findViewById(R.id.product_quantity);
        }
    }

    private void functionQuantityBtn(ProductViewHolder holder) {
        holder.btnPlus.setOnClickListener(v -> {


            String qtyString = holder.quantity.getText().toString().trim();
            int qty = qtyString.isEmpty() ? 0 : Integer.parseInt(qtyString);
            if (qty < 99999999){
                qty++;

                holder.quantity.setText(String.valueOf(qty));
            }else{
                Toast.makeText(context, "Max Quantity", Toast.LENGTH_SHORT).show();
            }

        });

        holder.btnMinus.setOnClickListener(v -> {

            String qtyString = holder.quantity.getText().toString().trim();

            int qty = qtyString.isEmpty() ? 0 : Integer.parseInt(qtyString);

            if (qty > 1) {
                qty--;
                holder.quantity.setText(String.valueOf(qty));
            }
        });
    }
}
