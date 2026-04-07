package com.example.medication_reminder_app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medication_reminder_app.R;
import com.example.medication_reminder_app.data.entity.Schedule;

public class ScheduleAdapter extends ListAdapter<Schedule, ScheduleAdapter.ViewHolder> {

    public ScheduleAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_schedule_time);
        }

        void bind(Schedule schedule) {
            tvTime.setText("⏰ " + schedule.getTimeString());
        }
    }

    private static final DiffUtil.ItemCallback<Schedule> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Schedule>() {
                @Override
                public boolean areItemsTheSame(@NonNull Schedule a, @NonNull Schedule b) {
                    return a.id == b.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull Schedule a, @NonNull Schedule b) {
                    return a.timeHour == b.timeHour && a.timeMinute == b.timeMinute
                            && a.isActive == b.isActive;
                }
            };
}
