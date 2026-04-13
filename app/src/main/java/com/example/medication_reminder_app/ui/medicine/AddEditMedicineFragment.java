package com.example.medication_reminder_app.ui.medicine;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;
import com.example.medication_reminder_app.databinding.FragmentAddEditMedicineBinding;
import com.example.medication_reminder_app.utils.AlarmUtils;
import com.example.medication_reminder_app.viewmodel.MedicineViewModel;
import com.google.android.material.chip.Chip;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddEditMedicineFragment extends Fragment {

    private FragmentAddEditMedicineBinding binding;
    private MedicineViewModel viewModel;

    private String savedImagePath = null;
    private Uri cameraImageUri = null;
    private int medicineId = -1;
    private boolean isEditMode = false;

    private final List<int[]> timeSlots = new ArrayList<>();

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraImageUri != null) {
                    savedImagePath = copyUriToInternalStorage(cameraImageUri);
                    binding.imgPreview.setImageURI(cameraImageUri);
                    binding.imgPreview.setVisibility(View.VISIBLE);
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    savedImagePath = copyUriToInternalStorage(uri);
                    binding.imgPreview.setImageURI(uri);
                    binding.imgPreview.setVisibility(View.VISIBLE);
                }
            });

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) openCamera();
                else Toast.makeText(requireContext(),
                        "Cần quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditMedicineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MedicineViewModel.class);

        medicineId = getArguments() != null ? getArguments().getInt("medicine_id", -1) : -1;
        isEditMode = medicineId != -1;

        if (isEditMode) {
            binding.toolbar.setTitle("Sửa thuốc");
            binding.btnSave.setText("Cập nhật");

            viewModel.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
                for (Medicine m : medicines) {
                    if (m.id == medicineId) {
                        binding.etMedicineName.setText(m.name);
                        binding.etDosage.setText(m.dosage);
                        binding.etNotes.setText(m.notes);
                        savedImagePath = m.imagePath;
                        if (m.imagePath != null && !m.imagePath.isEmpty()) {
                            File f = new File(m.imagePath);
                            if (f.exists()) {
                                binding.imgPreview.setImageURI(Uri.fromFile(f));
                                binding.imgPreview.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    }
                }
            });

            viewModel.getSchedulesForMedicine(medicineId).observe(getViewLifecycleOwner(), schedules -> {
                if (!timeSlots.isEmpty()) return;
                timeSlots.clear();
                binding.chipGroupTimes.removeAllViews();
                for (Schedule s : schedules) {
                    int[] slot = new int[]{s.timeHour, s.timeMinute};
                    timeSlots.add(slot);
                    addTimeChip(s.timeHour, s.timeMinute, slot);
                }
            });
        } else {
            binding.toolbar.setTitle("Thêm thuốc mới");
            binding.btnSave.setText("Lưu thuốc");
        }

        binding.btnPickImage.setOnClickListener(v -> showImagePickerDialog());
        binding.btnAddTime.setOnClickListener(v -> showTimePicker());
        binding.btnSave.setOnClickListener(v -> saveMedicine());
        binding.btnCancel.setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());
    }

    private void showImagePickerDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn ảnh thuốc")
                .setItems(new String[]{"Chụp ảnh", "Chọn từ thư viện"}, (dialog, which) -> {
                    if (which == 0) checkCameraPermissionAndOpen();
                    else galleryLauncher.launch("image/*");
                }).show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME,
                "med_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        cameraImageUri = requireContext().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (cameraImageUri != null) cameraLauncher.launch(cameraImageUri);
    }

    private String copyUriToInternalStorage(Uri uri) {
        try {
            File dir = new File(requireContext().getFilesDir(), "medicine_images");
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(dir, "med_" + System.currentTimeMillis() + ".jpg");
            try (InputStream in = requireContext().getContentResolver().openInputStream(uri);
                 OutputStream out = Files.newOutputStream(dest.toPath())) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }
            return dest.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private void showTimePicker() {
        new TimePickerDialog(requireContext(), (tp, hour, minute) -> {
            int[] slot = new int[]{hour, minute};
            timeSlots.add(slot);
            addTimeChip(hour, minute, slot);
        }, 8, 0, true).show();
    }

    private void addTimeChip(int hour, int minute, int[] slot) {
        Chip chip = new Chip(requireContext());
        chip.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            binding.chipGroupTimes.removeView(chip);
            timeSlots.remove(slot);
        });
        binding.chipGroupTimes.addView(chip);
    }

    private boolean checkAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager =
                    (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(requireContext(),
                        "Vui lòng cấp quyền đặt lịch báo thức rồi thử lại",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void saveMedicine() {
        String name   = binding.etMedicineName.getText().toString().trim();
        String dosage = binding.etDosage.getText().toString().trim();
        String notes  = binding.etNotes.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etMedicineName.setError("Vui lòng nhập tên thuốc");
            return;
        }
        if (dosage.isEmpty()) {
            binding.etDosage.setError("Vui lòng nhập liều dùng");
            return;
        }
        if (timeSlots.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Vui lòng thêm ít nhất 1 giờ uống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkAlarmPermission()) return;

        List<Schedule> schedules = new ArrayList<>();
        for (int[] t : timeSlots) {
            schedules.add(new Schedule(0, t[0], t[1]));
        }

        binding.btnSave.setEnabled(false);

        if (isEditMode) {
            Medicine medicine = new Medicine(name, dosage, timeSlots.size(), notes);
            medicine.id = medicineId;
            if (savedImagePath != null) medicine.imagePath = savedImagePath;

            viewModel.updateMedicineWithSchedules(medicine, schedules, (id, savedSchedules) -> {
                if (getContext() != null) {
                    for (Schedule s : savedSchedules) {
                        AlarmUtils.setAlarm(requireContext(), s);
                    }
                    Toast.makeText(requireContext(),
                            "Đã cập nhật thuốc!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                }
            });
        } else {
            Medicine medicine = new Medicine(name, dosage, timeSlots.size(), notes);
            if (savedImagePath != null) medicine.imagePath = savedImagePath;

            viewModel.addMedicineWithSchedules(medicine, schedules, (id, savedSchedules) -> {
                if (getContext() != null) {
                    for (Schedule s : savedSchedules) {
                        AlarmUtils.setAlarm(requireContext(), s);
                    }
                    Toast.makeText(requireContext(),
                            "Đã thêm thuốc thành công!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}