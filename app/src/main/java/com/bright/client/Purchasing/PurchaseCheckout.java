package com.bright.client.Purchasing;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Adapter.ProductSpinnerAdapter;
import com.bright.client.Adapter.PurchaseItemAdapter;
import com.bright.client.Model.Product;
import com.bright.client.Model.PurchaseItem;
import com.bright.client.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PurchaseCheckout extends AppCompatActivity {

    private String supplierId, supplierName, orderDate, receiveDate, temporaryId;

    private RecyclerView recyclerView;
    private PurchaseItemAdapter adapter;
    private List<PurchaseItem> purchaseItems = new ArrayList<>();

    private TextView totalPriceText;
    private LinearLayout btnContinue;

    private List<Product> productList = new ArrayList<>();
    private DatabaseReference purchaseDraftRef;

    double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_checkout);

        initStatusBar();
        getIntentData();
        initViews();
        setupRecyclerView();
        initFirebaseReferences();

        loadProducts();
        loadPurchaseItems();

        findViewById(R.id.fab_add_item).setOnClickListener(v -> showAddItemDialog());

        btnContinue.setOnClickListener(v -> continueToPayment());
    }

    private void initStatusBar() {
        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void getIntentData() {
        supplierId = getIntent().getStringExtra("supplierId");
        supplierName = getIntent().getStringExtra("supplierName");
        orderDate = getIntent().getStringExtra("orderDate");
        receiveDate = getIntent().getStringExtra("receiveDate");
        temporaryId = getIntent().getStringExtra("temporaryId");
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_choose_purchase);
        totalPriceText = findViewById(R.id.total_price);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PurchaseItemAdapter(this, purchaseItems);
        recyclerView.setAdapter(adapter);
    }

    private void initFirebaseReferences() {
        purchaseDraftRef = FirebaseDatabase.getInstance()
                .getReference("PurchaseDraft")
                .child(temporaryId)
                .child("items");
    }

    private void loadProducts() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    if (product != null) productList.add(product);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadPurchaseItems() {
        purchaseDraftRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchaseItems.clear();
                total = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PurchaseItem item = ds.getValue(PurchaseItem.class);
                    if (item != null) {
                        purchaseItems.add(item);
                        total += item.getTotalPrice();
                    }
                }
                adapter.notifyDataSetChanged();
                totalPriceText.setText(NumberFormat.getInstance().format(total) + " Birr");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showAddItemDialog() {
        if (productList.isEmpty()) {
            Toast.makeText(this, "Products not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_purchase_item);

        Spinner spinnerProduct = dialog.findViewById(R.id.select_product);
        EditText inputQuantity = dialog.findViewById(R.id.input_quantity);
        EditText inputPrice = dialog.findViewById(R.id.input_price);
        LinearLayout btnAdd = dialog.findViewById(R.id.btn_add_item);

        spinnerProduct.setAdapter(new ProductSpinnerAdapter(this, productList));

        btnAdd.setOnClickListener(v -> {
            int pos = spinnerProduct.getSelectedItemPosition();
            if (pos == Spinner.INVALID_POSITION) {
                Toast.makeText(this, "Select a product", Toast.LENGTH_SHORT).show();
                return;
            }

            String qtyStr = inputQuantity.getText().toString().trim();
            String priceStr = inputPrice.getText().toString().trim();
            if (qtyStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Fill quantity and price", Toast.LENGTH_SHORT).show();
                return;
            }

            Product selected = productList.get(pos);
            int quantity = Integer.parseInt(qtyStr);
            double unitPrice = Double.parseDouble(priceStr);
            double total = quantity * unitPrice;

            HashMap<String, Object> map = new HashMap<>();
            map.put("name", selected.getName());
            map.put("image", selected.getImageUrl());
            map.put("model", selected.getModel());
            map.put("unit", selected.getUnit());
            map.put("prodId", selected.getProdId());
            map.put("quantity", quantity);
            map.put("unitPrice", unitPrice);
            map.put("totalPrice", total);

            purchaseDraftRef.child(selected.getProdId())
                    .setValue(map)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(R.drawable.rounded_background);
        }
    }

    private void continueToPayment() {
        if (temporaryId == null || temporaryId.isEmpty()) {
            Toast.makeText(this, "Temporary ID missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("supplierId", supplierId);
        orderMap.put("supplierName", supplierName);
        orderMap.put("orderDate", orderDate);
        orderMap.put("receiveDate", receiveDate);
        orderMap.put("totalPrice", total);

        FirebaseDatabase.getInstance()
                .getReference("PurchaseDraft")
                .child(temporaryId)
                .updateChildren(orderMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, PurchasePayment.class)
                                .putExtra("temporaryId", temporaryId));
                    } else {
                        Toast.makeText(this, "Failed to save order info", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}