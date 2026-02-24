package com.bright.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bright.client.Assets.AddAssetScan;
import com.bright.client.Assets.DisposableAssets;
import com.bright.client.Assets.FixedAssets;
import com.bright.client.Employees.EmployeeManagement;
import com.bright.client.Fragments.DashboardFragment;
import com.bright.client.Fragments.ProfileFragment;
import com.bright.client.Fragments.SalesFragment;
import com.bright.client.Fragments.ScanFragment;
import com.bright.client.Inventory.AddWarehouse;
import com.bright.client.Inventory.Categories;
import com.bright.client.Inventory.ProductList;
import com.bright.client.Inventory.Products;
import com.bright.client.Inventory.Warehouses;
import com.bright.client.Model.Employee;
import com.bright.client.Purchasing.AddSupplier;
import com.bright.client.Purchasing.PurchaseOrderChoose;
import com.bright.client.Purchasing.PurchasingInvoice;
import com.bright.client.Purchasing.SuppliersList;
import com.bright.client.Sales.AddCustomer;
import com.bright.client.Sales.CustomerList;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.paperdb.Paper;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    private ImageView btnMenu;
    private DrawerLayout drawerLayout;

    //user data
    Employee currentUser;

    //Side Navigation Bar
    private LinearLayout menuEmployee, menuFinance, menuInventory, menuPurchase, menuAsset, menuSales, menuWarehouse;
    private LinearLayout subEmployee, subFinance, subInventory, subPurchase, subAsset, subSales, subWarehouse;
    private ImageView arrowEmployee, arrowFinance, arrowInventory, arrowPurchase, arrowAsset, arrowSales, arrow_warehouse;

    private LinearLayout btnRoles, btnEmployees, btnAttendance;
    private LinearLayout btnIncome, btnExpense, btnAccount;
    private LinearLayout btnCategories, btnRawMaterials, btnProductList;
    private LinearLayout btnAddSupplier, btnPurchaseOrder, btnSuppliers;
    private LinearLayout btnAddAsset, btnFixedAsset, btnDisposableAsset;
    private LinearLayout btnAddCustomer, btnCustomerList, btnSale;
    private LinearLayout btnAddWarehouse, btnWarehouses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        initViews();
        getUserData();
        initBottomNavigation();

        btnMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });

        subMenuFunc();
        subMenus();


        // Default fragment
        loadFragment(new DashboardFragment());
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
    }



    private void initViews() {

        //Drawer Menu
        btnMenu = findViewById(R.id.btn_menu);
        drawerLayout = findViewById(R.id.drawer_layout);

        //Side Navigation Bar
        menuEmployee = findViewById(R.id.menu_employee);
        menuFinance = findViewById(R.id.menu_finance);
        menuInventory = findViewById(R.id.menu_inventory);
        menuPurchase = findViewById(R.id.menu_purchase);
        menuAsset = findViewById(R.id.menu_asset);
        menuSales = findViewById(R.id.menu_sales);
        menuWarehouse = findViewById(R.id.menu_warehouse);

        subEmployee = findViewById(R.id.sub_employee);
        subFinance = findViewById(R.id.sub_finance);
        subInventory = findViewById(R.id.sub_inventory);
        subPurchase = findViewById(R.id.sub_purchase);
        subAsset = findViewById(R.id.sub_asset);
        subSales = findViewById(R.id.sub_sales);
        subWarehouse = findViewById(R.id.sub_warehouse);

        arrowEmployee = findViewById(R.id.arrow_employee);
        arrowFinance = findViewById(R.id.arrow_finance);
        arrowInventory = findViewById(R.id.arrow_inventory);
        arrowPurchase = findViewById(R.id.arrow_purchase);
        arrowAsset = findViewById(R.id.arrow_asset);
        arrowSales = findViewById(R.id.arrow_sales);
        arrow_warehouse = findViewById(R.id.arrow_warehouse);

        //side sub menus

        btnRoles = findViewById(R.id. btn_roles);
        btnEmployees = findViewById(R.id.btn_employees);
        btnAttendance = findViewById(R.id.btn_attendance);

        btnIncome = findViewById(R.id.btn_income);
        btnExpense = findViewById(R.id.btn_expense);
        btnAccount = findViewById(R.id.btn_account);

        btnCategories = findViewById(R.id.btn_categories);
        btnRawMaterials = findViewById(R.id.btn_raw_materials);
        btnProductList = findViewById(R.id.btn_product_list);

        btnAddSupplier = findViewById(R.id.btn_add_supplier);
        btnPurchaseOrder = findViewById(R.id.btn_purchase_order);
        btnSuppliers = findViewById(R.id.btn_suppliers);

        btnAddAsset = findViewById(R.id.btn_add_asset);
        btnFixedAsset = findViewById(R.id.btn_fixed);
        btnDisposableAsset = findViewById(R.id.btn_disposable);

        btnAddCustomer = findViewById(R.id.btn_add_customer);
        btnCustomerList = findViewById(R.id.btn_customer_list);
        btnSale = findViewById(R.id.btn_sale);

        btnWarehouses = findViewById(R.id.btn_warehouse);
        btnAddWarehouse = findViewById(R.id.btn_add_warehouse);


    }

    private void getUserData() {
        Paper.init(this);
        currentUser = Paper.book().read("currentUser");
    }

    private void subMenuFunc() {

        menuEmployee.setOnClickListener(v -> {
            if (subEmployee.getVisibility() == View.GONE) {
                closeAllSubMenus();
                subEmployee.setVisibility(View.VISIBLE);
                arrowEmployee.setRotation(180f);
            } else {
                closeAllSubMenus();
            }
        });

        menuFinance.setOnClickListener(v -> {
            if (subFinance.getVisibility() == View.GONE) {
                closeAllSubMenus();
                subFinance.setVisibility(View.VISIBLE);
                arrowFinance.setRotation(180f);
            } else {
                closeAllSubMenus();
            }
        });

        menuInventory.setOnClickListener(v -> {
            if (subInventory.getVisibility() == View.GONE) {
                closeAllSubMenus();
                subInventory.setVisibility(View.VISIBLE);
                arrowInventory.setRotation(180f);
            } else {
                closeAllSubMenus();
            }
        });

        menuPurchase.setOnClickListener(v -> {
            if (subPurchase.getVisibility() == View.GONE) {
                closeAllSubMenus();
                subPurchase.setVisibility(View.VISIBLE);
                arrowPurchase.setRotation(180f);
            } else {
                closeAllSubMenus();
            }
        });

        menuAsset.setOnClickListener(v -> {
            if (subAsset.getVisibility() == View.GONE) {
                closeAllSubMenus();
                subAsset.setVisibility(View.VISIBLE);
                arrowAsset.setRotation(180f);
            } else {
                closeAllSubMenus();
            }
        });

        menuSales.setOnClickListener(v -> {
            if (subSales.getVisibility() == View.GONE) {
                closeAllSubMenus();
                subSales.setVisibility(View.VISIBLE);
                arrowSales.setRotation(180f);
            } else {
                closeAllSubMenus();
            }
        });

        menuWarehouse.setOnClickListener(v -> {
            if (subWarehouse.getVisibility() == View.GONE) {
                closeAllSubMenus();
                subWarehouse.setVisibility(View.VISIBLE);
                arrow_warehouse.setRotation(180f);
            } else {
                closeAllSubMenus();
            }
        });
    }

    private void subMenus(){

        btnRoles.setOnClickListener(v -> {

        });

        btnEmployees.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, EmployeeManagement.class);
            startActivity(intent);
        });

        btnAttendance.setOnClickListener(v -> {

        });

        btnIncome.setOnClickListener(v -> {

        });

        btnExpense.setOnClickListener(v -> {

        });

        btnAccount.setOnClickListener(v -> {

        });

        btnCategories.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Categories.class);
            startActivity(intent);
        });

        btnRawMaterials.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, PurchasingInvoice.class);
            startActivity(intent);
        });


        btnProductList.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, ProductList.class);
            startActivity(intent);
        });

        btnAddSupplier.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddSupplier.class);
            startActivity(intent);
        });

        btnPurchaseOrder.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, PurchaseOrderChoose.class);
            startActivity(intent);
        });

        btnSuppliers.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, SuppliersList.class);
            startActivity(intent);
        });

        btnAddAsset.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddAssetScan.class);
            startActivity(intent);
        });

        btnFixedAsset.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, FixedAssets.class);
            intent.putExtra("assigned", currentUser.getUserId());
            intent.putExtra("position", currentUser.getPosition());
            startActivity(intent);

        });

        btnDisposableAsset.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, DisposableAssets.class);
            intent.putExtra("assigned", currentUser.getUserId());
            intent.putExtra("position", currentUser.getPosition());
            startActivity(intent);
        });

        btnAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddCustomer.class);
            startActivity(intent);
        });

        btnCustomerList.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, CustomerList.class);
            startActivity(intent);
        });

        btnSale.setOnClickListener(v -> {

        });

        btnWarehouses.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Warehouses.class);
            startActivity(intent);
        });

        btnAddWarehouse.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddWarehouse.class);
            startActivity(intent);
        });




    }

    private void closeAllSubMenus() {
        subEmployee.setVisibility(View.GONE);
        subFinance.setVisibility(View.GONE);
        subInventory.setVisibility(View.GONE);
        subPurchase.setVisibility(View.GONE);
        subAsset.setVisibility(View.GONE);
        subSales.setVisibility(View.GONE);
        subWarehouse.setVisibility(View.GONE);

        arrowEmployee.setRotation(0f);
        arrowFinance.setRotation(0f);
        arrowInventory.setRotation(0f);
        arrowPurchase.setRotation(0f);
        arrowAsset.setRotation(0f);
        arrowSales.setRotation(0f);
        arrow_warehouse.setRotation(0f);

    }


    private void initBottomNavigation() {
        bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    loadFragment(new DashboardFragment());
                    return true;

                case R.id.nav_sales:
                    loadFragment(new SalesFragment());
                    return true;

                case R.id.nav_scan:
                    loadFragment(new ScanFragment());
                    return true;

                case R.id.nav_profile:
                    loadFragment(new ProfileFragment());
                    return true;
            }
            return false;
        });

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}