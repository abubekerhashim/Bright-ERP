package com.bright.client.Inventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bright.client.Model.Category;
import com.bright.client.Model.Product;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AddProduct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private AutoCompleteTextView categoryDropdown;
    private EditText productName, productModel,
            productCostPrice, productSellPrice, productUnit;

    private ImageView showPhoto;
    private LinearLayout createProduct;

    private Uri imageUri;

    private DatabaseReference categoryRef, productRef, sequenceRef;
    private StorageReference storageRef;

    private ArrayList<Category> categoryList = new ArrayList<>();

    private String selectedCategoryId = "";
    private String selectedCategoryName = "";
    private String selectedColor = "";
    private LinearLayout lastSelectedColor = null;


    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Status bar style
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        // Views
        categoryDropdown = findViewById(R.id.category_dropdown);
        productName = findViewById(R.id.product_name);
        productModel = findViewById(R.id.product_model);
        productCostPrice = findViewById(R.id.product_cost_price);
        productSellPrice = findViewById(R.id.product_sell_price);
        productUnit = findViewById(R.id.product_unit);
        showPhoto = findViewById(R.id.show_photo);
        createProduct = findViewById(R.id.product_create);

        // Firebase
        categoryRef = FirebaseDatabase.getInstance().getReference("Categories");
        productRef = FirebaseDatabase.getInstance().getReference("Products");
        sequenceRef = FirebaseDatabase.getInstance().getReference("Sequences");
        storageRef = FirebaseStorage.getInstance().getReference("product_images");

        chooseCategory();

        // Photo click
        findViewById(R.id.choose_photo).setOnClickListener(v -> openFileChooser());

        // Create product click
        createProduct.setOnClickListener(v -> validateAndUpload());

        // Setup colors
        setupColorClick(R.id.color_black, "Black");
        setupColorClick(R.id.color_white, "White");
        setupColorClick(R.id.color_red, "Red");
        setupColorClick(R.id.color_blue, "Blue");
        setupColorClick(R.id.color_green, "Green");
        setupColorClick(R.id.color_yellow, "Yellow");
        setupColorClick(R.id.color_orange, "Orange");
        setupColorClick(R.id.color_purple, "Purple");
        setupColorClick(R.id.color_pink, "Pink");
        setupColorClick(R.id.color_brown, "Brown");
        setupColorClick(R.id.color_gray, "Gray");
        setupColorClick(R.id.color_cyan, "Cyan");
    }

    // ================= CATEGORY =================

    private void chooseCategory() {

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categoryList.clear();
                ArrayList<String> names = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Category category = ds.getValue(Category.class);

                    if (category != null && category.isStatus()) {
                        category.setId(ds.getKey());
                        categoryList.add(category);
                        names.add(category.getName());
                    }
                }

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(AddProduct.this,
                                android.R.layout.simple_dropdown_item_1line,
                                names);

                categoryDropdown.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProduct.this,
                        "Failed to load categories",
                        Toast.LENGTH_SHORT).show();
            }
        });

        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
            Category selected = categoryList.get(position);
            selectedCategoryId = selected.getId();
            selectedCategoryName = selected.getName();
        });
    }

    // ================= IMAGE PICKER =================

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            imageUri = data.getData();
            showPhoto.setImageURI(imageUri);
            showPhoto.setVisibility(View.VISIBLE);
        }
    }

    // ================= COLOR =================

    private void setupColorClick(int viewId, String colorName) {

        LinearLayout layout = findViewById(viewId);

        layout.setOnClickListener(v -> {

            if (lastSelectedColor != null) {
                lastSelectedColor.setBackgroundTintList(
                        getColorStateList(R.color.white_grey));
            }

            layout.setBackgroundTintList(
                    getColorStateList(R.color.unselected_icon_color));

            lastSelectedColor = layout;
            selectedColor = colorName;
        });
    }

    // ================= VALIDATION =================

    private void validateAndUpload() {

        String name = productName.getText().toString().trim();
        String model = productModel.getText().toString().trim();
        String costStr = productCostPrice.getText().toString().trim();
        String sellStr = productSellPrice.getText().toString().trim();
        String unit = productUnit.getText().toString().trim();

        if (imageUri == null) {
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategoryId.isEmpty()) {
            Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedColor.isEmpty()) {
            Toast.makeText(this, "Select Color", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || model.isEmpty() ||
                costStr.isEmpty() || sellStr.isEmpty() ||
                unit.isEmpty()) {

            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double costPrice = Double.parseDouble(costStr);
        double sellPrice = Double.parseDouble(sellStr);

        showLoading("Creating Product...");
        generateProductIdAndUpload(name, model, costPrice, sellPrice, unit);
    }

    // ================= GENERATE ID =================

    private void generateProductIdAndUpload(String name,
                                            String model,
                                            double costPrice,
                                            double sellPrice,
                                            String unit) {

        sequenceRef.child("productSeq")
                .runTransaction(new Transaction.Handler() {

                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                        Integer currentValue = currentData.getValue(Integer.class);

                        if (currentValue == null) {
                            currentData.setValue(1);
                            return Transaction.success(currentData);
                        }

                        currentData.setValue(currentValue + 1);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error,
                                           boolean committed,
                                           @Nullable DataSnapshot snapshot) {

                        if (committed && snapshot != null) {

                            int seqNumber = snapshot.getValue(Integer.class);

                            String formatted =
                                    String.format("%06d", seqNumber);

                            String prodId =
                                    "HALI-PROD-" + formatted;

                            uploadImageAndSave(prodId,
                                    name, model,
                                    costPrice, sellPrice, unit);
                        }
                    }
                });
    }

    // ================= UPLOAD & SAVE =================

    private void uploadImageAndSave(String prodId,
                                    String name,
                                    String model,
                                    double costPrice,
                                    double sellPrice,
                                    String unit) {

        StorageReference fileRef =
                storageRef.child(prodId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(task ->
                        fileRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {

                                    Product product = new Product(
                                            prodId,
                                            name,
                                            model,
                                            selectedCategoryName,
                                            selectedCategoryId,
                                            selectedColor,
                                            uri.toString(),
                                            unit,
                                            costPrice,
                                            sellPrice,
                                            true,
                                            0
                                    );

                                    productRef.child(prodId)
                                            .setValue(product)
                                            .addOnSuccessListener(unused -> {

                                                hideLoading();

                                                Toast.makeText(this,
                                                        "Product Created",
                                                        Toast.LENGTH_SHORT).show();

                                                finish();
                                            });
                                }))
                .addOnFailureListener(e -> {
                    hideLoading();
                    Toast.makeText(this,
                            "Upload Failed",
                            Toast.LENGTH_SHORT).show();
                });

    }

    private void showLoading(String message) {
        if (loadingDialog != null && loadingDialog.isShowing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_loading, null);
        builder.setView(view);
        builder.setCancelable(false);

        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
