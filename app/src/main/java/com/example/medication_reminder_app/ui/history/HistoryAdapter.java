package com.example.medication_reminder_app.ui.history;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medication_reminder_app.R;
import com.example.medication_reminder_app.data.entity.DateUtils;
import com.example.medication_reminder_app.data.entity.HistoryLog;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryLog> logs = new ArrayList<>();

    public void setLogs(List<HistoryLog> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(logs.get(position));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvStatusIcon, tvMedicineName, tvScheduledTime, tvStatus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatusIcon    = itemView.findViewById(R.id.tvStatusIcon);
            tvMedicineName  = itemView.findViewById(R.id.tvMedicineName);
            tvScheduledTime = itemView.findViewById(R.id.tvScheduledTime);
            tvStatus        = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(HistoryLog log) {
            tvMedicineName.setText(log.medicineName);
            tvScheduledTime.setText(DateUtils.toDisplayDateTime(log.scheduledTime));

            switch (log.status) {
                case HistoryLog.STATUS_TAKEN:
                    tvStatusIcon.setText("✅");
                    tvStatus.setText("Đã uống");
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                    break;
                case HistoryLog.STATUS_MISSED:
                    tvStatusIcon.setText("❌");
                    tvStatus.setText("Bỏ lỡ");
                    tvStatus.setTextColor(Color.parseColor("#F44336"));
                    break;
                case HistoryLog.STATUS_SNOOZED:
                    tvStatusIcon.setText("⏰");
                    tvStatus.setText("Nhắc lại");
                    tvStatus.setTextColor(Color.parseColor("#FF9800"));
                    break;
            }
        }
    }
}