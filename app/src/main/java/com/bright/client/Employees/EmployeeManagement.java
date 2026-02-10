package com.bright.client.Employees;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bright.client.Adapter.EmployeeAdapter;
import com.bright.client.Adapter.EmployeeShimmerAdapter;
import com.bright.client.Model.Employee;
import com.bright.client.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManagement extends AppCompatActivity
        implements EmployeeAdapter.OnEmployeeClickListener {

    private RecyclerView recyclerView;
    private RecyclerView shimmerRecycler;

    private FloatingActionButton fabAddEmployee;
    private EmployeeAdapter adapter;
    private final List<Employee> employeeList = new ArrayList<>();

    private ImageView btnRefresh, backButton;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);


        setupStatusBar();
        initViews();
        initFirebase();
        loadUsers();

        backButton.setOnClickListener(v -> onBackPressed());
        btnRefresh.setOnClickListener(v -> {
            refreshEmployees();
            ObjectAnimator rotate1 = ObjectAnimator.ofFloat(btnRefresh, "rotation", 0f, 360f);
            rotate1.setDuration(500); // half a second
            rotate1.start();
        });

    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.WHITE);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_employees);
        fabAddEmployee = findViewById(R.id.fab_add_employee);
        shimmerRecycler = findViewById(R.id.recycler_shimmer);

        shimmerRecycler.setLayoutManager(new LinearLayoutManager(this));
        shimmerRecycler.setAdapter(new EmployeeShimmerAdapter());

        recyclerView.setVisibility(View.GONE);
        shimmerRecycler.setVisibility(View.VISIBLE);

        backButton = findViewById(R.id.back_button);
        btnRefresh = findViewById(R.id.btn_refresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmployeeAdapter(employeeList, this);
        recyclerView.setAdapter(adapter);

        fabAddEmployee.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterEmployee.class);
            startActivity(intent);
        });
    }

    private void initFirebase() {
        usersRef = FirebaseDatabase.getInstance().getReference("Employees");
    }

    private void loadUsers() {

        shimmerRecycler.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                employeeList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Employee employee = ds.getValue(Employee.class);
                    if (employee != null) {
                        employeeList.add(employee);
                    }
                }

                shimmerRecycler.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                shimmerRecycler.setVisibility(View.GONE);
                Toast.makeText(EmployeeManagement.this,
                        "Failed to load employees",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    // ================= Adapter Callbacks =================

    @Override
    public void onUserClick(Employee employee) {
        Intent intent = new Intent(this, EmployeeDetail.class);
        intent.putExtra("userId", employee.getUserId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Employee employee) {
        Intent intent = new Intent(EmployeeManagement.this, RegisterEmployee.class);
        intent.putExtra("userId", employee.getUserId());
        startActivity(intent);
    }

    @Override
    public void onCallClick(Employee employee) {
        if (employee.getUserId() == null || employee.getUserId().isEmpty()) {
            Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + employee.getUserId()));
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Employee employee) {

    }

    private void refreshEmployees() {
        btnRefresh.setEnabled(false);

        shimmerRecycler.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        employeeList.clear();
        adapter.notifyDataSetChanged();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Employee employee = ds.getValue(Employee.class);
                    if (employee != null) {
                        employeeList.add(employee);
                    }
                }

                shimmerRecycler.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                btnRefresh.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                shimmerRecycler.setVisibility(View.GONE);
                btnRefresh.setEnabled(true);
                Toast.makeText(EmployeeManagement.this,
                        "Failed to refresh",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



}
