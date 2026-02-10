package com.bright.client.Employees;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bright.client.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class RegisterEmployee extends AppCompatActivity {

    private Spinner positionSpinner;
    private CardView choosePhoto;
    private ImageView showPhoto, chooseIcon;
    private Uri imageUri;

    private LinearLayout layoutWoman, layoutMan, btnRegister;
    private String selectedGender = "";

    private TextView firstName, middleName, lastName,
            edtPhone, edtCity, edtSubCity, edtHno;

    private StorageReference storageRef;
    private RelativeLayout loadingLayout;

    private boolean isEditMode = false;
    private String editingPhone = null;

    private ArrayAdapter<String> positionAdapter;
    private final ArrayList<String> positionList = new ArrayList<>();

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_employee);


        setupStatusBar();

        positionSpinner = findViewById(R.id.position_spinner);
        choosePhoto = findViewById(R.id.choose_photo);
        showPhoto = findViewById(R.id.show_photo);
        chooseIcon = findViewById(R.id.choose_photo_icon);

        layoutWoman = findViewById(R.id.gender_woman);
        layoutMan = findViewById(R.id.gender_man);
        btnRegister = findViewById(R.id.btn_register);

        firstName = findViewById(R.id.edt_first_name);
        middleName = findViewById(R.id.edt_middle_name);
        lastName = findViewById(R.id.edt_last_name);
        edtPhone = findViewById(R.id.edt_phone);
        edtCity = findViewById(R.id.edt_city);
        edtSubCity = findViewById(R.id.edt_subcity);
        edtHno = findViewById(R.id.edt_hno);

        loadingLayout = findViewById(R.id.loading_layout);
        backButton = findViewById(R.id.back_button);

        storageRef = FirebaseStorage.getInstance().getReference("EmployeePhotos");

        setupPositionSpinner();
        setupGenderSelection();
        choosePhoto.setOnClickListener(v -> openGallery());

        if (getIntent().hasExtra("userId")) {
            isEditMode = true;
            editingPhone = getIntent().getStringExtra("userId");
            loadEmployeeData(editingPhone);
            setButtonText("Update");
        } else {
            setButtonText("Register");
        }

        btnRegister.setOnClickListener(v -> {
            if (!validateFields()) return;
            showLoading();

            if (isEditMode) {
                updateEmployee();
            } else {
                checkIfEmployeeExists();
            }
        });

        backButton.setOnClickListener(v -> onBackPressed());
    }

    //-------------------- POSITION SPINNER --------------------
    private void setupPositionSpinner() {

        positionList.clear();
        positionList.add("Select position");

        positionAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                positionList
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };

        positionSpinner.setAdapter(positionAdapter);

        FirebaseDatabase.getInstance()
                .getReference("Positions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        positionList.clear();
                        positionList.add("Select position");

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String name = ds.child("name").getValue(String.class);
                            if (name != null) positionList.add(name);
                        }

                        positionAdapter.notifyDataSetChanged();

                        if (isEditMode && editingPhone != null) {
                            loadEmployeePosition(editingPhone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegisterEmployee.this,
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateFields() {

        if (firstName.getText().toString().trim().isEmpty()) {
            firstName.setError("Required");
            firstName.requestFocus();
            return false;
        }

        if (middleName.getText().toString().trim().isEmpty()) {
            middleName.setError("Required");
            middleName.requestFocus();
            return false;
        }

        if (lastName.getText().toString().trim().isEmpty()) {
            lastName.setError("Required");
            lastName.requestFocus();
            return false;
        }

        if (edtPhone.getText().toString().trim().isEmpty()) {
            edtPhone.setError("Required");
            edtPhone.requestFocus();
            return false;
        }

        // Ethiopian phone format check (simple)
        if (!edtPhone.getText().toString().trim().matches("^(09|07)\\d{8}$")) {
            edtPhone.setError("Invalid phone number");
            edtPhone.requestFocus();
            return false;
        }

        if (positionSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select position", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isEditMode && imageUri == null) {
            Toast.makeText(this, "Please select photo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtCity.getText().toString().trim().isEmpty()) {
            edtCity.setError("Required");
            edtCity.requestFocus();
            return false;
        }

        if (edtSubCity.getText().toString().trim().isEmpty()) {
            edtSubCity.setError("Required");
            edtSubCity.requestFocus();
            return false;
        }

        if (edtHno.getText().toString().trim().isEmpty()) {
            edtHno.setError("Required");
            edtHno.requestFocus();
            return false;
        }

        return true;
    }


    //-------------------- GENDER --------------------
    private void setupGenderSelection() {
        layoutWoman.setOnClickListener(v -> {
            selectedGender = "Woman";
            layoutWoman.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D5F4F4")));
            layoutMan.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        });

        layoutMan.setOnClickListener(v -> {
            selectedGender = "Man";
            layoutMan.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D5F4F4")));
            layoutWoman.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        });
    }

    //-------------------- LOAD EMPLOYEE --------------------
    private void loadEmployeeData(String phone) {
        FirebaseDatabase.getInstance()
                .getReference("Employees")
                .child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        firstName.setText(snapshot.child("firstName").getValue(String.class));
                        middleName.setText(snapshot.child("middleName").getValue(String.class));
                        lastName.setText(snapshot.child("lastName").getValue(String.class));
                        edtPhone.setText(snapshot.child("userId").getValue(String.class));
                        edtPhone.setEnabled(false);
                        edtCity.setText(snapshot.child("city").getValue(String.class));
                        edtSubCity.setText(snapshot.child("subCity").getValue(String.class));
                        edtHno.setText(snapshot.child("houseNo").getValue(String.class));

                        selectedGender = snapshot.child("gender").getValue(String.class);
                        if ("Woman".equals(selectedGender)) layoutWoman.performClick();
                        else if ("Man".equals(selectedGender)) layoutMan.performClick();

                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                        if (imageUrl != null) {
                            showPhoto.setVisibility(View.VISIBLE);
                            chooseIcon.setVisibility(View.GONE);
                            Glide.with(RegisterEmployee.this).load(imageUrl).into(showPhoto);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegisterEmployee.this,
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //-------------------- SPINNER POSITION --------------------
    private void loadEmployeePosition(String phone) {
        FirebaseDatabase.getInstance()
                .getReference("Employees")
                .child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String position = snapshot.child("position").getValue(String.class);
                        if (position != null && positionAdapter != null) {
                            int pos = positionAdapter.getPosition(position);
                            if (pos >= 0) positionSpinner.setSelection(pos);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    //-------------------- REGISTER --------------------
    private void checkIfEmployeeExists() {
        FirebaseDatabase.getInstance()
                .getReference("Employees")
                .child(edtPhone.getText().toString().trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            hideLoading();
                            edtPhone.setError("Employee already exists");
                        } else {
                            getEmployeeId();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        hideLoading();
                        Toast.makeText(RegisterEmployee.this,
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getEmployeeId() {
        FirebaseDatabase.getInstance()
                .getReference("EmployeeIdSequence/lastNumber")
                .runTransaction(new com.google.firebase.database.Transaction.Handler() {
                    @NonNull
                    @Override
                    public com.google.firebase.database.Transaction.Result doTransaction(
                            @NonNull com.google.firebase.database.MutableData currentData) {
                        Integer v = currentData.getValue(Integer.class);
                        currentData.setValue(v == null ? 1 : v + 1);
                        return com.google.firebase.database.Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                        if (committed && snapshot != null) {
                            uploadPhoto(generateEmployeeId(snapshot.getValue(Integer.class)));
                        }
                    }
                });
    }

    private void uploadPhoto(String employeeId) {
        Uri finalUri = compressImage(imageUri);
        storageRef.child(employeeId + ".jpg")
                .putFile(finalUri)
                .addOnSuccessListener(t ->
                        storageRef.child(employeeId + ".jpg").getDownloadUrl()
                                .addOnSuccessListener(uri ->
                                        saveEmployee(employeeId, uri.toString())))
                .addOnFailureListener(e -> {
                    hideLoading();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveEmployee(String employeeId, String imageUrl) {

        DatabaseReference emp = FirebaseDatabase.getInstance()
                .getReference("Employees")
                .child(edtPhone.getText().toString().trim());

        emp.child("employeeId").setValue(employeeId);
        emp.child("firstName").setValue(firstName.getText().toString().trim());
        emp.child("middleName").setValue(middleName.getText().toString().trim());
        emp.child("lastName").setValue(lastName.getText().toString().trim());
        emp.child("userId").setValue(edtPhone.getText().toString().trim());
        emp.child("city").setValue(edtCity.getText().toString().trim());
        emp.child("subCity").setValue(edtSubCity.getText().toString().trim());
        emp.child("houseNo").setValue(edtHno.getText().toString().trim());
        emp.child("gender").setValue(selectedGender);
        emp.child("position").setValue(positionSpinner.getSelectedItem().toString());
        emp.child("imageUrl").setValue(imageUrl);
        emp.child("suspend").setValue(false);

        emp.child("regDate").setValue(System.currentTimeMillis())
                .addOnSuccessListener(v -> {
                    hideLoading();
                    Toast.makeText(this, "Employee Registered", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    //-------------------- UPDATE --------------------
    private void updateEmployee() {
        DatabaseReference emp = FirebaseDatabase.getInstance()
                .getReference("Employees")
                .child(editingPhone);

        if (imageUri != null) {
            storageRef.child(editingPhone + ".jpg")
                    .putFile(imageUri)
                    .addOnSuccessListener(t ->
                            storageRef.child(editingPhone + ".jpg").getDownloadUrl()
                                    .addOnSuccessListener(uri ->
                                            saveUpdatedEmployee(emp, uri.toString())));
        } else {
            saveUpdatedEmployee(emp, null);
        }
    }

    private void saveUpdatedEmployee(DatabaseReference emp, String imageUrl) {
        emp.child("firstName").setValue(firstName.getText().toString().trim());
        emp.child("middleName").setValue(middleName.getText().toString().trim());
        emp.child("lastName").setValue(lastName.getText().toString().trim());
        emp.child("city").setValue(edtCity.getText().toString().trim());
        emp.child("subCity").setValue(edtSubCity.getText().toString().trim());
        emp.child("houseNo").setValue(edtHno.getText().toString().trim());
        emp.child("gender").setValue(selectedGender);
        emp.child("position").setValue(positionSpinner.getSelectedItem().toString());

        if (imageUrl != null) emp.child("imageUrl").setValue(imageUrl);

        hideLoading();
        Toast.makeText(this, "Employee updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    //-------------------- IMAGE --------------------
    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        imagePickerLauncher.launch(i);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    r -> {
                        if (r.getResultCode() == RESULT_OK && r.getData() != null) {
                            imageUri = r.getData().getData();
                            showPhoto.setImageURI(imageUri);
                            showPhoto.setVisibility(View.VISIBLE);
                            chooseIcon.setVisibility(View.GONE);
                        }
                    });

    //-------------------- UTILS --------------------
    private void setupStatusBar() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        w.setStatusBarColor(Color.WHITE);
    }

    private String generateEmployeeId(int number) {
        return String.format("HALI-EMP-%05d", number);
    }

    private void setButtonText(String text) {
        for (int i = 0; i < btnRegister.getChildCount(); i++) {
            View v = btnRegister.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setText(text);
                break;
            }
        }
    }

    private Uri compressImage(Uri uri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);

            File f = new File(getCacheDir(), "temp.jpg");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(out.toByteArray());
            fos.close();

            return Uri.fromFile(f);
        } catch (Exception e) {
            return uri;
        }
    }

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
    }
}


