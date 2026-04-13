package com.example.medication_reminder_app.ui.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medication_reminder_app.R;

public class SensorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor, accelSensor, tempSensor, pressureSensor, humiditySensor;

    private TextView tvTemp, tvHumidity, tvPressure, tvLight, tvAccel, tvWarning, tvSteps;
    private TextView stepTextView;
    private ProgressBar progressBar;

    private int stepCount = 0;

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

        // Ánh xạ view
        tvTemp       = view.findViewById(R.id.tvTemp);
        tvHumidity   = view.findViewById(R.id.tvHumidity);
        tvPressure   = view.findViewById(R.id.tvPressure);
        tvLight      = view.findViewById(R.id.tvLight);
        tvAccel      = view.findViewById(R.id.tvAccel);
        tvWarning    = view.findViewById(R.id.tvWarning);
        tvSteps      = view.findViewById(R.id.tvSteps);
        stepTextView = view.findViewById(R.id.stepTextView);
        progressBar  = view.findViewById(R.id.progressBar);

        sensorManager = (SensorManager) requireActivity()
                .getSystemService(Context.SENSOR_SERVICE);

        // Lấy cảm biến
        lightSensor    = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelSensor    = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        tempSensor     = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        // Trạng thái ban đầu
        tvTemp.setText(tempSensor         != null ? "🌡 Nhiệt độ: Đang đo..."  : "🌡 Nhiệt độ: Không hỗ trợ");
        tvHumidity.setText(humiditySensor != null ? "💧 Độ ẩm: Đang đo..."     : "💧 Độ ẩm: Không hỗ trợ");
        tvPressure.setText(pressureSensor != null ? "🔵 Áp suất: Đang đo..."   : "🔵 Áp suất: Không hỗ trợ");
        tvLight.setText(lightSensor       != null ? "☀ Ánh sáng: Đang đo..."  : "☀ Ánh sáng: Không hỗ trợ");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lightSensor != null)
            sensorManager.registerListener(this, lightSensor,    SensorManager.SENSOR_DELAY_NORMAL);
        if (accelSensor != null)
            sensorManager.registerListener(this, accelSensor,    SensorManager.SENSOR_DELAY_GAME);
        if (tempSensor != null)
            sensorManager.registerListener(this, tempSensor,     SensorManager.SENSOR_DELAY_NORMAL);
        if (pressureSensor != null)
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (humiditySensor != null)
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {

            case Sensor.TYPE_LIGHT:
                tvLight.setText(String.format("☀ Ánh sáng: %.0f lux", event.values[0]));
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                tvTemp.setText(String.format("🌡 Nhiệt độ: %.1f °C", event.values[0]));
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                tvHumidity.setText(String.format("💧 Độ ẩm: %.1f %%", event.values[0]));
                break;

            case Sensor.TYPE_PRESSURE:
                tvPressure.setText(String.format("🔵 Áp suất: %.1f hPa", event.values[0]));
                break;

            case Sensor.TYPE_ACCELEROMETER:
                float x = event.values[0], y = event.values[1], z = event.values[2];
                float force = (float) Math.sqrt(x * x + y * y + z * z);
                tvAccel.setText(String.format("📐 Gia tốc: X=%.1f Y=%.1f Z=%.1f", x, y, z));

                if (force > 12f && force < 20f) {
                    stepCount++;
                    stepTextView.setText(String.valueOf(stepCount));
                    progressBar.setProgress(stepCount);
                    tvSteps.setText("👟 " + stepCount + " bước chân");

                    if (stepCount < 1000)
                        tvWarning.setText("🚶 Hãy đi bộ nhẹ 15 phút!");
                    else if (stepCount < 5000)
                        tvWarning.setText("💪 Tốt! Tiếp tục vận động!");
                    else
                        tvWarning.setText("🏆 Xuất sắc! Rất năng động!");
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}