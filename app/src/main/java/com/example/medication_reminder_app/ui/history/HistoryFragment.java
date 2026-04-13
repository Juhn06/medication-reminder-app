package com.example.medication_reminder_app.ui.history;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medication_reminder_app.MedicineReceiver;
import com.example.medication_reminder_app.R;
import com.example.medication_reminder_app.data.database.AppDatabase;
import com.example.medication_reminder_app.data.entity.DateUtils;
import com.example.medication_reminder_app.data.entity.HistoryLog;
import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.repository.HistoryRepository;
import com.example.medication_reminder_app.utils.AlarmUtils;
import com.example.medication_reminder_app.data.entity.Schedule;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {

    private HistoryRepository historyRepository;
    private HistoryAdapter adapter;
    private TextView tvSelectedDate, tvTakenCount, tvMissedCount, tvCompliance;
    private String selectedDate;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvTakenCount   = view.findViewById(R.id.tvTakenCount);
        tvMissedCount  = view.findViewById(R.id.tvMissedCount);
        tvCompliance   = view.findViewById(R.id.tvCompliance);
        Button btnPickDate = view.findViewById(R.id.btnPickDate);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);

        historyRepository = new HistoryRepository(requireActivity().getApplication());

        // Mặc định hiển thị hôm nay
        selectedDate = DateUtils.today();
        tvSelectedDate.setText("Hôm nay");
        loadHistory(selectedDate);

        // Nút chọn ngày
        btnPickDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (datePicker, year, month, day) -> {
                        selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                        tvSelectedDate.setText(
                                String.format("%02d/%02d/%04d", day, month + 1, year));
                        loadHistory(selectedDate);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Kiểm tra có mở từ thông báo không → hiện dialog xác nhận
        checkNotificationIntent();
    }

    private void checkNotificationIntent() {
        if (getActivity() == null) return;

        android.content.Intent intent = getActivity().getIntent();
        if (intent == null) return;

        String openTab = intent.getStringExtra("OPEN_TAB");
        int scheduleId = intent.getIntExtra("SCHEDULE_ID", -1);
        int medicineId = intent.getIntExtra("MEDICINE_ID", -1);

        if ("history".equals(openTab) && scheduleId != -1 && medicineId != -1) {
            // Xóa intent để tránh hiện dialog lại khi quay lại tab
            intent.removeExtra("OPEN_TAB");
            intent.removeExtra("SCHEDULE_ID");
            intent.removeExtra("MEDICINE_ID");

            // Lấy tên thuốc rồi hiện dialog
            executor.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                Medicine medicine = db.medicineDao().getByIdSync(medicineId);
                if (medicine == null) return;

                requireActivity().runOnUiThread(() ->
                        showConfirmDialog(scheduleId, medicineId, medicine.name, medicine.dosage));
            });
        }
    }

    private void showConfirmDialog(int scheduleId, int medicineId, String name, String dosage) {
        if (getContext() == null) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận uống thuốc")
                .setMessage("Bạn đã uống " + name + " (" + dosage + ") chưa?")
                .setCancelable(false) // bắt buộc phải chọn 1 trong 2
                .setPositiveButton("✅ Đã uống", (dialog, which) -> {
                    saveTaken(scheduleId, medicineId, name, dosage);
                })
                .setNegativeButton("🔔 Nhắc lại sau 10 phút", (dialog, which) -> {
                    saveSnooze(scheduleId, medicineId, name, dosage);
                })
                .show();
    }

    private void saveTaken(int scheduleId, int medicineId, String name, String dosage) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            HistoryLog log = new HistoryLog(
                    scheduleId, medicineId, name, dosage,
                    System.currentTimeMillis(), HistoryLog.STATUS_TAKEN
            );
            db.historyDao().insert(log);

            // Reload lịch sử sau khi lưu
            requireActivity().runOnUiThread(() -> loadHistory(selectedDate));
        });
    }

    private void saveSnooze(int scheduleId, int medicineId, String name, String dosage) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());

            // Lưu trạng thái SNOOZED
            HistoryLog log = new HistoryLog(
                    scheduleId, medicineId, name, dosage,
                    System.currentTimeMillis(), HistoryLog.STATUS_SNOOZED
            );
            db.historyDao().insert(log);

            // Đặt lại alarm sau 10 phút
            long snoozeTime = System.currentTimeMillis() + 10 * 60 * 1000;
            android.app.AlarmManager alarmManager = (android.app.AlarmManager)
                    requireContext().getSystemService(android.content.Context.ALARM_SERVICE);
            android.content.Intent snoozeIntent = new android.content.Intent(
                    requireContext(), MedicineReceiver.class);
            snoozeIntent.putExtra("SCHEDULE_ID", scheduleId);
            snoozeIntent.putExtra("MEDICINE_ID", medicineId);

            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                    requireContext(), scheduleId + 5000, snoozeIntent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT |
                            android.app.PendingIntent.FLAG_IMMUTABLE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            android.app.AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
                } else {
                    alarmManager.setAndAllowWhileIdle(
                            android.app.AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                        android.app.AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
            }

            requireActivity().runOnUiThread(() -> loadHistory(selectedDate));
        });
    }

    private void loadHistory(String date) {
        historyRepository.getByDate(date).observe(getViewLifecycleOwner(), logs -> {
            adapter.setLogs(logs);
            updateStats(logs);
        });
    }

    private void updateStats(List<HistoryLog> logs) {
        int taken = 0, missed = 0;
        for (HistoryLog log : logs) {
            if (log.isTaken()) taken++;
            else if (log.isMissed()) missed++;
        }
        tvTakenCount.setText(String.valueOf(taken));
        tvMissedCount.setText(String.valueOf(missed));

        int total = logs.size();
        if (total > 0) {
            int percent = (taken * 100) / total;
            tvCompliance.setText(percent + "% tuân thủ");
        } else {
            tvCompliance.setText("Chưa có dữ liệu");
        }
    }
}