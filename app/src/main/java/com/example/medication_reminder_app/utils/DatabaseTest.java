package com.example.medication_reminder_app.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.medication_reminder_app.data.database.AppDatabase;
import com.example.medication_reminder_app.data.entity.DateUtils;
import com.example.medication_reminder_app.data.entity.HistoryLog;
import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseTest {

    private static final String TAG = "DB_TEST";

    public static void runAll(Context context, Runnable onComplete) {
        ExecutorService exec = Executors.newSingleThreadExecutor();

        exec.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);

            // Xóa data cũ
            db.medicineDao().deleteAll();
            Log.d(TAG, "=== BẮT ĐẦU TEST DATABASE ===");

            // TEST 1: Insert Medicine
            Medicine paracetamol = new Medicine("Paracetamol", "500mg - 1 viên", 3, "Uống sau ăn");
            long id1 = db.medicineDao().insert(paracetamol);
            Medicine vitaminC = new Medicine("Vitamin C", "1000mg - 1 viên", 1, "Uống buổi sáng");
            long id2 = db.medicineDao().insert(vitaminC);
            Log.d(TAG, "✅ TEST 1 PASS: Insert Medicine — id1=" + id1 + ", id2=" + id2);

            // TEST 2: Get by id
            Medicine fetched = db.medicineDao().getByIdSync((int) id1);
            if (fetched == null || !fetched.name.equals("Paracetamol")) {
                Log.e(TAG, "❌ TEST 2 FAIL");
                return;
            }
            Log.d(TAG, "✅ TEST 2 PASS: getByIdSync — name=" + fetched.name);

            // TEST 3: Insert Schedules
            Schedule s1 = new Schedule((int) id1, 8, 0);
            Schedule s2 = new Schedule((int) id1, 12, 30);
            Schedule s3 = new Schedule((int) id1, 20, 0);
            db.scheduleDao().insertAll(Arrays.asList(s1, s2, s3));
            List<Schedule> schedules = db.scheduleDao().getByMedicineIdSync((int) id1);
            if (schedules.size() != 3) {
                Log.e(TAG, "❌ TEST 3 FAIL: size=" + schedules.size());
                return;
            }
            Log.d(TAG, "✅ TEST 3 PASS: Schedules — count=" + schedules.size());
            for (Schedule s : schedules) {
                Log.d(TAG, "   → " + s.getTimeString());
            }

            // TEST 4: Insert HistoryLog
            Schedule first = schedules.get(0);
            HistoryLog log = new HistoryLog(
                    first.id, (int) id1, "Paracetamol",
                    "500mg", System.currentTimeMillis(), HistoryLog.STATUS_TAKEN
            );
            long logId = db.historyDao().insert(log);
            if (logId <= 0) {
                Log.e(TAG, "❌ TEST 4 FAIL");
                return;
            }
            Log.d(TAG, "✅ TEST 4 PASS: Insert HistoryLog — logId=" + logId);

            // TEST 5: Compliance
            String today = DateUtils.today();
            int taken = db.historyDao().getTakenCountSync(today, today);
            int total = db.historyDao().getTotalCountSync(today, today);
            Log.d(TAG, "✅ TEST 5 PASS: Compliance — taken=" + taken + "/" + total);

            // TEST 6: Update
            fetched.notes = "Đã cập nhật";
            db.medicineDao().update(fetched);
            Medicine updated = db.medicineDao().getByIdSync((int) id1);
            if (!updated.notes.equals("Đã cập nhật")) {
                Log.e(TAG, "❌ TEST 6 FAIL");
                return;
            }
            Log.d(TAG, "✅ TEST 6 PASS: Update — notes=" + updated.notes);

            // TEST 7: Soft delete
            db.medicineDao().setActive((int) id1, false);
            List<Medicine> activeList = db.medicineDao().getAllActiveSync();
            boolean stillActive = false;
            for (Medicine m : activeList) {
                if (m.id == id1) { stillActive = true; break; }
            }
            if (stillActive) {
                Log.e(TAG, "❌ TEST 7 FAIL");
                return;
            }
            Log.d(TAG, "✅ TEST 7 PASS: Soft delete — còn " + activeList.size() + " thuốc active");

            Log.d(TAG, "=== TẤT CẢ TEST PASSED ✅ ===");

            if (onComplete != null) {
                new Handler(Looper.getMainLooper()).post(onComplete);
            }
        });
    }
}