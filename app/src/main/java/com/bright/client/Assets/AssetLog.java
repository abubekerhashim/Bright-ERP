package com.bright.client.Assets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.bright.client.Adapter.AssetHistoryAdapter;
import com.bright.client.Model.AssetHistory;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AssetLog extends AppCompatActivity {

    RecyclerView recyclerView;
    AssetHistoryAdapter adapter;
    List<AssetHistory> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_log);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        String assetId = getIntent().getStringExtra("assetId");
        if (assetId == null) {
            Toast.makeText(this, "Invalid asset ID", Toast.LENGTH_SHORT).show();
        }
        if (assetId != null) {
            loadHistory(assetId);
        }

        recyclerView = findViewById(R.id.recycler_asset_log);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new AssetHistoryAdapter(this, historyList);
        recyclerView.setAdapter(adapter);



    }

    private void loadHistory(String assetId) {

        DatabaseReference historyRef = FirebaseDatabase.getInstance()
                .getReference("Assets")
                .child(assetId)
                .child("history");

        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    AssetHistory history = ds.getValue(AssetHistory.class);
                    if (history != null) {
                        historyList.add(history);
                    }
                }

                // 🔽 SORT: Newest first
                historyList.sort((h1, h2) ->
                        Long.compare(h2.getTimestamp(), h1.getTimestamp()));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssetLog.this, "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });
    }


}