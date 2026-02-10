package com.bright.client.Sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.bright.client.Adapter.CustomerAdapter;
import com.bright.client.Model.Customer;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerList extends AppCompatActivity {

    RecyclerView recyclerCustomers;
    CustomerAdapter adapter;
    List<Customer> customerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        recyclerCustomers = findViewById(R.id.recycler_customer);
        recyclerCustomers.setLayoutManager(new LinearLayoutManager(this));

        customerList = new ArrayList<>();
        adapter = new CustomerAdapter(this, customerList);
        recyclerCustomers.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Customers");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Customer customer = ds.getValue(Customer.class);
                    if (customer != null) {
                        customerList.add(customer);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}