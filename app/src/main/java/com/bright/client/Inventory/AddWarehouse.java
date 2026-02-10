package com.bright.client.Inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bright.client.Model.Employee;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class AddWarehouse extends AppCompatActivity {

    private EditText warehouseName, warehouseLocation, warehouseNote;

    private DatabaseReference warehouseRef, seqRef;
    private Employee currentUser;
    private ImageView backButton;

    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_warehouse);

        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        initViews();
        loadCurrentUser();
        initFirebase();

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        findViewById(R.id.btn_create_warehouse).setOnClickListener(view -> createWarehouse());
    }

    private void initViews() {
        warehouseName = findViewById(R.id.warehouse_name);
        warehouseLocation = findViewById(R.id.warehouse_location);
        warehouseNote = findViewById(R.id.warehouse_note);

        backButton = findViewById(R.id.back_button);
    }

    private void loadCurrentUser() {
        Paper.init(this);
        currentUser = Paper.book().read("currentUser");
    }

    private void initFirebase() {
        warehouseRef = FirebaseDatabase.getInstance().getReference("Warehouses");
        seqRef = FirebaseDatabase.getInstance().getReference("Sequences");
    }

    private boolean validateInput(String name, String location) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            warehouseName.setError("Warehouse name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(location)) {
            warehouseLocation.setError("Warehouse location is required");
            isValid = false;
        }

        return isValid;
    }

    private void createWarehouse() {
        String name = warehouseName.getText().toString().trim();
        String location = warehouseLocation.getText().toString().trim();
        String note = warehouseNote.getText().toString().trim();

        if (!validateInput(name, location)) return;

        showLoading("Saving Warehouse...");

        // Get current sequence
        seqRef.child("warehouseSeq").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentSeq = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
                long nextSeq = currentSeq + 1;

                String code = String.format("HALI-WH-%06d", nextSeq);

                HashMap<String, Object> warehouseData = new HashMap<>();
                warehouseData.put("code", code);
                warehouseData.put("name", name);
                warehouseData.put("location", location);
                warehouseData.put("note", note);
                warehouseData.put("registeredByName",
                        currentUser.getFirstName() + " " + currentUser.getMiddleName());
                warehouseData.put("registeredByPhone", currentUser.getUserId());
                warehouseData.put("status", true);

                // Save warehouse and update sequence
                warehouseRef.child(code).setValue(warehouseData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                seqRef.child("warehouseSeq").setValue(nextSeq);
                                Toast.makeText(AddWarehouse.this, "Warehouse created successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                showError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                            }
                            hideLoading();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showError(error.getMessage());
                hideLoading();
            }
        });
    }


    /** Show error toast */
    private void showError(String message) {
        Toast.makeText(AddWarehouse.this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    private void showLoading(String message) {
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
