package com.bright.client.Purchasing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bright.client.Adapter.PaymentAdapter;
import com.bright.client.Model.Payment;
import com.bright.client.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PurchasePayment extends AppCompatActivity {

    LinearLayout fabAddPayment;

    RecyclerView recyclerView;
    ArrayList<Payment> paymentList;
    PaymentAdapter adapter;

    private String temporaryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_payment);

        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);


        temporaryId = getIntent().getStringExtra("temporaryId");

        recyclerView = findViewById(R.id.recycler_payment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        paymentList = new ArrayList<>();
        adapter = new PaymentAdapter(this, paymentList);
        recyclerView.setAdapter(adapter);

        fabAddPayment = findViewById(R.id.fab_add_payment);


        fabAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PurchasePayment.this, "FAB clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("PurchaseDraft")
                .child(temporaryId)
                .child("payment");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                paymentList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Payment payment = ds.getValue(Payment.class);
                    paymentList.add(payment);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PurchasePayment.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });

    }
}