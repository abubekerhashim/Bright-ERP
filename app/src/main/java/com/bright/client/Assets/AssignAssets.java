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

import com.bright.client.Adapter.AssignAssetUserListAdapter;
import com.bright.client.Model.Employee;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class AssignAssets extends AppCompatActivity {

    private RecyclerView recyclerAssign;
    private AssignAssetUserListAdapter adapter;
    private List<Employee> employeeList;

    private DatabaseReference employeeRef;

    private Employee currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_assets);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        recyclerAssign = findViewById(R.id.recycler_assign_asset);
        recyclerAssign.setLayoutManager(new LinearLayoutManager(this));

        employeeList = new ArrayList<>();
        adapter = new AssignAssetUserListAdapter(this, employeeList, new AssignAssetUserListAdapter.OnEmployeeClickListener() {
            @Override
            public void onAssignClicked(Employee employee) {
                assignAsset(employee);
            }

        });

        recyclerAssign.setAdapter(adapter);

        employeeRef = FirebaseDatabase.getInstance().getReference("Employees");

        loadEmployees();
    }

    private void loadEmployees() {
        employeeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeList.clear();

                for (DataSnapshot empSnap : snapshot.getChildren()) {
                    Employee employee = empSnap.getValue(Employee.class);
                    if (employee != null) {
                        employeeList.add(employee);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void assignAsset(Employee employee) {

        String assetId = getIntent().getStringExtra("assetId");
        if (assetId == null) {
            Toast.makeText(this, "Invalid asset ID", Toast.LENGTH_SHORT).show();
            return;
        }
        Paper.init(this);
        currentUser = Paper.book().read("currentUser");

        assert currentUser != null;
        String currentUserId = currentUser.getUserId();
        String currentUserName = currentUser.getFirstName() + " " + currentUser.getMiddleName();


        DatabaseReference assetRef =
                FirebaseDatabase.getInstance().getReference("Assets").child(assetId);

        long time = System.currentTimeMillis();

        // Update main asset fields
        Map<String, Object> assignData = new HashMap<>();
        assignData.put("assignedTo", employee.getUserId());
        assignData.put("assigneeName", employee.getFirstName() + " " + employee.getMiddleName());
        assignData.put("assignmentStatus", "pending");
        assignData.put("assignedBy", currentUserName);
        assignData.put("assignedById", currentUserId);
        assignData.put("assignedAt", time);

        assetRef.updateChildren(assignData);



        assetRef.updateChildren(assignData);

        // Add history log
        Map<String, Object> history = new HashMap<>();
        history.put("action", "ASSIGNED");
        history.put("from", currentUserId);
        history.put("to", employee.getUserId());
        history.put("status", "pending");
        history.put("timestamp", time);

        assetRef.child("history").push().setValue(history);

        Toast.makeText(this, "Asset assigned (Pending approval)", Toast.LENGTH_SHORT).show();
        finish();
    }



}