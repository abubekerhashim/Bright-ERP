package com.bright.client.Inventory;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Adapter.WarehouseAdapter;
import com.bright.client.Model.Warehouse;
import com.bright.client.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Warehouses extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WarehouseAdapter adapter;
    private final List<Warehouse> warehouseList = new ArrayList<>();

    private ImageView btnRefresh, backButton, btnAdd;
    private DatabaseReference warehouseRef;

    private ShimmerFrameLayout shimmerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouses);

        setupStatusBar();
        initViews();
        initFirebase();
        setupRecyclerView();
        setupRefreshButton();

        backButton.setOnClickListener(v -> onBackPressed());
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Warehouses.this, AddWarehouse.class);
            startActivity(intent);
        });

        loadWarehouses();
    }


    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        btnRefresh = findViewById(R.id.btn_refresh);
        backButton = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.recycler_warehouses);
        btnAdd = findViewById(R.id.fab_add_warehouse);
        shimmerLayout = findViewById(R.id.shimmer_layout);
    }



    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new WarehouseAdapter(this, warehouseList);
        recyclerView.setAdapter(adapter);
    }

    private void initFirebase() {
        warehouseRef = FirebaseDatabase.getInstance().getReference("Warehouses");
    }

    private void setupRefreshButton() {
        btnRefresh.setOnClickListener(v -> {
            btnRefresh.setEnabled(false);
            btnRefresh.animate().rotationBy(360).setDuration(600).start();
            loadWarehouses();
        });
    }


    private void loadWarehouses() {

        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        recyclerView.setVisibility(View.GONE);
        btnRefresh.setEnabled(false);

        warehouseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                warehouseList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Warehouse warehouse = ds.getValue(Warehouse.class);
                    if (warehouse != null) {
                        warehouseList.add(warehouse);
                    }
                }

                adapter.notifyDataSetChanged();

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                btnRefresh.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                btnRefresh.setEnabled(true);

                Toast.makeText(Warehouses.this,
                        "Failed to load warehouses",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Called when Raw Material button is clicked
    public void onRawMaterialClick(Warehouse warehouse) {
        Toast.makeText(this, "Raw Material clicked: " + warehouse.getName(),
                Toast.LENGTH_SHORT).show();

        // TODO: Open Raw Material Activity
        // Intent intent = new Intent(this, RawMaterialActivity.class);
        // intent.putExtra("warehouseId", warehouse.getCode());
        // startActivity(intent);
    }

    // Called when Warehouse button is clicked
    public void onWarehouseClick(Warehouse warehouse) {

         Intent intent = new Intent(this, Products.class);
         intent.putExtra("warehouseId", warehouse.getCode());
         startActivity(intent);
    }


}
