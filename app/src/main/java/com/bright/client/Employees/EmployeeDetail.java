package com.bright.client.Employees;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bright.client.Model.Employee;
import com.bright.client.R;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmployeeDetail extends AppCompatActivity {

    private TextView employeeName, employeeGender, employeeRegDate, employeePhone,
            employeeEmail, employeeEmergencyPhone, employeeCity, employeeSubCity,
            employeeHomeNo, employeeNameMain, employeePosition, employeeId, employeeSuspend;

    private LinearLayout btnCall, btnEdit, btnAssign, btnSuspend;
    private ImageView backButton, employeePhoto, moreButton;

    private ShimmerFrameLayout shimmerLayout;
    private ScrollView contentLayout;

    private DatabaseReference employeeRef;
    private boolean isSuspended = false;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);


        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        initViews();
        buttonFunc();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "No User ID passed!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        employeeRef = FirebaseDatabase.getInstance()
                .getReference("Employees")
                .child(userId);

        startShimmer();
        loadEmployeeData();
    }

    private void initViews() {

        userId = getIntent().getStringExtra("userId");

        employeeName = findViewById(R.id.employee_name);
        employeeGender = findViewById(R.id.employee_gender);
        employeeRegDate = findViewById(R.id.employee_reg_date);
        employeePhone = findViewById(R.id.employee_phone);
        employeeEmail = findViewById(R.id.employee_email);
        employeeEmergencyPhone = findViewById(R.id.employee_emergency_phone);
        employeeCity = findViewById(R.id.employee_city);
        employeeSubCity = findViewById(R.id.employee_sub_city);
        employeeHomeNo = findViewById(R.id.employee_home_no);
        employeeNameMain = findViewById(R.id.employee_name_main);
        employeePosition = findViewById(R.id.employee_position);
        employeeId = findViewById(R.id.employee_id);
        employeeSuspend = findViewById(R.id.employee_suspend);

        btnCall = findViewById(R.id.btn_call);
        btnEdit = findViewById(R.id.btn_edit);
        btnAssign = findViewById(R.id.btn_assign);
        btnSuspend = findViewById(R.id.btn_suspend);

        backButton = findViewById(R.id.back_button);
        employeePhoto = findViewById(R.id.employee_photo);
        moreButton = findViewById(R.id.more_menu);

        shimmerLayout = findViewById(R.id.shimmer_layout);
        contentLayout = findViewById(R.id.content_layout);
    }

    private void buttonFunc() {

        backButton.setOnClickListener(v -> onBackPressed());

        btnCall.setOnClickListener(v -> callEmployee(userId));

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterEmployee.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnAssign.setOnClickListener(v -> {
            Intent intent = new Intent(this, AssignEmployee.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnSuspend.setOnClickListener(v -> toggleSuspend());
    }

    private void startShimmer() {
        shimmerLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        shimmerLayout.startShimmer();
    }

    private void stopShimmer() {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

    private void loadEmployeeData() {

        employeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    stopShimmer();
                    Toast.makeText(EmployeeDetail.this, "Employee not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                Employee employee = snapshot.getValue(Employee.class);
                if (employee == null) {
                    stopShimmer();
                    return;
                }

                String fullName = employee.getFirstName() + " "
                        + employee.getMiddleName() + " "
                        + employee.getLastName();

                String shortName = employee.getFirstName() + " " + employee.getMiddleName();

                employeeName.setText(fullName);
                employeeNameMain.setText(shortName);
                employeeGender.setText(employee.getGender());
                employeePosition.setText(employee.getPosition());
                employeeCity.setText(employee.getCity());
                employeeSubCity.setText(employee.getSubCity());
                employeeHomeNo.setText(employee.getHouseNo());
                employeeId.setText(employee.getEmployeeId());
                employeePhone.setText(toInternationalFormat(userId));

                String duration = getDurationFromTimestamp(employee.getRegDate());
                employeeRegDate.setText(formatDate(employee.getRegDate()) + " (" + duration + " ago)");

                if (employee.getImageUrl() != null && !employee.getImageUrl().isEmpty()) {
                    Glide.with(EmployeeDetail.this)
                            .load(employee.getImageUrl())
                            .placeholder(R.drawable.ic_loading)
                            .into(employeePhoto);
                }

                isSuspended = employee.isSuspend();
                updateSuspendUI();

                stopShimmer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                stopShimmer();
                Toast.makeText(EmployeeDetail.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSuspendUI() {
        employeeSuspend.setText(isSuspended ? "Activate" : "Suspend");
        btnSuspend.setBackgroundTintList(
                ContextCompat.getColorStateList(
                        this,
                        isSuspended ? R.color.sold_green : R.color.sold_red
                )
        );
    }

    private void toggleSuspend() {

        boolean newStatus = !isSuspended;

        employeeRef.child("suspend").setValue(newStatus)
                .addOnSuccessListener(unused -> {
                    isSuspended = newStatus;
                    updateSuspendUI();
                    Toast.makeText(
                            this,
                            isSuspended ? "Employee Suspended" : "Employee Activated",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
                );
    }

    private void callEmployee(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(android.net.Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private String formatDate(long timestamp) {
        return new SimpleDateFormat(
                "E, MMM dd yyyy   HH:mm",
                Locale.getDefault()
        ).format(new Date(timestamp));
    }

    public static String toInternationalFormat(String localNumber) {

        if (localNumber == null || localNumber.length() != 10) return "";

        if (localNumber.startsWith("0")) {
            localNumber = localNumber.substring(1);
        }

        String intl = "251" + localNumber;

        return "(" + intl.substring(0, 3) + ")"
                + intl.substring(3, 6) + "-"
                + intl.substring(6);
    }

    public static String getDurationFromTimestamp(long regTimestamp) {

        long days = (System.currentTimeMillis() - regTimestamp) / (1000 * 60 * 60 * 24);
        long years = days / 365;
        long months = (days % 365) / 30;

        if (years > 0) return years + " yrs " + months + " months";
        if (months > 0) return months + " months";
        return days + " days";
    }
}
