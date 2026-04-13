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

import com.example.medication_reminder_app.R;
import com.example.medication_reminder_app.data.entity.DateUtils;
import com.example.medication_reminder_app.data.entity.HistoryLog;
import com.example.medication_reminder_app.data.repository.HistoryRepository;

import java.util.Calendar;
import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryRepository historyRepository;
    private HistoryAdapter adapter;
    private TextView tvSelectedDate, tvTakenCount, tvMissedCount, tvCompliance;
    private String selectedDate;

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