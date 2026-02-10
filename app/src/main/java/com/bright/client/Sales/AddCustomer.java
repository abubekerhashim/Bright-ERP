package com.bright.client.Sales;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bright.client.Model.Employee;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;

import io.paperdb.Paper;

public class AddCustomer extends AppCompatActivity {

    private EditText etName, etPhone, etAddress, etBankName, etAccountNumber;
    private Spinner spType;
    private AppCompatButton btnSave;

    private DatabaseReference customerRef;
    private DatabaseReference customerSeqRef;

    private Employee currentUser;

    private FrameLayout loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        setupStatusBar();
        initViews();
        loadCurrentUser();
        initFirebase();
        setupCustomerTypeSpinner();
        setupSaveButton();
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        etName = findViewById(R.id.customer_name);
        etPhone = findViewById(R.id.customer_phone);
        etAddress = findViewById(R.id.customer_address);
        etBankName = findViewById(R.id.bank_name);
        etAccountNumber = findViewById(R.id.account_number);

        spType = findViewById(R.id.customer_type_spinner);
        btnSave = findViewById(R.id.btn_create_customer);

        loading = findViewById(R.id.loading);

    }

    private void setupCustomerTypeSpinner() {
        String[] types = {"End User", "Broker", "Retailer", "Wholesale"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                types
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spType.setAdapter(adapter);
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveCustomer());
    }

    private void loadCurrentUser() {
        Paper.init(this);
        currentUser = Paper.book().read("currentUser");
    }

    private void initFirebase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        customerRef = db.getReference("Customers");
        customerSeqRef = db.getReference("Sequences").child("customerSeq");
    }

    private void saveCustomer() {

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (!validate(name, phone)) return;

        showLoading();

        customerSeqRef.runTransaction(new Transaction.Handler() {

            @NonNull
            @Override
            public Transaction.Result doTransaction(
                    @NonNull MutableData currentData) {

                Integer seq = currentData.getValue(Integer.class);
                if (seq == null) seq = 0;

                currentData.setValue(seq + 1);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(
                    @Nullable DatabaseError error,
                    boolean committed,
                    @Nullable DataSnapshot snapshot) {

                if (error != null || !committed) {
                    onError("Failed to generate customer ID");
                    return;
                }

                int seq = snapshot.getValue(Integer.class);
                String customerId =
                        String.format("HALI-CUS-%06d", seq);

                saveCustomerToDatabase(customerId, phone);
            }
        });
    }

    private void saveCustomerToDatabase(
            String customerId, String phone) {

        HashMap<String, Object> customer = new HashMap<>();
        customer.put("customerId", customerId);
        customer.put("name", etName.getText().toString().trim());
        customer.put("userId", phone);
        customer.put("address", etAddress.getText().toString().trim());
        customer.put("accName", etBankName.getText().toString().trim());
        customer.put("accNum", etAccountNumber.getText().toString().trim());
        customer.put("type", spType.getSelectedItem().toString());
        customer.put("registeredByName",
                currentUser.getFirstName() + " " +
                        currentUser.getMiddleName());
        customer.put("registeredByPhone",
                currentUser.getUserId());
        customer.put("status", true);

        customerRef.child(phone)
                .setValue(customer)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(
                            this,
                            "Customer Registered",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        onError(e.getMessage()));
    }

    private boolean validate(String name, String phone) {

        if (name.isEmpty()) {
            etName.setError("Required");
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Required");
            return false;
        }

        return true;
    }

    private void onError(String message) {
        hideLoading();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
    }

    private void hideLoading() {
        loading.setVisibility(View.GONE);
        btnSave.setEnabled(true);
    }

}
