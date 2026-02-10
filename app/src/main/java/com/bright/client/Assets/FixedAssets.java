package com.bright.client.Assets;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bright.client.Adapter.AssetAdapter;
import com.bright.client.Model.Asset;
import com.bright.client.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedAssets extends AppCompatActivity implements AssetAdapter.OnProductClickListener {

    private AssetAdapter assetAdapter;
    private List<Asset> assetList;

    private RecyclerView recyclerAssets;
    private LinearLayout noDataLayout;
    private SwipeRefreshLayout swipeRefresh;

    private FloatingActionButton btnAddAsset;
    private ImageView filterButton, backButton;

    private DatabaseReference assetRef;

    private String assignedPhone = "";
    private String position = "";

    private boolean showAllFixed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_asset);

        initViews();
        setupStatusBar();
        setupRecyclerView();
        getIntentData();
        setupFirebase();
        setupButtons();
        loadProducts();
    }

    // ------------------ Initialization ------------------
    private void initViews() {
        recyclerAssets = findViewById(R.id.recycler_fixed_assets);
        noDataLayout = findViewById(R.id.no_data);
        swipeRefresh = findViewById(R.id.swipe_refresh); // add SwipeRefreshLayout in XML wrapping RecyclerView
        btnAddAsset = findViewById(R.id.add_asset);
        filterButton = findViewById(R.id.fixed_asset_filter);
        backButton = findViewById(R.id.back_button);
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }

    private void setupRecyclerView() {
        assetList = new ArrayList<>();
        assetAdapter = new AssetAdapter(this, assetList, this);

        recyclerAssets.setLayoutManager(new LinearLayoutManager(this));
        recyclerAssets.setAdapter(assetAdapter);
        recyclerAssets.setNestedScrollingEnabled(false);
    }

    private void getIntentData() {
        assignedPhone = getIntent().getStringExtra("assigned");
        position = getIntent().getStringExtra("position");
    }

    private void setupFirebase() {
        assetRef = FirebaseDatabase.getInstance().getReference("Assets");
    }

    private void setupButtons() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Add asset button
        btnAddAsset.setOnClickListener(v -> startActivity(new Intent(FixedAssets.this, AddAssetScan.class)));

        // Filter button (only visible for General Manager)
        filterButton.setVisibility("General Manager".equals(position) ? View.VISIBLE : View.GONE);
        filterButton.setOnClickListener(v -> {
            showAllFixed = !showAllFixed;
            filterButton.setColorFilter(showAllFixed ? Color.RED : Color.GRAY);
            Toast.makeText(this, showAllFixed ? "Showing all Fixed assets" : "Showing my Fixed assets", Toast.LENGTH_SHORT).show();
            loadProducts();
        });

        // Swipe to refresh
        swipeRefresh.setOnRefreshListener(this::loadProducts);
    }

    // ------------------ Load Assets ------------------
    private void loadProducts() {
        swipeRefresh.setRefreshing(true);

        assetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assetList.clear();

                for (DataSnapshot assetSnapshot : snapshot.getChildren()) {
                    Asset asset = assetSnapshot.getValue(Asset.class);
                    if (asset == null || !"Fixed Asset".equalsIgnoreCase(asset.getType())) continue;

                    if (showAllFixed) {
                        assetList.add(asset);
                    } else if (assignedPhone != null && assignedPhone.equalsIgnoreCase(asset.getAssignedTo())) {
                        assetList.add(asset);
                    }
                }

                assetAdapter.notifyDataSetChanged();

                noDataLayout.setVisibility(assetList.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerAssets.setVisibility(assetList.isEmpty() ? View.GONE : View.VISIBLE);

                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FixedAssets.this, "Failed to load assets: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });

    }

    // ------------------ Adapter Callbacks ------------------
    @Override
    public void onLayoutClick(Asset asset, int position) {
        Intent intent = new Intent(FixedAssets.this, AssetDetail.class);
        intent.putExtra("assetId", asset.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Asset asset, int position) {
        // Optional delete functionality
    }

    @Override
    public void onEditClick(Asset asset, int position) {
        Intent intent = new Intent(FixedAssets.this, AddAssets.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("assetId", asset.getId());
        startActivity(intent);
    }

    @Override
    public void onAcceptClick(Asset asset, int position) {
        DatabaseReference ref = assetRef.child(asset.getId());
        long timestamp = System.currentTimeMillis();

        // Update history
        ref.child("history").push().setValue(new HashMap<String, Object>() {{
            put("action", "ASSIGNMENT ACCEPTED");
            put("from", asset.getAssignedById());
            put("to", asset.getAssignedTo());
            put("timestamp", timestamp);
        }});

        // Clear assignment
        ref.child("assignmentStatus").removeValue();
        ref.child("assignedBy").removeValue();
        ref.child("assignedById").removeValue();

        Toast.makeText(this, "Asset accepted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRejectClick(Asset asset, int position) {
        DatabaseReference ref = assetRef.child(asset.getId());
        long timestamp = System.currentTimeMillis();

        Map<String, Object> clearData = new HashMap<>();
        clearData.put("assignedTo", asset.getAssignedById());
        clearData.put("assigneeName", asset.getAssignedBy());
        clearData.put("assignmentStatus", null);
        clearData.put("assignedBy", null);
        clearData.put("assignedById", null);
        clearData.put("assignedAt", null);

        ref.updateChildren(clearData);

        // Add to history
        ref.child("history").push().setValue(new HashMap<String, Object>() {{
            put("action", "ASSIGNMENT REJECTED");
            put("from", asset.getAssignedById());
            put("to", asset.getAssignedTo());
            put("timestamp", timestamp);
        }});

        Toast.makeText(this, "Asset rejected", Toast.LENGTH_SHORT).show();
    }
}
