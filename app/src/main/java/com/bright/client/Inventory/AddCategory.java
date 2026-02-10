package com.bright.client.Inventory;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bright.client.Model.Category;
import com.bright.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddCategory extends AppCompatActivity {

    private static final String CATEGORY_SEQ = "categorySeq";

    private ImageView categoryIcon;
    private EditText categoryName;
    private View btnCreateCategory;

    private int selectedIcon = R.drawable.ic_menu_outline;

    private DatabaseReference rootRef;
    private AlertDialog loadingDialog;

    private final int[] icons = {
            R.drawable.ic_amplifier,
            R.drawable.ic_battery,
            R.drawable.ic_bulb,
            R.drawable.ic_panel,
            R.drawable.ic_speaker
    };

    private final String[] iconNames = {
            "Amplifier",
            "Battery",
            "Lantern",
            "Solar Panel",
            "Speakers"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        setupStatusBar();
        initViews();

        rootRef = FirebaseDatabase.getInstance().getReference();

        categoryIcon.setOnClickListener(v -> showIconDialog());
        btnCreateCategory.setOnClickListener(v -> createCategory());
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initViews() {
        categoryIcon = findViewById(R.id.category_icon);
        categoryName = findViewById(R.id.category_name);
        btnCreateCategory = findViewById(R.id.btn_create_category);
    }

    private void showIconDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category Icon");

        builder.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.layout_icon,
                R.id.name,
                iconNames
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView icon = view.findViewById(R.id.icon);
                icon.setImageResource(icons[position]);
                return view;
            }
        }, (dialog, which) -> {
            selectedIcon = icons[which];
            categoryIcon.setImageResource(selectedIcon);
        });

        builder.show();
    }

    private void createCategory() {
        String name = categoryName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            categoryName.setError("Category name required");
            categoryName.requestFocus();
            return;
        }

        showLoading("Saving category...");

        rootRef.child("Sequences").child(CATEGORY_SEQ)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        long currentSeq = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
                        long newSeq = currentSeq + 1;

                        String categoryId = String.format("HALI-CAT-%06d", newSeq);

                        Category category = new Category(
                                categoryId,
                                name,
                                selectedIcon,
                                true
                        );

                        rootRef.child("Categories")
                                .child(categoryId)
                                .setValue(category)
                                .addOnSuccessListener(unused -> {
                                    rootRef.child("Sequences")
                                            .child(CATEGORY_SEQ)
                                            .setValue(newSeq);

                                    hideLoading();
                                    Toast.makeText(AddCategory.this,
                                            "Category Added",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    hideLoading();
                                    Toast.makeText(AddCategory.this,
                                            e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        hideLoading();
                        Toast.makeText(AddCategory.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
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
