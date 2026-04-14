package com.example.medication_reminder_app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.medication_reminder_app.R;
import com.example.medication_reminder_app.data.entity.Medicine;

import java.io.File;

public class MedicineAdapter extends ListAdapter<Medicine, MedicineAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Medicine medicine);
    }

    private OnItemClickListener listener;

    public MedicineAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine medicine = getItem(position);
        holder.bind(medicine, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgMedicine;
        private final TextView tvName;
        private final TextView tvDosage;
        private final TextView tvTimes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMedicine = itemView.findViewById(R.id.img_medicine);
            tvName      = itemView.findViewById(R.id.tv_medicine_name);
            tvDosage    = itemView.findViewById(R.id.tv_medicine_dosage);
            tvTimes     = itemView.findViewById(R.id.tv_medicine_times);
        }

        void bind(Medicine medicine, OnItemClickListener listener) {
            tvName.setText(medicine.name);
            tvDosage.setText(medicine.dosage);
            tvTimes.setText(medicine.timesPerDay + " lần/ngày");

            // Load ảnh bằng Glide → không lag
            if (medicine.imagePath != null && !medicine.imagePath.isEmpty()) {
                File imgFile = new File(medicine.imagePath);
                if (imgFile.exists()) {
                    Glide.with(itemView.getContext())
                            .load(imgFile)
                            .placeholder(R.drawable.ic_medicine_placeholder)
                            .error(R.drawable.ic_medicine_placeholder)
                            .centerCrop()
                            .into(imgMedicine);
                } else {
                    Glide.with(itemView.getContext())
                            .load(R.drawable.ic_medicine_placeholder)
                            .into(imgMedicine);
                }
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_medicine_placeholder)
                        .into(imgMedicine);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(medicine);
            });
        }
    }

    private static final DiffUtil.ItemCallback<Medicine> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Medicine>() {
                @Override
                public boolean areItemsTheSame(@NonNull Medicine a, @NonNull Medicine b) {
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Medicine a, @NonNull Medicine b) {
                    return a.name.equals(b.name)
                            && a.dosage.equals(b.dosage)
                            && a.timesPerDay == b.timesPerDay
                            && a.isActive == b.isActive
                            && (a.imagePath == null ? b.imagePath == null
                            : a.imagePath.equals(b.imagePath));
                }
            };
}