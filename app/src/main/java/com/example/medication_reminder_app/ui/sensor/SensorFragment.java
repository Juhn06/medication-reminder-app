package com.example.medication_reminder_app.ui.sensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.medication_reminder_app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SensorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor, tempSensor, pressureSensor, humiditySensor, stepSensor;

    private TextView tvTemp, tvHumidity, tvPressure, tvLight;
    private TextView tvSteps, stepTextView, tvStepAdvice;
    private TextView tvWeatherAdvice;
    private ProgressBar progressBar;

    private int initialStepCount = -1;
    private float lastTemperature = Float.MIN_VALUE;
    private float lastHumidity = Float.MIN_VALUE;

    private static final String PREFS_NAME = "step_prefs";
    private static final String KEY_STEP_DATE = "step_date";
    private static final String KEY_STEP_INITIAL = "step_initial";
    private static final String KEY_STEP_TODAY = "step_today";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTemp          = view.findViewById(R.id.tvTemp);
        tvHumidity      = view.findViewById(R.id.tvHumidity);
        tvPressure      = view.findViewById(R.id.tvPressure);
        tvLight         = view.findViewById(R.id.tvLight);
        tvSteps         = view.findViewById(R.id.tvSteps);
        stepTextView    = view.findViewById(R.id.stepTextView);
        tvStepAdvice    = view.findViewById(R.id.tvStepAdvice);
        tvWeatherAdvice = view.findViewById(R.id.tvWeatherAdvice);
        progressBar     = view.findViewById(R.id.progressBar);

        sensorManager  = (SensorManager) requireActivity()
                .getSystemService(Context.SENSOR_SERVICE);

        lightSensor    = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        tempSensor     = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        stepSensor     = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Trạng thái ban đầu
        tvTemp.setText(tempSensor         != null ? "🌡 Nhiệt độ: Đang đo..."  : "🌡 Nhiệt độ: Không hỗ trợ");
        tvHumidity.setText(humiditySensor != null ? "💧 Độ ẩm: Đang đo..."     : "💧 Độ ẩm: Không hỗ trợ");
        tvPressure.setText(pressureSensor != null ? "🔵 Áp suất: Đang đo..."   : "🔵 Áp suất: Không hỗ trợ");
        tvLight.setText(lightSensor       != null ? "☀ Ánh sáng: Đang đo..."  : "☀ Ánh sáng: Không hỗ trợ");

        if (stepSensor != null) {
            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            int savedSteps = prefs.getInt(KEY_STEP_TODAY, 0);
            stepTextView.setText(String.valueOf(savedSteps));
            progressBar.setProgress(savedSteps);
            tvSteps.setText("👟 " + savedSteps + " bước chân hôm nay");
            updateStepAdvice(savedSteps);
        } else {
            tvSteps.setText("👟 Không hỗ trợ đếm bước chân");
            stepTextView.setText("--");
            tvStepAdvice.setText("");
        }

        // Xin quyền ACTIVITY_RECOGNITION trên Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACTIVITY_RECOGNITION},
                        200);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lightSensor != null)
            sensorManager.registerListener(this, lightSensor,    SensorManager.SENSOR_DELAY_NORMAL);
        if (tempSensor != null)
            sensorManager.registerListener(this, tempSensor,     SensorManager.SENSOR_DELAY_NORMAL);
        if (pressureSensor != null)
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (humiditySensor != null)
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (stepSensor != null)
            sensorManager.registerListener(this, stepSensor,     SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (stepSensor != null) {
                    sensorManager.registerListener(this, stepSensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                }
            } else {
                tvSteps.setText("👟 Cần cấp quyền để đếm bước chân");
                stepTextView.setText("--");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {

            case Sensor.TYPE_LIGHT:
                tvLight.setText(String.format("☀ Ánh sáng: %.0f lux", event.values[0]));
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                lastTemperature = event.values[0];
                tvTemp.setText(String.format("🌡 Nhiệt độ: %.1f °C", lastTemperature));
                updateWeatherAdvice();
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                lastHumidity = event.values[0];
                tvHumidity.setText(String.format("💧 Độ ẩm: %.1f %%", lastHumidity));
                updateWeatherAdvice();
                break;

            case Sensor.TYPE_PRESSURE:
                tvPressure.setText(String.format("🔵 Áp suất: %.1f hPa", event.values[0]));
                break;

            case Sensor.TYPE_STEP_COUNTER:
                handleStepCount((int) event.values[0]);
                break;
        }
    }

    private void updateWeatherAdvice() {
        String advice = "";
        String color = "#2E7D32";

        if (lastTemperature != Float.MIN_VALUE) {
            if (lastTemperature > 40) {
                advice = "🥵 Rất nóng! Hạn chế ra ngoài, uống nhiều nước!";
                color = "#C62828";
            } else if (lastTemperature > 35) {
                advice = "🌂 Trời nóng! Đội mũ che nắng, uống nhiều nước!";
                color = "#E65100";
            } else if (lastTemperature < 10) {
                advice = "🧥 Trời lạnh! Hãy mặc áo ấm khi ra ngoài!";
                color = "#1565C0";
            } else if (lastTemperature < 20) {
                advice = "👕 Trời mát! Mặc áo nhẹ là đủ!";
                color = "#0277BD";
            } else {
                advice = "✅ Thời tiết dễ chịu!";
                color = "#2E7D32";
            }
        }

        if (lastHumidity != Float.MIN_VALUE) {
            if (lastHumidity > 80) {
                advice += "\n💦 Độ ẩm cao! Dễ mệt mỏi, nghỉ ngơi hợp lý!";
            } else if (lastHumidity < 30) {
                advice += "\n🏜 Không khí khô! Uống nhiều nước!";
            }
        }

        if (advice.isEmpty()) {
            advice = "Đang chờ dữ liệu cảm biến...";
        }

        final String finalAdvice = advice;
        final String finalColor = color;
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvWeatherAdvice.setText(finalAdvice);
                tvWeatherAdvice.setTextColor(android.graphics.Color.parseColor(finalColor));
            });
        }
    }

    private void updateStepAdvice(int stepCount) {
        if (stepCount < 1000) {
            tvStepAdvice.setText("🚶 Hãy đi bộ nhẹ 15 phút!");
        } else if (stepCount < 5000) {
            tvStepAdvice.setText("💪 Tốt! Tiếp tục vận động!");
        } else if (stepCount < 10000) {
            tvStepAdvice.setText("🏃 Gần đạt mục tiêu rồi!");
        } else {
            tvStepAdvice.setText("🏆 Xuất sắc! Đã đạt mục tiêu!");
        }
    }

    private void handleStepCount(int totalSteps) {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        String savedDate = prefs.getString(KEY_STEP_DATE, "");

        if (!today.equals(savedDate)) {
            // Sang ngày mới → reset
            prefs.edit()
                    .putString(KEY_STEP_DATE, today)
                    .putInt(KEY_STEP_INITIAL, totalSteps)
                    .putInt(KEY_STEP_TODAY, 0)
                    .apply();
            initialStepCount = totalSteps;
        } else {
            if (initialStepCount == -1) {
                initialStepCount = prefs.getInt(KEY_STEP_INITIAL, totalSteps);
            }
        }

        int stepCount = totalSteps - initialStepCount;
        prefs.edit().putInt(KEY_STEP_TODAY, stepCount).apply();

        stepTextView.setText(String.valueOf(stepCount));
        progressBar.setProgress(stepCount);
        tvSteps.setText("👟 " + stepCount + " bước chân hôm nay");
        updateStepAdvice(stepCount);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}