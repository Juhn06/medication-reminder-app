package com.example.medication_reminder_app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.example.medication_reminder_app.data.entity.Schedule;
import com.example.medication_reminder_app.MedicineReceiver;

import java.util.Calendar;

public class AlarmUtils {

    public static void setAlarm(Context context, Schedule schedule) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(context, MedicineReceiver.class);
        intent.setAction("ACTION_MEDICINE_REMINDER");
        intent.putExtra("SCHEDULE_ID", schedule.id);
        intent.putExtra("MEDICINE_ID", schedule.medicineId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                schedule.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, schedule.timeHour);
        calendar.set(Calendar.MINUTE, schedule.timeMinute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
        
        Log.d("AlarmUtils", "Alarm set for schedule " + schedule.id + " at " + calendar.getTime());
    }

    public static void cancelAlarm(Context context, int scheduleId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, MedicineReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                scheduleId,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d("AlarmUtils", "Alarm cancelled for schedule " + scheduleId);
        }
    }
}