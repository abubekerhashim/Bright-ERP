package com.bright.client.Inventory;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Adapter.CategoryAdapter;
import com.bright.client.Model.Category;
import com.bright.client.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Categories extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    private ImageView backButton, btnRefresh;
    private DatabaseReference categoryRef;
    private FloatingActionButton addCategory;
    private ShimmerFrameLayout shimmerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        setupStatusBar();
        initViews();
        setupRecyclerView();
        setupFirebase();
        setupActions();

        loadCategories();
    }


    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        btnRefresh = findViewById(R.id.btn_refresh);
        recyclerView = findViewById(R.id.recycler_categories);
        addCategory = findViewById(R.id.add_categories);
        shimmerLayout = findViewById(R.id.shimmer_layout);
    }


    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(adapter);
    }

    private void setupFirebase() {
        categoryRef = FirebaseDatabase.getInstance()
                .getReference("Categories");
    }

    private void setupActions() {

        backButton.setOnClickListener(v -> finish());

        addCategory.setOnClickListener(v -> {
            Intent intent = new Intent(Categories.this, AddCategory.class);
            startActivity(intent);
        });

        btnRefresh.setOnClickListener(v -> {
            btnRefresh.setRotation(0f); // IMPORTANT: reset

            ObjectAnimator animator =
                    ObjectAnimator.ofFloat(btnRefresh, "rotation", 0f, 360f);
            animator.setDuration(500);
            animator.start();

            loadCategories();
        });


    }


    private void loadCategories() {

        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        recyclerView.setVisibility(View.GONE);
        btnRefresh.setEnabled(false);

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categoryList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Category category = ds.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
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
            }
        });
    }


}
