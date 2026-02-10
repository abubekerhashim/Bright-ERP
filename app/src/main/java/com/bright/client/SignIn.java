package com.bright.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bright.client.Common.Common;
import com.bright.client.Model.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.security.MessageDigest;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    private AppCompatButton btnSign;
    private EditText edtPhone, edtPassword;
    private RelativeLayout loadingLayout;
    private LinearLayout btnFingerprint;
    private ImageView togglePassword;

    private DatabaseReference usersRef;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        setupStatusBar();
        initViews();
        initFirebase();
        initActions();
    }
    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        Paper.init(this);

        btnSign = findViewById(R.id.btn_sign);
        edtPhone = findViewById(R.id.edt_phone);
        edtPassword = findViewById(R.id.edt_password);
        loadingLayout = findViewById(R.id.loading_layout);
        btnFingerprint = findViewById(R.id.btn_fingerprint);
        togglePassword = findViewById(R.id.toggle_password);
    }

    private void initFirebase() {
        usersRef = FirebaseDatabase.getInstance().getReference("Employees");
    }

    private void initActions() {

        btnSign.setOnClickListener(v -> handleLogin());

        togglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnFingerprint.setOnClickListener(v -> handleFingerprintLogin());
    }

    private void handleLogin() {
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!validateInput(phone, password)) return;

        showLoading(true);

        usersRef.child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            showLoading(false);
                            toast("You haven't registered");
                            return;
                        }

                        String storedHash = snapshot.child("password").getValue(String.class);
                        String inputHash = hashPassword(password);

                        if (storedHash != null && storedHash.equals(inputHash)) {
                            loginSuccess(snapshot, phone);
                        } else {
                            showLoading(false);
                            toast("Incorrect password");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showLoading(false);
                        toast(error.getMessage());
                    }
                });
    }

    private boolean validateInput(String phone, String password) {

        if (TextUtils.isEmpty(phone)) {
            toast("Please enter phone");
            return false;
        }

        if (!phone.matches("^09\\d{8}$")) {
            toast("Use format like 09********");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            toast("Please enter password");
            return false;
        }

        return true;
    }

    private void loginSuccess(DataSnapshot snapshot, String userId) {
        Employee employee = snapshot.getValue(Employee.class);

        if (employee != null) {
            Paper.book().write(Common.USER_KEY, userId);
            Paper.book().write("currentUser", employee);

            startActivity(new Intent(this, Home.class));
            finish();
        }

        showLoading(false);
    }

    // -------------------- FINGERPRINT --------------------

    private void handleFingerprintLogin() {

        String savedUserId = Paper.book().read(Common.USER_KEY);

        if (savedUserId == null) {
            toast("Please login once using phone & password");
            return;
        }

        usersRef.child(savedUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            showFingerprintDialog(savedUserId);
                        } else {
                            Paper.book().destroy();
                            toast("Account not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        toast("Error checking user");
                    }
                });
    }

    private void showFingerprintDialog(String userId) {

        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Fingerprint Authentication")
                        .setSubtitle("Authenticate to login")
                        .setNegativeButtonText("Cancel")
                        .build();

        BiometricPrompt biometricPrompt =
                new BiometricPrompt(this,
                        ContextCompat.getMainExecutor(this),
                        new BiometricPrompt.AuthenticationCallback() {

                            @Override
                            public void onAuthenticationSucceeded(
                                    @NonNull BiometricPrompt.AuthenticationResult result) {

                                usersRef.child(userId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Employee employee = snapshot.getValue(Employee.class);
                                                    Paper.book().write(Common.USER_KEY, userId);
                                                    assert employee != null;
                                                    Paper.book().write("currentUser", employee);

                                                    startActivity(new Intent(SignIn.this, Home.class));
                                                    finish();
                                                } else {
                                                    toast("User not found");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                toast("Database error");
                                            }
                                        });
                            }

                            @Override
                            public void onAuthenticationError(int errorCode,
                                                              @NonNull CharSequence errString) {
                                toast(errString.toString());
                            }
                        });

        biometricPrompt.authenticate(promptInfo);
    }

    // -------------------- UI HELPERS --------------------

    private void togglePasswordVisibility() {

        if (isPasswordVisible) {
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            togglePassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            togglePassword.setImageResource(R.drawable.ic_visibility);
        }

        isPasswordVisible = !isPasswordVisible;
        edtPassword.setSelection(edtPassword.getText().length());
    }

    private void showLoading(boolean show) {
        loadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // HASH Password

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}