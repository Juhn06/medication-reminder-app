package com.example.medication_reminder_app.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.medication_reminder_app.MainActivity;
import com.example.medication_reminder_app.ui.onboarding.OnboardingPage;
import com.example.medication_reminder_app.ui.onboarding.OnboardingPagerAdapter;
import com.example.medication_reminder_app.SplashActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 onboardingPager;
    private Button skipButton;
    private Button nextButton;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        setupPager();
        setupActions();
    }

    private void bindViews() {
        onboardingPager = findViewById(R.id.onboardingPager);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);
        startButton = findViewById(R.id.startButton);
    }

    private void setupPager() {
        List<OnboardingPage> pages = new ArrayList<>();
        pages.add(new OnboardingPage(
                R.drawable.ic_pill_24,
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1)
        ));
        pages.add(new OnboardingPage(
                R.drawable.ic_notification_24,
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2)
        ));
        pages.add(new OnboardingPage(
                R.drawable.ic_schedule_24,
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3)
        ));
        pages.add(new OnboardingPage(
                R.drawable.ic_camera_24,
                getString(R.string.onboarding_title_4),
                getString(R.string.onboarding_desc_4)
        ));

        onboardingPager.setAdapter(new OnboardingPagerAdapter(pages));

        TabLayout tabLayout = findViewById(R.id.tabIndicator);
        new TabLayoutMediator(tabLayout, onboardingPager, (tab, position) -> {
            // dots indicator only
        }).attach();

        onboardingPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateButtons(position == pages.size() - 1);
            }
        });
    }

    private void setupActions() {
        skipButton.setOnClickListener(v -> completeOnboarding());

        nextButton.setOnClickListener(v -> {
            if (onboardingPager.getAdapter() == null) {
                return;
            }
            int nextItem = onboardingPager.getCurrentItem() + 1;
            if (nextItem < onboardingPager.getAdapter().getItemCount()) {
                onboardingPager.setCurrentItem(nextItem, true);
            }
        });

        startButton.setOnClickListener(v -> completeOnboarding());
    }

    private void updateButtons(boolean isLastPage) {
        skipButton.setVisibility(isLastPage ? View.INVISIBLE : View.VISIBLE);
        nextButton.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
        startButton.setVisibility(isLastPage ? View.VISIBLE : View.GONE);
    }

    private void completeOnboarding() {
        SharedPreferences prefs = getSharedPreferences(SplashActivity.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(SplashActivity.KEY_ONBOARDING_DONE, true).apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}