package com.bright.client.Financial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bright.client.Adapter.PaymentLogAdapter;
import com.bright.client.Model.BankAccount;
import com.bright.client.Model.PaymentLog;
import com.bright.client.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AccountDetail extends AppCompatActivity {

    RecyclerView recyclerView;
    PaymentLogAdapter adapter;
    List<PaymentLog> list;

    private ImageView backButton, refreshButton, balanceVisibility;
    private TextView bankName, balance, accountName, accountNumberBranch, status;

    private boolean isBalanceVisible = false;
    private double currentBalance = 0; // store real value

    private ShimmerFrameLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        initViews();

        balance.setText("Br. *****");
        balanceVisibility.setImageResource(R.drawable.ic_visibility_off);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new PaymentLogAdapter(list);
        recyclerView.setAdapter(adapter);

        String accountId = getIntent().getStringExtra("accountId");

        shimmerLayout.startShimmer();
        shimmerLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        loadAccountDetail(accountId);
        loadData(accountId);

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        refreshButton.setOnClickListener(v -> {
            loadAccountDetail(accountId);
            loadData(accountId);
        });

        balanceVisibility.setOnClickListener(v -> {

            isBalanceVisible = !isBalanceVisible;

            if (isBalanceVisible) {
                DecimalFormat format = new DecimalFormat("#,###");
                balance.setText("Br. " + format.format(currentBalance));
                balanceVisibility.setImageResource(R.drawable.ic_visibility); // 👁️
            } else {
                balance.setText("Br. *****");
                balanceVisibility.setImageResource(R.drawable.ic_visibility_off); // 🙈
            }
        });
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        refreshButton = findViewById(R.id.btn_refresh);
        balanceVisibility = findViewById(R.id.balance_visibility_icon);

        bankName = findViewById(R.id.bank_name);
        balance = findViewById(R.id.balance);
        accountName = findViewById(R.id.account_name);
        accountNumberBranch = findViewById(R.id.account_number_branch);
        status = findViewById(R.id.status);
        shimmerLayout = findViewById(R.id.shimmer_layout);
    }


    private void loadAccountDetail(String accountId) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("BankAccounts")
                .child(accountId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    BankAccount account = snapshot.getValue(BankAccount.class);

                    if (account != null) {

                        accountName.setText(account.getAccountName());

                        bankName.setText(account.getBankName());

                        accountNumberBranch.setText(
                                account.getAccountNumber() + " ● " +
                                        account.getBranch()
                        );

                        //Balance
                        if (account.getBalance() != null) {
                            currentBalance = account.getBalance(); // save value

                            if (isBalanceVisible) {
                                DecimalFormat format = new DecimalFormat("#,###");
                                balance.setText("Br. " + format.format(currentBalance));
                            } else {
                                balance.setText("Br. *****");
                            }
                        } else {
                            balance.setText("Br. *****");
                        }


                        // status
                        if (account.isStatus()) {
                            status.setText("Active");
                            status.setTextColor(Color.GREEN);
                        } else {
                            status.setText("Inactive");
                            status.setTextColor(Color.RED);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadData(String accountId) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("PaymentLogs");

        ref.orderByChild("accountId")
                .equalTo(accountId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            PaymentLog log = data.getValue(PaymentLog.class);
                            list.add(log);
                        }

                        adapter.notifyDataSetChanged();

                        // STOP shimmer
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}