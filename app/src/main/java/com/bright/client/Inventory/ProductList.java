package com.bright.client.Inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.bright.client.Adapter.ProductAdapter;
import com.bright.client.Adapter.ProductListAdapter;
import com.bright.client.Model.Product;
import com.bright.client.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductList extends AppCompatActivity {

    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerLayout;

    ArrayList<Product> productList;
    ProductListAdapter adapter;

    DatabaseReference productRef;
    private FloatingActionButton addProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        recyclerView = findViewById(R.id.recycler_product_list);
        shimmerLayout = findViewById(R.id.shimmer_layout);

        addProduct = findViewById(R.id.add_product);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        adapter = new ProductListAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        productRef = FirebaseDatabase.getInstance().getReference("Products");

        addProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ProductList.this, AddProduct.class);
            startActivity(intent);
        });

        loadProducts();
    }

    private void loadProducts() {

        shimmerLayout.startShimmer();
        shimmerLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }

                adapter.notifyDataSetChanged();

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
            }
        });
    }
}
