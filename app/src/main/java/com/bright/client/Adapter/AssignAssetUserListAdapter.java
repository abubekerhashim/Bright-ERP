package com.bright.client.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Employee;
import com.bright.client.R;

import java.util.List;

public class AssignAssetUserListAdapter extends RecyclerView.Adapter<AssignAssetUserListAdapter.EmployeeViewHolder> {

    public interface OnEmployeeClickListener {
        void onAssignClicked(Employee employee);
    }

    private Context context;
    private List<Employee> employeeList;
    private OnEmployeeClickListener listener;

    public AssignAssetUserListAdapter(Context context, List<Employee> employeeList, OnEmployeeClickListener listener) {
        this.context = context;
        this.employeeList = employeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_assign_asset, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);

        holder.userName.setText(employee.getFirstName() + " " + employee.getMiddleName());
        holder.userPosition.setText(employee.getPosition());
        holder.userId.setText(employee.getUserId());



        holder.btnAssign.setOnClickListener(v -> listener.onAssignClicked(employee));
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userPosition, userId;
        LinearLayout btnAssign;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userPosition = itemView.findViewById(R.id.user_position);
            userId = itemView.findViewById(R.id.user_id);

            btnAssign = itemView.findViewById(R.id.btn_assign);

        }
    }
}

