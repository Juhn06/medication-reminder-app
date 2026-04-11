package com.example.myapplicationmedtech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineScheduleAdapter extends RecyclerView.Adapter<MedicineScheduleAdapter.MedicineViewHolder> {

    private final List<MedicineScheduleItem> items;

    public MedicineScheduleAdapter(List<MedicineScheduleItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine_schedule, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        MedicineScheduleItem item = items.get(position);

        holder.txtMedicineName.setText(item.getMedicineName());
        holder.txtMedicineTime.setText(item.getTime());
        holder.txtMedicineStatus.setText(item.getStatus());

        int statusColor = ContextCompat.getColor(context, R.color.medical_text_secondary);
        if (item.getStatus().equals(context.getString(R.string.status_done))) {
            statusColor = ContextCompat.getColor(context, R.color.medical_success);
        } else if (item.getStatus().equals(context.getString(R.string.status_missed))) {
            statusColor = ContextCompat.getColor(context, R.color.medical_warning);
        }
        holder.txtMedicineStatus.setTextColor(statusColor);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        final TextView txtMedicineName;
        final TextView txtMedicineTime;
        final TextView txtMedicineStatus;

        MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMedicineName = itemView.findViewById(R.id.txtMedicineName);
            txtMedicineTime = itemView.findViewById(R.id.txtMedicineTime);
            txtMedicineStatus = itemView.findViewById(R.id.txtMedicineStatus);
        }
    }
}

