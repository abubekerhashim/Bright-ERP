package com.bright.client.Purchasing;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Adapter.SupplierAdapter;
import com.bright.client.Model.Suppliers;
import com.bright.client.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SuppliersList extends AppCompatActivity {

    private RecyclerView recyclerSuppliers;
    private SupplierAdapter adapter;
    private List<Suppliers> supplierList;

    private ImageView backButton, buttonRefresh;
    private DatabaseReference suppliersRef;

    private ShimmerFrameLayout shimmerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers_list);

        setupStatusBar();
        initViews();
        initFirebase();
        loadSuppliers();
        setupClickListeners();
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        recyclerSuppliers = findViewById(R.id.recycler_suppliers);
        recyclerSuppliers.setLayoutManager(new LinearLayoutManager(this));

        shimmerLayout = findViewById(R.id.shimmer_layout);

        backButton = findViewById(R.id.back_button);
        buttonRefresh = findViewById(R.id.btn_refresh);

        supplierList = new ArrayList<>();
        adapter = new SupplierAdapter(this, supplierList);
        recyclerSuppliers.setAdapter(adapter);
    }


    private void initFirebase() {
        suppliersRef = FirebaseDatabase.getInstance().getReference("Suppliers");
    }

    private void loadSuppliers() {

        // Show shimmer
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        recyclerSuppliers.setVisibility(View.GONE);

        rotateRefreshButton();

        suppliersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                supplierList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Suppliers supplier = ds.getValue(Suppliers.class);
                    if (supplier != null) supplierList.add(supplier);
                }

                adapter.notifyDataSetChanged();

                // Stop shimmer
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);

                recyclerSuppliers.setVisibility(View.VISIBLE);
                buttonRefresh.clearAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                buttonRefresh.clearAnimation();
            }
        });
    }


    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        buttonRefresh.setOnClickListener(v -> loadSuppliers());
    }

    private void rotateRefreshButton() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(buttonRefresh, "rotation", 0f, 360f);
        rotation.setDuration(600); // Rotation duration in ms
        rotation.start();
    }
}
