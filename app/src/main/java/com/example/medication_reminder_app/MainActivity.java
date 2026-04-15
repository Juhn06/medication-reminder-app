package com.example.medication_reminder_app;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Xin quyền notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        // Setup NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        NavController navController = navHostFragment.getNavController();

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_medicines) {
                navController.navigate(R.id.medicineListFragment);
                return true;
            } else if (id == R.id.nav_history) {
                navController.navigate(R.id.historyFragment);
                return true;
            } else if (id == R.id.nav_sensor) {
                navController.navigate(R.id.sensorFragment);
                return true;
            }
            return false;
        });

        // Xử lý khi mở app từ thông báo → chuyển sang tab Lịch sử,
        String openTab = getIntent().getStringExtra("OPEN_TAB");// lấy yêu cầu để khởi động Activity
        if ("history".equals(openTab)) {
            navController.navigate(R.id.historyFragment);
            bottomNav.setSelectedItemId(R.id.nav_history);
        }
    }

    // Xử lý khi app đang chạy mà nhận intent mới từ thông báo, xử lý chạy nền
    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        String openTab = intent.getStringExtra("OPEN_TAB");
        if ("history".equals(openTab)) {
            navController.navigate(R.id.historyFragment);//chuyển sang tab lịch sử
            bottomNav.setSelectedItemId(R.id.nav_history);//highlight tab lịch sử
        }
    }
}