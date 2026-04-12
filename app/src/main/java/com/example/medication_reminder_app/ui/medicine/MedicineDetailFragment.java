package com.example.medication_reminder_app.ui.medicine;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medication_reminder_app.R;
import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;
import com.example.medication_reminder_app.databinding.FragmentMedicineDetailBinding;
import com.example.medication_reminder_app.ui.adapter.ScheduleAdapter;
import com.example.medication_reminder_app.viewmodel.MedicineViewModel;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;

public class MedicineDetailFragment extends Fragment {

    private FragmentMedicineDetailBinding binding;
    private MedicineViewModel viewModel;
    private Medicine currentMedicine;

    private Uri cameraImageUri;

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraImageUri != null && currentMedicine != null) {
                    String path = copyUriToInternalStorage(cameraImageUri);
                    if (path != null) {
                        viewModel.updateImagePath(currentMedicine.id, path);
                        binding.imgMedicineDetail.setImageURI(cameraImageUri);
                    }
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && currentMedicine != null) {
                    String path = copyUriToInternalStorage(uri);
                    if (path != null) {
                        viewModel.updateImagePath(currentMedicine.id, path);
                        binding.imgMedicineDetail.setImageURI(uri);
                    }
                }
            });

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) openCamera();
                else Toast.makeText(requireContext(), "Cần quyền camera", Toast.LENGTH_SHORT).show();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicineDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MedicineViewModel.class);

        int medicineId = getArguments() != null ? getArguments().getInt("medicine_id", -1) : -1;

        if (medicineId == -1) {
            Navigation.findNavController(view).navigateUp();
            return;
        }

        // Lấy thông tin từ LiveData danh sách (đơn giản, không cần thêm query)
        viewModel.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
            for (Medicine m : medicines) {
                if (m.id == medicineId) {
                    currentMedicine = m;
                    displayMedicine(m);
                    break;
                }
            }
        });

        // Danh sách lịch uống
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter();
        binding.recyclerSchedules.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerSchedules.setAdapter(scheduleAdapter);
        viewModel.getSchedulesForMedicine(medicineId).observe(getViewLifecycleOwner(),
                scheduleAdapter::submitList);

        // Nút đổi ảnh
        binding.btnChangeImage.setOnClickListener(v -> showImagePickerDialog());

        // Nút xóa thuốc
        binding.btnDelete.setOnClickListener(v -> confirmDelete());

        // Nút back
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
    }

    private void displayMedicine(Medicine m) {
        binding.tvDetailName.setText(m.name);
        binding.tvDetailDosage.setText("Liều dùng: " + m.dosage);
        binding.tvDetailTimes.setText("Số lần/ngày: " + m.timesPerDay);
        binding.tvDetailNotes.setText(m.notes != null && !m.notes.isEmpty()
                ? "Ghi chú: " + m.notes : "Không có ghi chú");

        if (m.imagePath != null && !m.imagePath.isEmpty()) {
            File f = new File(m.imagePath);
            if (f.exists()) binding.imgMedicineDetail.setImageURI(Uri.fromFile(f));
            else binding.imgMedicineDetail.setImageResource(R.drawable.ic_medicine_placeholder);
        } else {
            binding.imgMedicineDetail.setImageResource(R.drawable.ic_medicine_placeholder);
        }
    }

    private void showImagePickerDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cập nhật ảnh thuốc")
                .setItems(new String[]{"Chụp ảnh", "Chọn từ thư viện"}, (d, which) -> {
                    if (which == 0) checkCameraPermission();
                    else galleryLauncher.launch("image/*");
                }).show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, "med_" + System.currentTimeMillis() + ".jpg");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        cameraImageUri = requireContext().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
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

    private void confirmDelete() {
        if (currentMedicine == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa thuốc")
                .setMessage("Bạn có chắc muốn xóa \"" + currentMedicine.name + "\"?")
                .setPositiveButton("Xóa", (d, w) -> {
                    viewModel.deleteMedicine(currentMedicine);
                    Toast.makeText(requireContext(), "Đã xóa thuốc", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
