package com.bright.client.Assets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DisposableAssets extends AppCompatActivity implements AssetAdapter.OnProductClickListener {

    private AssetAdapter assetAdapter;
    private List<Asset> assetList;

    FloatingActionButton btnAddProduct;
    DatabaseReference assetRef;

    private ImageView filterButton;

    LinearLayout noData;

    String assignedPhone = "";
    String position = "";

    boolean showAllDisposable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disposable_asset);
        // Status bar setup
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        noData = findViewById(R.id.no_data);

        // Get intent data
        Intent intent = getIntent();
        assignedPhone = intent.getStringExtra("assigned");  // assigned phone number
        position = intent.getStringExtra("position");

        // Firebase reference
        assetRef = FirebaseDatabase.getInstance().getReference("Assets");

        // Recycler setup
        RecyclerView productRecycler = findViewById(R.id.recycler_disposable_assets);
        assetList = new ArrayList<>();
        assetAdapter = new AssetAdapter(this, assetList, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        productRecycler.setLayoutManager(linearLayoutManager);
        productRecycler.setAdapter(assetAdapter);
        productRecycler.setNestedScrollingEnabled(false);

        // ✅ LOAD DATA FIRST TIME
        loadProducts();


        // Buttons
        btnAddProduct = findViewById(R.id.add_asset);
        filterButton = findViewById(R.id.disposable_asset_filter);

        if ("General Manager".equals(position)) {
            filterButton.setVisibility(View.VISIBLE);
        } else {
            filterButton.setVisibility(View.GONE);
        }


        filterButton.setOnClickListener(v -> {
            showAllDisposable = !showAllDisposable;

            if (showAllDisposable) {
                Toast.makeText(this, "Showing all disposable assets", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Showing my disposable assets", Toast.LENGTH_SHORT).show();
            }

            filterButton.setColorFilter(
                    showAllDisposable ? Color.RED : Color.GRAY
            );


            loadProducts();
        });


        btnAddProduct.setOnClickListener(v -> {
            Intent intent1 = new Intent(DisposableAssets.this, AddAssetScan.class);
            startActivity(intent1);
        });


    }

    private void loadProducts() {

        assetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                assetList.clear();

                for (DataSnapshot assetSnapshot : snapshot.getChildren()) {

                    Asset asset = assetSnapshot.getValue(Asset.class);
                    if (asset == null) continue;

                    // ✅ ONLY Disposable Assets
                    if (!"Disposable Asset".equalsIgnoreCase(asset.getType())) {
                        continue;
                    }

                    // ✅ FILTER LOGIC
                    if (showAllDisposable) {
                        // Show ALL disposable assets
                        assetList.add(asset);
                    } else {
                        // Show ONLY assets assigned to this phone
                        if (assignedPhone != null
                                && !assignedPhone.isEmpty()
                                && assignedPhone.equalsIgnoreCase(asset.getAssignedTo())) {

                            assetList.add(asset);
                        }
                    }
                }

                assetAdapter.notifyDataSetChanged();

                RecyclerView productRecycler = findViewById(R.id.recycler_disposable_assets);

                // ✅ SHOW / HIDE NO DATA VIEW
                if (assetList.isEmpty()) {
                    noData.setVisibility(View.VISIBLE);
                    productRecycler.setVisibility(View.GONE);
                } else {
                    noData.setVisibility(View.GONE);
                    productRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisposableAssets.this,
                        "Failed to load assets: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onLayoutClick(Asset assets, int position) {
        Intent intent = new Intent(DisposableAssets.this, AssetDetail.class);
        intent.putExtra("assetId", assets.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Asset assets, int position) {
        // Delete logic if needed
    }

    @Override
    public void onEditClick(Asset assets, int position) {
        Intent intent = new Intent(DisposableAssets.this, AddAssets.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("assetId", assets.getId());
        startActivity(intent);
    }


    @Override
    public void onAcceptClick(Asset asset, int position) {


        DatabaseReference assetRef =
                FirebaseDatabase.getInstance().getReference("Assets")
                        .child(asset.getId());


        long time = System.currentTimeMillis();

        // history
        assetRef.child("history").push().setValue(new HashMap<String, Object>() {{
            put("action", "ASSIGNMENT ACCEPTED");
            put("from", asset.getAssignedById());
            put("to", asset.getAssignedTo());
            put("timestamp", time);


        }});


        assetRef.child("assignmentStatus").removeValue();
        assetRef.child("assignedBy").removeValue();
        assetRef.child("assignedById").removeValue();
        assetRef.child("assignedById").removeValue();


        Toast.makeText(this, "Asset accepted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRejectClick(Asset asset, int position) {

        DatabaseReference assetRef =
                FirebaseDatabase.getInstance().getReference("Assets")
                        .child(asset.getId());

        long time = System.currentTimeMillis();

        Map<String, Object> clearData = new HashMap<>();
        clearData.put("assignedTo", asset.getAssignedById());
        clearData.put("assigneeName", asset.getAssignedBy());
        clearData.put("assignmentStatus", null);

        clearData.put("assignedBy", null);
        clearData.put("assignedById", null);
        clearData.put("assignedAt", null);

        assetRef.updateChildren(clearData);

        // history
        assetRef.child("history").push().setValue(new HashMap<String, Object>() {{
            put("action", "ASSIGNMENT REJECTED");
            put("from", asset.getAssignedById());
            put("to", asset.getAssignedTo());
            put("timestamp", time);
        }});

        Toast.makeText(this, "Asset rejected", Toast.LENGTH_SHORT).show();
    }


}