package com.bright.client.Purchasing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bright.client.Model.Customer;
import com.bright.client.Model.Suppliers;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class PurchaseOrder extends AppCompatActivity {

    private AutoCompleteTextView customerDropdown;
    private ArrayList<Suppliers> customerList = new ArrayList<>();

    private DatabaseReference customerRef;

    private String selectedSupplierId = "";
    private String selectedSupplierName = "";

    private LinearLayout chooseOrderDate, chooseReceiveDate;
    private TextView textOrderDate, textReceiveDate;

    private LinearLayout btnContinue;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order);

        // Status bar style
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        initViews();
        buttonFunc();
        chooseCustomer();
        chooseDate();

    }

    // init views
    private void initViews() {

        // Views
        customerDropdown = findViewById(R.id.customer_dropdown);
        chooseOrderDate = findViewById(R.id.choose_order_date);
        chooseReceiveDate = findViewById(R.id.choose_receive_date);

        textOrderDate = findViewById(R.id.text_order_date);
        textReceiveDate = findViewById(R.id.text_receive_date);

        btnContinue = findViewById(R.id.btn_continue);
        backButton = findViewById(R.id.back_button);

        // Firebase
        customerRef = FirebaseDatabase.getInstance().getReference("Suppliers");
    }

    private void buttonFunc(){
        backButton.setOnClickListener(v -> onBackPressed());
        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(PurchaseOrder.this, PurchaseOrderChoose.class);
            intent.putExtra("supplierId", selectedSupplierId);
            intent.putExtra("supplierName", selectedSupplierName);

            intent.putExtra("orderDate", textOrderDate.getText().toString());
            intent.putExtra("receiveDate", textReceiveDate.getText().toString());

            startActivity(intent);


        });
    }
    // ================= CATEGORY =================

    private void chooseCustomer() {

        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                customerList.clear();
                ArrayList<String> names = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Suppliers suppliers = ds.getValue(Suppliers.class);

                    if (suppliers != null && suppliers.isStatus()) {
                        suppliers.setUserId(ds.getKey());
                        customerList.add(suppliers);
                        names.add(suppliers.getName());
                    }
                }

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(PurchaseOrder.this,
                                android.R.layout.simple_dropdown_item_1line,
                                names);

                customerDropdown.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PurchaseOrder.this,
                        "Failed to load categories",
                        Toast.LENGTH_SHORT).show();
            }
        });

        customerDropdown.setOnItemClickListener((parent, view, position, id) -> {
            Suppliers selected = customerList.get(position);
            selectedSupplierId = selected.getUserId();
            selectedSupplierName = selected.getName();
        });
    }

    private void chooseDate(){
        chooseOrderDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    PurchaseOrder.this,
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {

                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        textOrderDate.setText(date);

                    }, year, month, day);

            datePickerDialog.show();
        });

        chooseReceiveDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    PurchaseOrder.this,
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {

                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        textReceiveDate.setText(date);

                    }, year, month, day);

            datePickerDialog.show();
        });
    }
}