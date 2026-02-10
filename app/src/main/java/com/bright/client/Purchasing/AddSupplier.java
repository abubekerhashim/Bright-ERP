package com.bright.client.Purchasing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bright.client.Model.Employee;
import com.bright.client.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class AddSupplier extends AppCompatActivity {

    private EditText etName, etPhone, etAddress;
    private EditText etBankName, etAccountName, etAccountNumber;
    private ChipGroup supplierChips;
    private AppCompatButton btnSave;
    private FrameLayout loading;

    private DatabaseReference supplierRef;
    private DatabaseReference supplierSeqRef;
    private AppCompatButton btnAddCategory;

    private ImageView backButton;

    private Employee currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier);

        setupStatusBar();
        initViews();
        loadCurrentUser();
        initFirebase();
        setupSaveButton();
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
        backButton.setOnClickListener(v -> onBackPressed());

    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        etName = findViewById(R.id.supplier_name);
        etPhone = findViewById(R.id.supplier_Phone);
        etAddress = findViewById(R.id.supplier_address);

        etBankName = findViewById(R.id.bank_name);
        etAccountName = findViewById(R.id.account_name);
        etAccountNumber = findViewById(R.id.account_number);

        supplierChips = findViewById(R.id.supplier_chips);
        btnSave = findViewById(R.id.btn_create_supplier);
        btnAddCategory = findViewById(R.id.btn_add_category); // ✅ ADD
        loading = findViewById(R.id.loading);

        backButton = findViewById(R.id.back_button);
    }


    private void loadCurrentUser() {
        Paper.init(this);
        currentUser = Paper.book().read("currentUser");
    }

    private void initFirebase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        supplierRef = db.getReference("Suppliers");
        supplierSeqRef = db.getReference("Sequences")
                .child("supplierSeq");
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveSupplier());
    }

    private void saveSupplier() {

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (!validate(name, phone)) return;

        showLoading();

        supplierSeqRef.runTransaction(new Transaction.Handler() {

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
                    onError("Failed to generate supplier ID");
                    return;
                }

                int seq = snapshot.getValue(Integer.class);
                String supplierId =
                        String.format("HALI-SUP-%06d", seq);

                saveSupplierToDatabase(supplierId, phone);
            }
        });
    }

    private void saveSupplierToDatabase(
            String supplierId, String phone) {

        List<String> categories = new ArrayList<>();

        for (int id : supplierChips.getCheckedChipIds()) {
            Chip chip = supplierChips.findViewById(id);
            categories.add(chip.getText().toString().trim());
        }

        String categoryString = android.text.TextUtils.join(", ", categories);

        HashMap<String, Object> supplier = new HashMap<>();
        supplier.put("supplierId", supplierId);
        supplier.put("name", etName.getText().toString().trim());
        supplier.put("userId", phone);
        supplier.put("address", etAddress.getText().toString().trim());
        supplier.put("accName", etAccountName.getText().toString().trim());
        supplier.put("accNum", etAccountNumber.getText().toString().trim());
        supplier.put("bankName", etBankName.getText().toString().trim());
        supplier.put("categories", categoryString);
        supplier.put("registeredByName",
                currentUser.getFirstName() + " " +
                        currentUser.getMiddleName());
        supplier.put("registeredByPhone",
                currentUser.getUserId());
        supplier.put("status", true);

        supplierRef.child(phone)
                .setValue(supplier)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    Toast.makeText(
                            this,
                            "Supplier Registered",
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

        if (supplierChips.getCheckedChipIds().isEmpty()) {
            Toast.makeText(this,
                    "Select at least one category",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showAddCategoryDialog() {

        EditText input = new EditText(this);
        input.setHint("Category name");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Add Category")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {

                    String category = input.getText()
                            .toString().trim();

                    if (category.isEmpty()) {
                        Toast.makeText(this,
                                "Category required",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addChip(category);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void addChip(String text) {

        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChecked(true);
        chip.setCloseIconVisible(true);

        chip.setOnCloseIconClickListener(v ->
                supplierChips.removeView(chip));

        supplierChips.addView(chip);
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
