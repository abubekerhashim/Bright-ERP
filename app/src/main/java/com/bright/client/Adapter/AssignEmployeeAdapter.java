package com.bright.client.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Employee;
import com.bright.client.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AssignEmployeeAdapter
        extends RecyclerView.Adapter<AssignEmployeeAdapter.AssignEmployeeViewHolder> {

    private List<Employee> list;
    private String assignToPhone;
    private DatabaseReference employeesRef;

    public AssignEmployeeAdapter(List<Employee> list, String assignToPhone) {
        this.list = list;
        this.assignToPhone = assignToPhone;
        this.employeesRef = FirebaseDatabase.getInstance().getReference("Employees");
    }

    @NonNull
    @Override
    public AssignEmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_employee_assign, parent, false);
        return new AssignEmployeeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignEmployeeViewHolder holder, int position) {
        Employee employee = list.get(position);

        holder.txtName.setText(
                employee.getFirstName() + " " + employee.getMiddleName()
        );
        holder.employeePosition.setText(employee.getPosition());

        if (employee.getImageUrl() != null && !employee.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())  // <-- use holder.itemView.getContext()
                    .load(employee.getImageUrl())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_loading) // optional: show if image fails
                    .into(holder.employeePhoto);
        } else {
            // Optional: set default placeholder if no image URL
            holder.employeePhoto.setImageResource(R.drawable.ic_face_man);
        }


        DatabaseReference bossAssignRef = employeesRef
                .child(assignToPhone)
                .child("assignedEmployees")
                .child(employee.getUserId());

        DatabaseReference employeeBossRef = employeesRef
                .child(employee.getUserId())
                .child("assignedBoss");


        employeeBossRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAssignedToThisBoss =
                        snapshot.exists() && assignToPhone.equals(snapshot.getValue(String.class));

                updateButtonUI(holder, isAssignedToThisBoss);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        holder.btnAssign.setOnClickListener(v -> {

            employeeBossRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        String currentBossPhone = snapshot.getValue(String.class);

                        if (assignToPhone.equals(currentBossPhone)) {
                            // Already assigned to this boss → confirm unassign
                            new AlertDialog.Builder(holder.itemView.getContext())
                                    .setTitle("Confirm Unassign")
                                    .setMessage("Do you want to unassign this employee?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        bossAssignRef.removeValue();
                                        employeeBossRef.removeValue();
                                        updateButtonUI(holder, false);
                                        Toast.makeText(holder.itemView.getContext(),
                                                "Employee unassigned", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("No", null)
                                    .show();

                        } else {
                            // Already assigned to another boss → fetch boss name
                            DatabaseReference otherBossRef = employeesRef
                                    .child(currentBossPhone);

                            otherBossRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot bossSnapshot) {
                                    String currentBossName = "Unknown";
                                    if (bossSnapshot.exists()) {
                                        String firstName = bossSnapshot.child("firstName").getValue(String.class);
                                        String middleName = bossSnapshot.child("middleName").getValue(String.class);
                                        currentBossName = (firstName != null ? firstName : "") + " " +
                                                (middleName != null ? middleName : "");
                                    }

                                    new AlertDialog.Builder(holder.itemView.getContext())
                                            .setTitle("Confirm Reassign")
                                            .setMessage("This employee is already assigned to '" + currentBossName + "'.\nDo you want to unassign from '" + currentBossName + "' and assign to this boss?")
                                            .setPositiveButton("Yes", (dialog, which) -> {
                                                // Remove from old boss
                                                employeesRef.child(currentBossPhone)
                                                        .child("assignedEmployees")
                                                        .child(employee.getUserId()).removeValue();

                                                // Assign to new boss
                                                bossAssignRef.setValue(true);
                                                employeeBossRef.setValue(assignToPhone);
                                                updateButtonUI(holder, true);
                                                Toast.makeText(holder.itemView.getContext(),
                                                        "Employee reassigned", Toast.LENGTH_SHORT).show();
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }

                    } else {
                        // Not assigned → confirm assign
                        new AlertDialog.Builder(holder.itemView.getContext())
                                .setTitle("Confirm Assign")
                                .setMessage("Do you want to assign this employee to this boss?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    bossAssignRef.setValue(true);
                                    employeeBossRef.setValue(assignToPhone);
                                    updateButtonUI(holder, true);
                                    Toast.makeText(holder.itemView.getContext(),
                                            "Employee assigned", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });


    }

    // 🔹 UI State Handler (SAFE & CLEAN)
    private void updateButtonUI(@NonNull AssignEmployeeViewHolder holder, boolean assigned) {
        Context context = holder.itemView.getContext();

        holder.btnAssign.setBackgroundResource(R.drawable.rounded_background);

        if (assigned) {
            holder.txtAssign.setText("UnAssign");
            holder.txtAssign.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            holder.btnAssign.setBackgroundResource(R.drawable.rounded_background);
            holder.btnAssign.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.unselected_icon_color)
                    )
            );
        } else {
            holder.txtAssign.setText("Assign");
            holder.txtAssign.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
            holder.btnAssign.setBackgroundResource(R.drawable.rounded_background);
            holder.btnAssign.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sold_green)
                    )
            );
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 🔹 ViewHolder
    static class AssignEmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtAssign, employeePosition;
        LinearLayout btnAssign;
        ImageView employeePhoto;

        AssignEmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.employee_name);
            txtAssign = itemView.findViewById(R.id.text_assign);
            btnAssign = itemView.findViewById(R.id.btn_assign);
            employeePhoto = itemView.findViewById(R.id.employee_photo);
            employeePosition = itemView.findViewById(R.id.employee_position);
        }
    }
}
