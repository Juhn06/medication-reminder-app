package com.example.medication_reminder_app.ui.onboarding;


import com.example.medication_reminder_app.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medication_reminder_app.ui.onboarding.OnboardingPage;

import java.util.List;

public class OnboardingPagerAdapter extends RecyclerView.Adapter<OnboardingPagerAdapter.PageViewHolder> {

    private final List<OnboardingPage> pages;

    public OnboardingPagerAdapter(List<OnboardingPage> pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        OnboardingPage page = pages.get(position);
        holder.icon.setImageResource(page.getIconRes());
        holder.title.setText(page.getTitle());
        holder.description.setText(page.getDescription());
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView description;

        PageViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imgOnboardingIcon);
            title = itemView.findViewById(R.id.txtOnboardingTitle);
            description = itemView.findViewById(R.id.txtOnboardingDescription);
        }
    }
}

