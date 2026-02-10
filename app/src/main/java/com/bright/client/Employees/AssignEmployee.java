package com.bright.client.Employees;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.bright.client.Adapter.AssignEmployeeAdapter;
import com.bright.client.Model.Employee;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AssignEmployee extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssignEmployeeAdapter adapter;
    private final List<Employee> employeeList = new ArrayList<>();

    private DatabaseReference employeesRef;
    private String assignedToPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_employee);


        setupStatusBar();
        getIntentData();
        setupRecyclerView();

        employeesRef = FirebaseDatabase.getInstance().getReference("Employees");
        loadEmployees();
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }

    private void getIntentData() {
        Intent intent = getIntent();
        assignedToPhone = intent.getStringExtra("userId");

        if (assignedToPhone == null || assignedToPhone.trim().isEmpty()) {
            Toast.makeText(this, "No User ID passed!", Toast.LENGTH_SHORT).show();
            finish(); // stop activity if invalid
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.employee_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new AssignEmployeeAdapter(employeeList, assignedToPhone);
        recyclerView.setAdapter(adapter);
    }

    private void loadEmployees() {
        employeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String phone = dataSnapshot.getKey();
                    if (phone == null || phone.equals(assignedToPhone)) continue;

                    Employee employee = dataSnapshot.getValue(Employee.class);
                    if (employee == null) continue;

                    // set Firebase key as userId
                    employee.setUserId(phone);
                    employeeList.add(employee);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        AssignEmployee.this,
                        "Failed to load employees: " + error.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
