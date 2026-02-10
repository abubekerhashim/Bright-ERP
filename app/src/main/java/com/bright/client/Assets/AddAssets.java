package com.bright.client.Assets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.InputStream;

public class AddAssets extends AppCompatActivity {

    Spinner typeSpinner, categorySpinner;
    private EditText assetName, assetDetail, assetLocation;
    private AppCompatButton btnCreate;
    private LinearLayout assetAddPhoto, txtImgLayout;
    private DatabaseReference assetRef;
    private FirebaseDatabase database;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String typeSelected = "", categorySelected = "";
    private ImageView showImage;
    private String assetId, mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asset);

        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        initViews();
        initializeFirebase();
        spinnersFunctionality();

        Intent intent = getIntent();
        assetId = intent.getStringExtra("assetId");
        mode = intent.getStringExtra("mode");

        if (assetId == null || assetId.trim().isEmpty()) {
            Toast.makeText(this, "No Asset ID found", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }

        if ("edit".equalsIgnoreCase(mode)) {
            loadAssetDataForEditing(assetId);
            btnCreate.setText("Update Asset");
            btnCreate.setOnClickListener(v -> {
                if (validateInputs()) {
                    updateAssetInFirebase(assetId);
                }
            });
            assetAddPhoto.setOnClickListener(v -> openGallery());
        } else {
            DatabaseReference assetCheckRef = assetRef.child(assetId);
            assetCheckRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(AddAssets.this, "Asset ID already registered", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        uploadAndComplete();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AddAssets.this, "Database error", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        }
    }

    private void initViews() {
        categorySpinner = findViewById(R.id.category_spinner);
        typeSpinner = findViewById(R.id.asset_type_spinner);
        assetName = findViewById(R.id.asset_name);
        assetDetail = findViewById(R.id.asset_detail);
        assetLocation = findViewById(R.id.asset_location);
        assetAddPhoto = findViewById(R.id.asset_photo_layout);
        showImage = findViewById(R.id.show_image);
        btnCreate = findViewById(R.id.btn_create_asset);
        txtImgLayout = findViewById(R.id.txt_img_layout);
    }

    private void initializeFirebase() {
        database = FirebaseDatabase.getInstance();
        assetRef = database.getReference("Assets");
    }

    private void spinnersFunctionality() {
        String[] typeItems = {"Select Type", "Fixed Asset", "Disposable Asset"};
        String[] categoryItems = {"Select Category", "Electronics", "Furniture", "Accessories", "Others"};

        setUpSpinner(typeSpinner, typeItems, s -> typeSelected = s);
        setUpSpinner(categorySpinner, categoryItems, s -> categorySelected = s);
    }

    private void setUpSpinner(Spinner spinner, String[] items, SpinnerCallback callback) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callback.onItemSelected(parent.getItemAtPosition(position).toString());
                ((TextView) view).setTextSize(12);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    interface SpinnerCallback {
        void onItemSelected(String selected);
    }

    private void uploadAndComplete() {
        assetAddPhoto.setOnClickListener(v -> openGallery());
        btnCreate.setOnClickListener(v -> {
            if (validateInputs()) {
                uploadToFirebase(assetId);
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            txtImgLayout.setVisibility(View.GONE);
            showImage.setVisibility(View.VISIBLE);
            showImage.setImageURI(imageUri);
        }
    }

    private boolean validateInputs() {
        if (imageUri == null && !"edit".equalsIgnoreCase(mode)) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (assetName.getText().toString().trim().isEmpty()) {
            assetName.setError("Asset name is required");
            return false;
        }
        if (assetDetail.getText().toString().trim().isEmpty()) {
            assetDetail.setError("Asset detail is required");
            return false;
        }
        if (assetLocation.getText().toString().trim().isEmpty()) {
            assetLocation.setError("Asset location is required");
            return false;
        }

        return true;
    }

    private void uploadToFirebase(String assetId) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Compressing and uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String storagePath = "Assets/" + typeSelected + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(storagePath);
        byte[] compressedImage = compressImage(imageUri, 150);

        storageRef.putBytes(compressedImage)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            DatabaseReference mainRef = assetRef.child(assetId);

                            mainRef.child("id").setValue(assetId);
                            mainRef.child("type").setValue(typeSelected);
                            mainRef.child("name").setValue(assetName.getText().toString().trim());
                            mainRef.child("category").setValue(categorySelected);
                            mainRef.child("location").setValue(assetLocation.getText().toString());
                            mainRef.child("regDate").setValue(System.currentTimeMillis());
                            mainRef.child("status").setValue("Active");
                            mainRef.child("detail").setValue(assetDetail.getText().toString());
                            mainRef.child("imageUrl").setValue(downloadUrl);

                            progressDialog.dismiss();
                            Toast.makeText(AddAssets.this, "Asset Created Successfully!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAssetInFirebase(String assetId) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating asset...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference mainRef = assetRef.child(assetId);
        mainRef.child("type").setValue(typeSelected);
        mainRef.child("name").setValue(assetName.getText().toString().trim());
        mainRef.child("category").setValue(categorySelected);
        mainRef.child("location").setValue(assetLocation.getText().toString());
        mainRef.child("detail").setValue(assetDetail.getText().toString());


        if (imageUri != null) {
            String storagePath = "Assets/" + typeSelected + "/" + System.currentTimeMillis() + ".jpg";
            StorageReference storageRef = FirebaseStorage.getInstance().getReference(storagePath);
            byte[] compressedImage = compressImage(imageUri, 150);

            storageRef.putBytes(compressedImage)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                mainRef.child("imageUrl").setValue(uri.toString());
                                progressDialog.dismiss();
                                Toast.makeText(this, "Asset Updated!", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Asset Updated!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void loadAssetDataForEditing(String assetId) {
        assetRef.child(assetId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    assetName.setText(snapshot.child("name").getValue(String.class));
                    assetDetail.setText(snapshot.child("detail").getValue(String.class));
                    assetLocation.setText(snapshot.child("location").getValue(String.class));
                    setSpinnerSelection(typeSpinner, snapshot.child("type").getValue(String.class));
                    setSpinnerSelection(categorySpinner, snapshot.child("category").getValue(String.class));


                    String image = snapshot.child("imageUrl").getValue(String.class);
                    Glide.with(AddAssets.this)
                            .load(image)
                            .into(showImage);
                    showImage.setVisibility(View.VISIBLE);
                    txtImgLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddAssets.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private Bitmap handleImageRotation(Uri uri) {
        try {
            InputStream input = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();

            InputStream exifInput = getContentResolver().openInputStream(uri);
            ExifInterface exif = new ExifInterface(exifInput);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifInput.close();

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] compressImage(Uri uri, int maxKB) {
        try {
            Bitmap bitmap = handleImageRotation(uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int quality = 100;

            do {
                stream.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                quality -= 5;
            } while (stream.toByteArray().length / 1024 > maxKB && quality > 10);

            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}