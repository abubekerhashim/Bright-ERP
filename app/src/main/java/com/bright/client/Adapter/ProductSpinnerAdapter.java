package com.bright.client.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bright.client.Model.Product;
import com.bright.client.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ProductSpinnerAdapter extends ArrayAdapter<Product> {

    private final Context context;
    private final List<Product> products;

    public ProductSpinnerAdapter(Context context, List<Product> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // The main selected view (collapsed spinner)
        return createView(position, convertView, parent, R.layout.spinner_product_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // The dropdown list item view
        return createView(position, convertView, parent, R.layout.spinner_product_item);
    }

    private View createView(int position, View convertView, ViewGroup parent, int layoutRes) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.product_image);
            holder.name = convertView.findViewById(R.id.product_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);
        holder.name.setText(product.getName());

        Glide.with(context)
                .load(product.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_loading) // default placeholder
                        .error(R.drawable.ic_error))
                .into(holder.image);

        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView name;
    }
}