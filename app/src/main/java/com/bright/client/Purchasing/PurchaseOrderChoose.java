package com.bright.client.Purchasing;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Adapter.ChoosePurchaseListAdapter;
import com.bright.client.Model.Product;
import com.bright.client.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderChoose extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerLayout;
    private LinearLayout btnContinue;
    private ImageView backButton, btnRefresh;

    private ChoosePurchaseListAdapter adapter;
    private final List<Product> productList = new ArrayList<>();

    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order_choose);

        setupStatusBar();
        initViews();
        setupRecycler();
        setupClickListeners();

        productRef = FirebaseDatabase.getInstance().getReference("Products");

        loadProducts();
    }

    // ================= UI SETUP =================
    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_choose_purchase);
        shimmerLayout = findViewById(R.id.shimmer_layout);
        btnContinue = findViewById(R.id.btn_continue);
        backButton = findViewById(R.id.back_button);
//        btnRefresh = findViewById(R.id.btn_refresh);
    }

    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new ChoosePurchaseListAdapter(this, productList);
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {

        backButton.setOnClickListener(v -> finish());

//        btnRefresh.setOnClickListener(v -> {
//            rotateRefreshIcon();
//            loadProducts();
//        });
    }

    // ================= LOAD DATA =================
    private void loadProducts() {

        showLoading(true);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }

                adapter.notifyDataSetChanged();
                showLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
            }
        });
    }

    // ================= LOADING =================
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            shimmerLayout.startShimmer();
            shimmerLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // ================= REFRESH ANIMATION =================
    private void rotateRefreshIcon() {
        RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotate.setDuration(500);
        btnRefresh.startAnimation(rotate);
    }
}