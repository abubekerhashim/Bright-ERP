package com.bright.client.Inventory;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Adapter.ProductAdapter;
import com.bright.client.Model.Product;
import com.bright.client.Model.Warehouse;
import com.bright.client.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Products extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerLayout;
    private ProductAdapter adapter;
    private final List<Product> productList = new ArrayList<>();
    private final Map<String, Integer> warehouseStockMap = new HashMap<>();
    private final Map<String, Integer> warehouseMinStock = new HashMap<>();

    private DatabaseReference productRef, stockRef, warehouseRef;
    private String warehouseId = "";

    private ImageView backButton, refreshButton, productFilter;

    private TextView warehouseLocation, warehouseCode, warehouseStatus, warehouseName;

    private LinearLayout noDataLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        setupStatusBar();
        initViews();
        setupRecycler();
        setupFirebase();
        setupClicks();

        loadWarehouseInfo();
        loadProducts();
    }

    private void setupClicks() {
        backButton.setOnClickListener(v -> finish());

        refreshButton.setOnClickListener(v -> {
            refreshButton.animate().rotationBy(360).setDuration(600).start();
            loadWarehouseInfo();
            loadProducts();
        });

    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        warehouseId = getIntent().getStringExtra("warehouseId");
        if (warehouseId == null || warehouseId.isEmpty()) {
            Toast.makeText(this, "Invalid Warehouse ID", Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.recycler_product);
        shimmerLayout = findViewById(R.id.shimmer_layout);
        backButton = findViewById(R.id.back_button);
        refreshButton = findViewById(R.id.btn_refresh);

        warehouseLocation = findViewById(R.id.warehouse_location);
        warehouseCode = findViewById(R.id.warehouse_code);
        warehouseStatus = findViewById(R.id.warehouse_status);
        warehouseName = findViewById(R.id.toolbar_warehouse_name);
        productFilter = findViewById(R.id.product_filter);

        noDataLayout = findViewById(R.id.no_data_layout);


    }

    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, productList, warehouseStockMap, warehouseMinStock);
        recyclerView.setAdapter(adapter);

        // Handle edit/delete
        // In setupRecycler() use setOnProductActionListener, not adapter.OnProductActionListener
        adapter.setOnProductActionListener(new ProductAdapter.OnProductActionListener() {
            @Override
            public void onEdit(Product product) {
                onEditClick(product);
            }

            @Override
            public void onDelete(Product product) {
                onDeleteClick(product);
            }

            @Override
            public void onAdjustStock(Product product) {
                onAdjustClick(product);
            }

            @Override
            public void onLayout(Product product) {
                onLayoutClick(product);
            }
        });

    }


    private void setupFirebase() {
        productRef = FirebaseDatabase.getInstance().getReference("Products");
        stockRef = FirebaseDatabase.getInstance().getReference("WarehouseStock").child(warehouseId);

        warehouseRef = FirebaseDatabase.getInstance()
                .getReference("Warehouses")
                .child(warehouseId);
    }

    private void loadWarehouseInfo() {

        warehouseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    Warehouse warehouse = snapshot.getValue(Warehouse.class);

                    if (warehouse != null) {

                        warehouseName.setText(warehouse.getName());
                        warehouseCode.setText("" + warehouse.getCode());
                        warehouseLocation.setText("" + warehouse.getLocation());

                        if (warehouse.isStatus()) {
                            warehouseStatus.setText("● Active");
                            warehouseStatus.setTextColor(Color.GREEN);
                        } else {
                            warehouseStatus.setText("● Inactive");
                            warehouseStatus.setTextColor(Color.RED);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadProducts() {
        showLoading(true);
        productList.clear();
        warehouseStockMap.clear();
        warehouseMinStock.clear();

        // Load stock first
        stockRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot stockSnapshot) {
                if (!stockSnapshot.exists()) {

                    showLoading(false);

                    recyclerView.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);

                    return;
                }


                List<String> productIds = new ArrayList<>();
                for (DataSnapshot ds : stockSnapshot.getChildren()) {
                    String productId = ds.getKey();
                    productIds.add(productId);

                    int quantity = ds.child("quantity").getValue(Integer.class) != null ?
                            ds.child("quantity").getValue(Integer.class) : 0;
                    int minQuantity = ds.child("minStock").getValue(Integer.class) != null ?
                            ds.child("minStock").getValue(Integer.class) : 0;

                    warehouseStockMap.put(productId, quantity);
                    warehouseMinStock.put(productId, minQuantity);
                }

                // Now fetch products
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                        productList.clear();
                        for (DataSnapshot ds : productSnapshot.getChildren()) {
                            Product product = ds.getValue(Product.class);
                            if (product != null && productIds.contains(product.getProdId())) {
                                productList.add(product);
                            }
                        }
                        showLoading(false);

                        if (productList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            noDataLayout.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noDataLayout.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showLoading(false);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            recyclerView.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.GONE);
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();
        } else {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }
    }



    private void onEditClick(Product product) {
        Toast.makeText(Products.this, "Edit: " + product.getName(), Toast.LENGTH_SHORT).show();
    }
    private void onDeleteClick(Product product) {
        Toast.makeText(Products.this, "Delete: " + product.getName(), Toast.LENGTH_SHORT).show();
    }
    private void onAdjustClick(Product product) {
        Toast.makeText(Products.this, "Adjust Stock: " + product.getName(), Toast.LENGTH_SHORT).show();
    }
    private void onLayoutClick(Product product) {
        Toast.makeText(Products.this, "Layout: " + product.getName(), Toast.LENGTH_SHORT).show();
    }
}
