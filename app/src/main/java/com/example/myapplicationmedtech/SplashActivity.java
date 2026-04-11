package com.example.myapplicationmedtech;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "medtech_prefs";
    public static final String KEY_ONBOARDING_DONE = "onboarding_done";
    public static final String KEY_THEME_MODE = "theme_mode";

    @Override
    @SuppressLint("CustomSplashScreen")
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isOnboardingDone = prefs.getBoolean(KEY_ONBOARDING_DONE, false);

        Intent nextIntent = new Intent(this, isOnboardingDone ? MainActivity.class : OnboardingActivity.class);
        startActivity(nextIntent);
        finish();
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int mode = prefs.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(mode);
    }
}