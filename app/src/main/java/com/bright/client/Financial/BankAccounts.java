package com.bright.client.Financial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Adapter.BankAccountAdapter;
import com.bright.client.Model.BankAccount;
import com.bright.client.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class BankAccounts extends AppCompatActivity {

    // UI
    private RecyclerView recyclerView;
    private View fabAccount, btnBack, btnRefresh;
    private ShimmerFrameLayout shimmerLayout;

    // Data
    private final List<BankAccount> accountList = new ArrayList<>();
    private BankAccountAdapter adapter;

    // Firebase
    private DatabaseReference reference;
    private ValueEventListener listener;

    private long shimmerStartTime;
    private static final long MIN_SHIMMER_TIME = 600; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_accounts);

        setupStatusBar();
        initViews();
        setupRecycler();
        setupFirebase();
        setupActions();

        loadAccounts();
    }

    // ---------------- STATUS BAR ---------------- //
    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    // ---------------- INIT ---------------- //
    private void initViews() {
        recyclerView = findViewById(R.id.bank_recycler);
        fabAccount = findViewById(R.id.fab_add_account);
        btnBack = findViewById(R.id.back_button);
        btnRefresh = findViewById(R.id.btn_refresh);
        shimmerLayout = findViewById(R.id.shimmer_layout);
    }

    // ---------------- RECYCLER ---------------- //
    private void setupRecycler() {
        adapter = new BankAccountAdapter(this, accountList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        // ✅ CLICK HANDLER
        adapter.setOnItemClickListener(account -> {

            Intent intent = new Intent(BankAccounts.this, AccountDetail.class);
            intent.putExtra("accountId", account.getAccountId());
            startActivity(intent);

        });
    }

    // ---------------- FIREBASE ---------------- //
    private void setupFirebase() {
        reference = FirebaseDatabase.getInstance().getReference("BankAccounts");
    }

    // ---------------- ACTIONS ---------------- //
    private void setupActions() {

        // Add account
        fabAccount.setOnClickListener(v ->
                startActivity(new Intent(this, AddBankAccount.class))
        );

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Refresh button
        btnRefresh.setOnClickListener(v -> {
            v.animate().rotationBy(360).setDuration(500).start();
            refreshData();
        });
    }

    // ---------------- LOAD DATA ---------------- //
    private void loadAccounts() {

        showLoading(true);

        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                accountList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    BankAccount account = ds.getValue(BankAccount.class);
                    if (account != null) {
                        accountList.add(account);
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

    // ---------------- REFRESH ---------------- //
    private void refreshData() {

        showLoading(true);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                accountList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    BankAccount account = ds.getValue(BankAccount.class);
                    if (account != null) {
                        accountList.add(account);
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

    // ---------------- LOADING ---------------- //
    private void showLoading(boolean isLoading) {

        if (isLoading) {
            shimmerStartTime = System.currentTimeMillis();

            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();

            recyclerView.setVisibility(View.GONE);
        } else {

            long elapsed = System.currentTimeMillis() - shimmerStartTime;

            if (elapsed < MIN_SHIMMER_TIME) {
                shimmerLayout.postDelayed(this::hideShimmer, MIN_SHIMMER_TIME - elapsed);
            } else {
                hideShimmer();
            }
        }
    }

    // ---------------- LIFECYCLE ---------------- //
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (listener != null) {
            reference.removeEventListener(listener);
        }
    }

    private void hideShimmer() {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);

        recyclerView.setVisibility(View.VISIBLE);
    }
}