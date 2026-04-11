package com.example.myapplicationmedtech;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupMedicineList();
        setupQuickActions();
        setupBottomNavigation();
        setupThemeMenu();
    }

    private void setupMedicineList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerTodayMedicine);
        TextView emptyState = findViewById(R.id.txtEmptyMedicine);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        List<MedicineScheduleItem> items = new ArrayList<>();
        recyclerView.setAdapter(new MedicineScheduleAdapter(items));

        boolean isEmpty = items.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void setupQuickActions() {
        MaterialButton btnCapture = findViewById(R.id.btnCaptureMedicine);
        MaterialButton btnAdd = findViewById(R.id.btnAddMedicine);
        MaterialButton btnList = findViewById(R.id.btnMedicineList);
        MaterialButton btnSchedule = findViewById(R.id.btnSchedule);
        MaterialButton btnHistory = findViewById(R.id.btnHistory);

        btnCapture.setOnClickListener(v -> showStubToast());
        btnAdd.setOnClickListener(v -> showStubToast());
        btnList.setOnClickListener(v -> showStubToast());
        btnSchedule.setOnClickListener(v -> showStubToast());
        btnHistory.setOnClickListener(v -> showStubToast());
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                return true;
            }
            showStubToast();
            return true;
        });
    }

    private void setupThemeMenu() {
        ImageButton settingsButton = findViewById(R.id.btnSettings);
        settingsButton.setOnClickListener(v -> {
            String[] themeOptions = new String[] {
                    getString(R.string.theme_light),
                    getString(R.string.theme_dark),
                    getString(R.string.theme_system)
            };

            new AlertDialog.Builder(this)
                    .setTitle(R.string.theme_menu_title)
                    .setItems(themeOptions, (dialog, which) -> {
                        int mode;
                        if (which == 0) {
                            mode = AppCompatDelegate.MODE_NIGHT_NO;
                        } else if (which == 1) {
                            mode = AppCompatDelegate.MODE_NIGHT_YES;
                        } else {
                            mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                        }

                        SharedPreferences prefs = getSharedPreferences(SplashActivity.PREFS_NAME, MODE_PRIVATE);
                        prefs.edit().putInt(SplashActivity.KEY_THEME_MODE, mode).apply();
                        AppCompatDelegate.setDefaultNightMode(mode);
                    })
                    .show();
        });
    }

    private void showStubToast() {
        Toast.makeText(this, R.string.toast_stub, Toast.LENGTH_SHORT).show();
    }
}