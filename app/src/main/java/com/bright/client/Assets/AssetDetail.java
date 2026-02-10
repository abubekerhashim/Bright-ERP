package com.bright.client.Assets;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bright.client.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssetDetail extends AppCompatActivity {

    // Views declaration
    ImageView backBtn, assetImage, btnHistory;
    TextView assetCategory, assetRegDate, assetName, assetDetail, assetLocation, assetAssignedTo, assetStatus;
    //    TextView assignedAt, assignedBy, assignedById;
    LinearLayout btnAssign, btnDamageReport;

    private DatabaseReference databaseReference;
    private String assetId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_detail);


        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // Initialize views
        backBtn = findViewById(R.id.product_detail_back);
        assetImage = findViewById(R.id.asset_image);
        assetCategory = findViewById(R.id.asset_category);
        assetRegDate = findViewById(R.id.asset_reg_date);
        assetName = findViewById(R.id.asset_name);
        assetDetail = findViewById(R.id.asset_detail);
        assetLocation = findViewById(R.id.asset_location);
        assetAssignedTo = findViewById(R.id.asset_assigned_to);
        assetStatus = findViewById(R.id.asset_status);
        btnAssign = findViewById(R.id.btn_assign);
        btnDamageReport = findViewById(R.id.btn_damage_report);
        btnHistory = findViewById(R.id.btn_history);

//        assignedAt = findViewById(R.id.assigned_at);
//        assignedBy = findViewById(R.id.assigned_by);
//        assignedById = findViewById(R.id.assigned_by_id);


        backBtn.setOnClickListener(v -> finish());

        // Get assetId from Intent extras
        assetId = getIntent().getStringExtra("assetId");
        if (assetId == null || assetId.isEmpty()) {
            Toast.makeText(this, "Invalid asset ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Reference to the asset node in Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Assets").child(assetId);

        btnAssign.setOnClickListener(v -> {
            Intent intent = new Intent(AssetDetail.this, AssignAssets.class);
            intent.putExtra("assetId", assetId);
            startActivity(intent);
        });

        btnDamageReport.setOnClickListener(v -> {
            Toast.makeText(this, "Damage Report clicked", Toast.LENGTH_SHORT).show();
            // TODO: Implement damage report logic
        });

        btnHistory.setOnClickListener(v -> {

            Intent intent = new Intent(AssetDetail.this, AssetLog.class);
            intent.putExtra("assetId", assetId);
            startActivity(intent);

        });

        // Load data from Firebase
        loadAssetData();
    }

    private void loadAssetData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String category = snapshot.child("category").getValue(String.class);
                    String detail = snapshot.child("detail").getValue(String.class);
                    Long regDateTimestamp = snapshot.child("regDate").getValue(Long.class);
                    String location = snapshot.child("location").getValue(String.class);
                    String assignedTo = snapshot.child("assigneeName").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    String assigned_by = snapshot.child("assignedBy").getValue(String.class);
                    String assigned_by_id = snapshot.child("assignedById").getValue(String.class);
                    Long assigned_at = snapshot.child("assignedAt").getValue(Long.class);

                    assetName.setText(name != null ? name : "N/A");
                    assetCategory.setText("Category: " + (category != null ? category : "N/A"));
                    assetDetail.setText(detail != null ? detail : "N/A");
                    assetRegDate.setText(formatTimestamp(regDateTimestamp));
                    assetLocation.setText("Location: " + (location != null ? location : "N/A"));
                    assetAssignedTo.setText("Assigned To: " + (assignedTo != null ? assignedTo : "N/A"));
                    assetStatus.setText(status != null ? status : "N/A");

//                    assignedAt.setText("Assigned At: " + formatTimestamp(assigned_at));
//                    assignedBy.setText("Assigned By: " + (assigned_by != null ? assigned_by : "N/A"));
//                    assignedById.setText("Assigned By: " + (assigned_by_id != null ? assigned_by_id : "N/A"));


                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(AssetDetail.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_loading)
                                .into(assetImage);
                    } else {
                        assetImage.setImageResource(R.drawable.ic_loading);
                    }
                } else {
                    Toast.makeText(AssetDetail.this, "Asset not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssetDetail.this, "Failed to load asset data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String formatTimestamp(Long timestamp) {
        if (timestamp == null) return "N/A";

        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.ENGLISH);
        return sdf.format(date);
    }
}
