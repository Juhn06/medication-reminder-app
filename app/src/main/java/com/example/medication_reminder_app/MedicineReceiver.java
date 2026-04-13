package com.example.medication_reminder_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.medication_reminder_app.data.database.AppDatabase;
import com.example.medication_reminder_app.data.entity.HistoryLog;
import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;
import com.example.medication_reminder_app.utils.AlarmUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicineReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "MEDICINE_REMINDER_CHANNEL";
    private static final String ACTION_TAKEN = "ACTION_TAKEN";
    private static final String ACTION_SNOOZE = "ACTION_SNOOZE";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int scheduleId = intent.getIntExtra("SCHEDULE_ID", -1);
        int medicineId = intent.getIntExtra("MEDICINE_ID", -1);

        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            rescheduleAllAlarms(context);
            return;
        }

        if (ACTION_TAKEN.equals(action)) {
            handleTakenAction(context, scheduleId, medicineId);
            return;
        }

        if (ACTION_SNOOZE.equals(action)) {
            handleSnoozeAction(context, scheduleId, medicineId);
            return;
        }

        showNotification(context, scheduleId, medicineId);
    }

    private void showNotification(Context context, int scheduleId, int medicineId) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Medicine medicine = db.medicineDao().getByIdSync(medicineId);
            Schedule schedule = db.scheduleDao().getByIdSync(scheduleId);

            if (medicine == null || schedule == null) return;

            createNotificationChannel(context);

            // Bấm vào thông báo → mở app vào tab Lịch sử + truyền thông tin thuốc
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.putExtra("OPEN_TAB", "history");
            mainIntent.putExtra("SCHEDULE_ID", scheduleId);
            mainIntent.putExtra("MEDICINE_ID", medicineId);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent mainPendingIntent = PendingIntent.getActivity(
                    context, scheduleId, mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Nút "Đã uống" → chỉ cập nhật DB, không mở app
            Intent takenIntent = new Intent(context, MedicineReceiver.class);
            takenIntent.setAction(ACTION_TAKEN);
            takenIntent.putExtra("SCHEDULE_ID", scheduleId);
            takenIntent.putExtra("MEDICINE_ID", medicineId);
            PendingIntent takenPendingIntent = PendingIntent.getBroadcast(
                    context, scheduleId + 1000, takenIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Nút "Nhắc lại" → chỉ snooze, không mở app
            Intent snoozeIntent = new Intent(context, MedicineReceiver.class);
            snoozeIntent.setAction(ACTION_SNOOZE);
            snoozeIntent.putExtra("SCHEDULE_ID", scheduleId);
            snoozeIntent.putExtra("MEDICINE_ID", medicineId);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                    context, scheduleId + 2000, snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentTitle("Đến giờ uống thuốc!")
                    .setContentText("Bạn cần uống: " + medicine.name + " (" + medicine.dosage + ")")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setContentIntent(mainPendingIntent)
                    .addAction(android.R.drawable.ic_menu_save, "Đã uống", takenPendingIntent)
                    .addAction(android.R.drawable.ic_menu_recent_history, "Nhắc lại", snoozePendingIntent);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(scheduleId, builder.build());

            AlarmUtils.setAlarm(context, schedule);
        });
    }

    private void handleTakenAction(Context context, int scheduleId, int medicineId) {
        // Xóa thông báo, KHÔNG mở app
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(scheduleId);

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Medicine medicine = db.medicineDao().getByIdSync(medicineId);
            if (medicine != null) {
                HistoryLog log = new HistoryLog(
                        scheduleId, medicineId, medicine.name, medicine.dosage,
                        System.currentTimeMillis(), HistoryLog.STATUS_TAKEN
                );
                db.historyDao().insert(log);
                Log.d("MedicineReceiver", "Marked as TAKEN for medicine " + medicineId);
            }
        });
    }

    private void handleSnoozeAction(Context context, int scheduleId, int medicineId) {
        // Xóa thông báo, KHÔNG mở app
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(scheduleId);

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Schedule schedule = db.scheduleDao().getByIdSync(scheduleId);
            if (schedule != null) {
                long snoozeTime = System.currentTimeMillis() + 10 * 60 * 1000;

                android.app.AlarmManager alarmManager = (android.app.AlarmManager)
                        context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, MedicineReceiver.class);
                intent.putExtra("SCHEDULE_ID", scheduleId);
                intent.putExtra("MEDICINE_ID", medicineId);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context, scheduleId + 5000, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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

                Medicine medicine = db.medicineDao().getByIdSync(medicineId);
                if (medicine != null) {
                    HistoryLog log = new HistoryLog(
                            scheduleId, medicineId, medicine.name, medicine.dosage,
                            System.currentTimeMillis(), HistoryLog.STATUS_SNOOZED
                    );
                    db.historyDao().insert(log);
                }
                Log.d("MedicineReceiver", "Snoozed 10 minutes for schedule " + scheduleId);
            }
        });
    }

    private void rescheduleAllAlarms(Context context) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            List<Schedule> activeSchedules = db.scheduleDao().getAllActiveSync();
            for (Schedule schedule : activeSchedules) {
                AlarmUtils.setAlarm(context, schedule);
            }
            Log.d("MedicineReceiver", "Rescheduled " + activeSchedules.size() + " alarms");
        });
    }

    private void createNotificationChannel(Context context) {
        CharSequence name = "Nhắc nhở uống thuốc";
        String description = "Thông báo khi đến giờ uống thuốc";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}