package com.bright.client.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Product;
import com.bright.client.R;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;
    private Map<String, Integer> warehouseStockMap, warehouseMinStock;


    private final DecimalFormat formatter = new DecimalFormat("#,##0.00");

    private OnProductActionListener onProductActionListener;

    public interface OnProductActionListener {
        void onEdit(Product product);
        void onDelete(Product product);
        void onAdjustStock(Product product);
        void onLayout(Product product);
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.onProductActionListener = listener;
    }

    public ProductAdapter(Context context, List<Product> productList,
                          Map<String, Integer> warehouseStockMap,
                          Map<String, Integer> warehouseMinStock) {
        this.context = context;
        this.productList = productList;
        this.warehouseStockMap = warehouseStockMap;
        this.warehouseMinStock = warehouseMinStock;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.name.setText(product.getName());
        holder.model.setText(" (" + product.getModel() + ")");
        holder.category.setText(product.getCategory());
        holder.id.setText(product.getProdId());



        holder.cost.setText("Buy ▼ Br " + formatPrice(product.getCostPrice()) + "   ");
        holder.sell.setText("Sell▲ Br " + formatPrice(product.getSellPrice()));

        //colors changing
        String colorStr = product.getColor().toLowerCase();
        int color;

        switch (colorStr) {
            case "red": color = Color.RED; break;
            case "green": color = Color.GREEN; break;
            case "blue": color = Color.BLUE; break;
            case "orange": color = Color.parseColor("#FFA500"); break; // custom name
            case "purple": color = Color.parseColor("#800080"); break;
            default: color = Color.BLACK;
        }

        holder.color.setText("● " + product.getColor());
        holder.color.setTextColor(color);



        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_error)
                .centerCrop()
                .into(holder.image);

        if (product.isStatus()) {
            holder.status.setText("Active");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.status.setText("Inactive");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        int stock = warehouseStockMap.getOrDefault(product.getProdId(), 0);
        int minStock = warehouseMinStock.getOrDefault(product.getProdId(), 0);
        holder.stock.setText(stock + " " + product.getUnit() + " left");
        if (stock <= minStock) {
            holder.stock.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.stock.setTextColor(ContextCompat.getColor(context, R.color.green));
        }

        holder.btnMore.setOnClickListener(v -> {
            androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context, holder.btnMore);
            popupMenu.getMenu().add("Edit");
            popupMenu.getMenu().add("Delete");
            popupMenu.getMenu().add("Adjust Stock");

            popupMenu.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                if (title.equals("Edit") && onProductActionListener != null)
                    onProductActionListener.onEdit(product);
                else if (title.equals("Delete") && onProductActionListener != null)
                    onProductActionListener.onDelete(product);
                else if (title.equals("Adjust Stock") && onProductActionListener != null)
                    onProductActionListener.onAdjustStock(product);
                return true;
            });

            popupMenu.show();
        });

        holder.itemView.setOnClickListener(v -> {
            onProductActionListener.onLayout(product);
        });

    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, model, category, id, color, cost, sell, status, stock;
        ImageView image, btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            model = itemView.findViewById(R.id.product_model);
            category = itemView.findViewById(R.id.product_category);
            id = itemView.findViewById(R.id.product_id);
            color = itemView.findViewById(R.id.product_color);
            cost = itemView.findViewById(R.id.product_cost_price);
            sell = itemView.findViewById(R.id.product_sell_price);
            status = itemView.findViewById(R.id.product_status);
            stock = itemView.findViewById(R.id.product_quantity);
            image = itemView.findViewById(R.id.product_photo);
            btnMore = itemView.findViewById(R.id.btn_more);
        }
    }


    private String formatPrice(double price) {
        return formatter.format(price);
    }

}
