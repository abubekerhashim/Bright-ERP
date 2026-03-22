package com.bright.client.Purchasing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.*;

import com.bright.client.Adapter.PaymentAdapter;
import com.bright.client.Model.Payment;
import com.bright.client.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PurchasePayment extends AppCompatActivity {

    private LinearLayout fabAddPayment, btnContinue;
    private RecyclerView recyclerView;

    private TextView textPaid, textCredit, textTotal;

    private ArrayList<Payment> paymentList;
    private PaymentAdapter adapter;

    private String temporaryId;

    private double totalAmount = 0;
    private double totalPaid = 0;

    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_payment);

        setupStatusBar();
        initViews();
        setupRecycler();

        temporaryId = getIntent().getStringExtra("temporaryId");

        fabAddPayment.setOnClickListener(v -> showAddPaymentDialog());
        btnContinue.setOnClickListener(v -> showConfirmDialog());

        loadTotal();
        listenPayments();
    }

    // -------------------- UI SETUP --------------------

    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        textPaid = findViewById(R.id.text_paid);
        textCredit = findViewById(R.id.text_credit);
        textTotal = findViewById(R.id.text_total);

        fabAddPayment = findViewById(R.id.fab_add_payment);
        btnContinue = findViewById(R.id.btn_continue);

        recyclerView = findViewById(R.id.recycler_payment_list);
    }

    private void setupRecycler() {
        paymentList = new ArrayList<>();
        adapter = new PaymentAdapter(this, paymentList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // -------------------- LOAD DATA --------------------

    private void loadTotal() {
        rootRef.child("PurchaseDraft").child(temporaryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Double total = snapshot.child("totalPrice").getValue(Double.class);

                        if (total != null) {
                            totalAmount = total;
                            updateSummary();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void listenPayments() {
        rootRef.child("PurchaseDraft").child(temporaryId).child("payment")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        paymentList.clear();
                        totalPaid = 0;

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Payment p = ds.getValue(Payment.class);

                            if (p != null) {
                                paymentList.add(p);
                                totalPaid += p.getAmount();
                            }
                        }

                        adapter.notifyDataSetChanged();
                        updateSummary();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void updateSummary() {
        double remaining = totalAmount - totalPaid;

        textTotal.setText(format(totalAmount));
        textPaid.setText(format(totalPaid));
        textCredit.setText(format(remaining));

        // Disable continue if not fully paid
        btnContinue.setEnabled(totalPaid >= totalAmount);
    }

    // -------------------- ADD PAYMENT --------------------

    private void showAddPaymentDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_add_payment, null);

        Spinner spinner = view.findViewById(R.id.spinner_account);
        EditText etAmount = view.findViewById(R.id.et_amount);
        EditText etDate = view.findViewById(R.id.et_date);
        EditText etRemark = view.findViewById(R.id.et_remark);
        LinearLayout btnSave = view.findViewById(R.id.btn_save_payment);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.show();

        // Bottom style
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Auto fill remaining
        double remaining = totalAmount - totalPaid;
        etAmount.setText(format(remaining));

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> numbers = new ArrayList<>();

        // Load accounts
        rootRef.child("BankAccounts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String id = ds.child("accountId").getValue(String.class);
                            String name = ds.child("accountName").getValue(String.class);
                            String number = ds.child("accountNumber").getValue(String.class);

                            Double balance = ds.child("balance").getValue(Double.class);

                            if (id != null && name != null) {
                                ids.add(id);
                                numbers.add(number);
                                names.add(name + " (" + number + ") - " + format(balance));
                            }
                        }

                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(PurchasePayment.this,
                                        R.layout.item_spinner_small, names);

                        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown_small);
                        spinner.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Date picker
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view1, y, m, d) ->
                    etDate.setText(d + "/" + (m + 1) + "/" + y),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Save
        btnSave.setOnClickListener(v -> handleSavePayment(
                spinner, etAmount, etDate, etRemark,
                ids, names, numbers, dialog
        ));
    }

    private void handleSavePayment(Spinner spinner,
                                   EditText etAmount,
                                   EditText etDate,
                                   EditText etRemark,
                                   ArrayList<String> ids,
                                   ArrayList<String> names,
                                   ArrayList<String> numbers,
                                   AlertDialog dialog) {

        String amountStr = etAmount.getText().toString().replace(",", "");
        String date = etDate.getText().toString();
        String remark = etRemark.getText().toString();

        int pos = spinner.getSelectedItemPosition();

        if (amountStr.isEmpty() || date.isEmpty() || pos < 0) {
            toast("Fill all fields");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        double remaining = totalAmount - totalPaid;

        if (amount > remaining) {
            toast("Exceeds remaining!");
            return;
        }

        String accountId = ids.get(pos);

        // Check balance
        rootRef.child("BankAccounts").child(accountId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                        Double balance = snap.child("balance").getValue(Double.class);

                        if (balance == null || amount > balance) {
                            toast("Insufficient balance!");
                            return;
                        }

                        savePayment(amount, pos, ids, names, numbers, date, remark, dialog);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void savePayment(double amount, int pos,
                             ArrayList<String> ids,
                             ArrayList<String> names,
                             ArrayList<String> numbers,
                             String date, String remark,
                             AlertDialog dialog) {

        DatabaseReference ref = rootRef.child("PurchaseDraft")
                .child(temporaryId)
                .child("payment");

        String id = ref.push().getKey();

        Payment p = new Payment();
        p.setPaymentId(id);
        p.setAccountId(ids.get(pos));
        p.setAccountName(names.get(pos));
        p.setAccountNumber(numbers.get(pos));
        p.setAmount(amount);
        p.setDate(date);
        p.setRemark(remark);

        ref.child(id).setValue(p).addOnSuccessListener(unused -> {
            Snackbar.make(findViewById(android.R.id.content),
                    "Payment Added", Snackbar.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    // -------------------- CONFIRM + COMPLETE --------------------

    private void showConfirmDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_purchase, null);

        ((TextView) view.findViewById(R.id.txt_total))
                .setText("Total: " + format(totalAmount));

        ((TextView) view.findViewById(R.id.txt_paid))
                .setText("Paid: " + format(totalPaid));

        ((TextView) view.findViewById(R.id.txt_remaining))
                .setText("Remaining: " + format(totalAmount - totalPaid));

        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.show();

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            dialog.dismiss();
            completePurchase();
        });
    }

    private void completePurchase() {

        DatabaseReference draftRef = rootRef.child("PurchaseDraft").child(temporaryId);
        DatabaseReference orderRef = rootRef.child("PurchaseOrder");
        DatabaseReference paymentLogRef = rootRef.child("PaymentLogs");
        DatabaseReference productLogRef = rootRef.child("ProductLogs");
        DatabaseReference seqRef = rootRef.child("Sequences").child("purchaseSeq");

        // ✅ STEP 1: SEQUENCE
        seqRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                Integer current = currentData.getValue(Integer.class);
                if (current == null) current = 0;

                current++;
                currentData.setValue(current);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot seqSnap) {

                if (!committed || seqSnap == null) {
                    toast("Failed to generate Order ID");
                    return;
                }

                int seq = seqSnap.getValue(Integer.class);
                String orderId = String.format("HALI-PO-%06d", seq);

                // ✅ STEP 2: GET DRAFT
                draftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot draftSnap) {

                        if (!draftSnap.exists()) {
                            toast("Draft not found");
                            return;
                        }

                        // ✅ STEP 3: SAVE ORDER
                        orderRef.child(orderId).setValue(draftSnap.getValue())
                                .addOnSuccessListener(unused -> {

                                    orderRef.child(orderId).child("orderId").setValue(orderId);

                                    // =========================
                                    // ✅ STEP 4: PAYMENT LOG + BALANCE
                                    // =========================
                                    if (draftSnap.hasChild("payment")) {

                                        for (DataSnapshot paySnap : draftSnap.child("payment").getChildren()) {

                                            Payment payment = paySnap.getValue(Payment.class);
                                            if (payment == null) continue;

                                            // 🔹 Save Payment Log
                                            String logId = paymentLogRef.push().getKey();

                                            paymentLogRef.child(logId).setValue(payment);
                                            paymentLogRef.child(logId).child("orderId").setValue(orderId);
                                            paymentLogRef.child(logId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                            paymentLogRef.child(logId).child("type").setValue("expense");
                                            paymentLogRef.child(logId).child("reason").setValue("Purchase Product");


                                            // 🔹 Deduct balance safely
                                            DatabaseReference accRef = rootRef.child("BankAccounts")
                                                    .child(payment.getAccountId());

                                            accRef.runTransaction(new Transaction.Handler() {
                                                @NonNull
                                                @Override
                                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                                                    Double balance = currentData.child("balance").getValue(Double.class);
                                                    if (balance == null) return Transaction.success(currentData);

                                                    double newBalance = balance - payment.getAmount();

                                                    if (newBalance < 0) {
                                                        return Transaction.abort(); // ❌ safety
                                                    }

                                                    currentData.child("balance").setValue(newBalance);

                                                    return Transaction.success(currentData);
                                                }

                                                @Override
                                                public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {}
                                            });
                                        }
                                    }

                                    // =========================
                                    // ✅ STEP 5: UPDATE STOCK + PRODUCT LOG (FIXED)
                                    // =========================
                                    if (draftSnap.hasChild("items")) {

                                        for (DataSnapshot itemSnap : draftSnap.child("items").getChildren()) {


                                            String prodId = itemSnap.child("prodId").getValue(String.class);
                                            Double qtyNumber = itemSnap.child("quantity").getValue(Double.class);

                                            if (prodId == null || qtyNumber == null) continue;

                                            long qtyLong = qtyNumber.longValue();

                                            // 🔍 Debug (optional)
                                            // Log.d("STOCK", "prodId=" + prodId + " qty=" + qtyLong);

                                            DatabaseReference productRef = rootRef.child("Products").child(prodId);

                                            // 🔹 Update stock safely
                                            DatabaseReference productLogChildRef = productLogRef.push(); // unique log

                                            productRef.runTransaction(new Transaction.Handler() {

                                                long beforeQty = 0;
                                                long afterQty = 0;

                                                @NonNull
                                                @Override
                                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                                                    Long currentQty = currentData.child("totalQty").getValue(Long.class);
                                                    if (currentQty == null) currentQty = 0L;

                                                    beforeQty = currentQty;
                                                    afterQty = currentQty + qtyLong;

                                                    currentData.child("totalQty").setValue(afterQty);

                                                    return Transaction.success(currentData);
                                                }

                                                @Override
                                                public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {

                                                    if (!committed) {
                                                        Log.e("STOCK", "Update failed");
                                                        return;
                                                    }

                                                    // ✅ SAVE PRODUCT LOG WITH BEFORE/AFTER
                                                    productLogChildRef.child("prodId").setValue(prodId);
                                                    productLogChildRef.child("qty").setValue(qtyLong);
                                                    productLogChildRef.child("beforeQty").setValue(beforeQty);
                                                    productLogChildRef.child("afterQty").setValue(afterQty);
                                                    productLogChildRef.child("type").setValue("IN");
                                                    productLogChildRef.child("source").setValue("Purchase");
                                                    productLogChildRef.child("orderId").setValue(orderId);
                                                    productLogChildRef.child("timestamp").setValue(ServerValue.TIMESTAMP);

                                                    Log.d("STOCK", "Updated + Logged");
                                                }
                                            });
                                        }
                                    }

                                    // =========================
                                    // ✅ STEP 6: DELETE DRAFT
                                    // =========================
                                    draftRef.removeValue();

                                    Snackbar.make(findViewById(android.R.id.content),
                                            "Purchase Completed\nID: " + orderId,
                                            Snackbar.LENGTH_LONG).show();

                                    finish();
                                })
                                .addOnFailureListener(e -> toast("Failed to save order"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        toast("Error loading draft");
                    }
                });
            }
        });
    }

    // -------------------- UTILS --------------------

    private String format(Double v) {
        if (v == null) return "0.00";
        return new DecimalFormat("#,###.00").format(v);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}