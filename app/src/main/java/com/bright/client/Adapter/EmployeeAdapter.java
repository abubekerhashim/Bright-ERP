package com.bright.client.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Employee;
import com.bright.client.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;
    private OnEmployeeClickListener listener;

    public interface OnEmployeeClickListener {
        void onUserClick(Employee employee);
        void onEditClick(Employee employee);
        void onCallClick(Employee employee);
        void onDeleteClick(Employee employee);
    }

    public EmployeeAdapter(List<Employee> employeeList, OnEmployeeClickListener listener) {
        this.employeeList = employeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        holder.bind(employeeList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return employeeList == null ? 0 : employeeList.size();
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView empName, empPosition, empId, empRate, empRateCount, regDate;
        ImageView empPhoto, empMenu;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);

            empName = itemView.findViewById(R.id.employee_name);
            empPhoto = itemView.findViewById(R.id.employee_photo);
            empPosition = itemView.findViewById(R.id.employee_position);
            empId = itemView.findViewById(R.id.employee_id);
            empMenu = itemView.findViewById(R.id.employee_menu);
            empRate = itemView.findViewById(R.id.employee_rate);
            empRateCount = itemView.findViewById(R.id.employee_rate_count);
            regDate = itemView.findViewById(R.id.reg_date);
        }

        void bind(Employee employee, OnEmployeeClickListener listener) {

            empName.setText(
                    employee.getFirstName() + " " +
                            employee.getMiddleName() + " " +
                            employee.getLastName()
            );

            empId.setText(employee.getEmployeeId());
            empPosition.setText(employee.getPosition());

            empRate.setText("⭐ " + employee.getRateAverage());
            empRateCount.setText("(" + employee.getRateCount() + ")   |");

            String duration = getDurationFromTimestamp(employee.getRegDate());
            regDate.setText(duration);


            // Load image correctly
            Glide.with(itemView.getContext())
                    .load(employee.getImageUrl())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
                    .into(empPhoto);

            // Item click
            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onUserClick(employee);
                }
            });

            // Popup menu
            empMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), empMenu);
                popupMenu.inflate(R.menu.employee_more_menu);

                popupMenu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();

                    if (id == R.id.action_edit) {
                        listener.onEditClick(employee);
                        return true;

                    } else if (id == R.id.action_call) {
                        listener.onCallClick(employee);
                        return true;

                    } else if (id == R.id.action_delete) {
                        listener.onDeleteClick(employee);
                        return true;
                    }

                    return false;
                });

                popupMenu.show();
            });
        }
    }

    public static String getDurationFromTimestamp(long regTimestamp) {

        long now = System.currentTimeMillis();
        long diffMillis = now - regTimestamp;

        long days = diffMillis / (1000 * 60 * 60 * 24);
        long years = days / 365;
        long months = (days % 365) / 30;
        long remainingDays = (days % 365) % 30;

        if (years > 0) {
            return years + " yrs " + months + " months";
        } else if (months > 0) {
            return months + " months " + remainingDays + " days";
        } else {
            return remainingDays + " days";
        }
    }

}
