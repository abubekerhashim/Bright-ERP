package com.bright.client.Financial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddBankAccount extends AppCompatActivity {

    private static final String ACCOUNT_SEQ = "accountSeq";

    private TextView bankName, accountNumber, accountName, accountBranch, accountBalance;
    private ImageView backButton;
    private LinearLayout btnCreate;

    private AlertDialog loadingDialog;

    private DatabaseReference sequenceRef;
    private DatabaseReference bankRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);


        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        initViews();

        sequenceRef = FirebaseDatabase.getInstance().getReference("Sequences");
        bankRef = FirebaseDatabase.getInstance().getReference("BankAccounts");

        backButton.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {

        String bank = bankName.getText().toString().trim();
        String number = accountNumber.getText().toString().trim();
        String name = accountName.getText().toString().trim();
        String branch = accountBranch.getText().toString().trim();
        String balanceStr = accountBalance.getText().toString().trim();

        if (bank.isEmpty() || number.isEmpty() || name.isEmpty() || branch.isEmpty() || balanceStr.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double balance;
        try {
            balance = Double.parseDouble(balanceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid balance", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading();

        sequenceRef.child(ACCOUNT_SEQ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Long seq = snapshot.getValue(Long.class);
                if (seq == null) seq = 0L;

                long newSeq = seq + 1;

                // update sequence
                sequenceRef.child(ACCOUNT_SEQ).setValue(newSeq);

                String accountId = String.format("HALI-ACC-%06d", newSeq);

                HashMap<String, Object> map = new HashMap<>();
                map.put("accountId", accountId);
                map.put("bankName", bank);
                map.put("accountNumber", number);
                map.put("accountName", name);
                map.put("branch", branch);
                map.put("balance", balance);
                map.put("status", true);
                map.put("timestamp", System.currentTimeMillis());

                bankRef.child(accountId).setValue(map);

                hideLoading();
                Toast.makeText(AddBankAccount.this, "Account Created: " + accountId, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoading();
                Toast.makeText(AddBankAccount.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        bankName = findViewById(R.id.bank_name);
        accountNumber = findViewById(R.id.account_number);
        accountName = findViewById(R.id.account_name);
        accountBranch = findViewById(R.id.account_branch);
        accountBalance = findViewById(R.id.account_balance);

        backButton = findViewById(R.id.back_button);
        btnCreate = findViewById(R.id.btn_create_account);
    }

    private void showLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_loading, null);

        builder.setView(view);
        builder.setCancelable(false);

        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}