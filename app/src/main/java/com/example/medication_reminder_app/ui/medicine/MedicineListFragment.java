package com.example.medication_reminder_app.ui.medicine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medication_reminder_app.R;
import com.example.medication_reminder_app.databinding.FragmentMedicineListBinding;
import com.example.medication_reminder_app.ui.adapter.MedicineAdapter;
import com.example.medication_reminder_app.viewmodel.MedicineViewModel;

public class MedicineListFragment extends Fragment {

    private FragmentMedicineListBinding binding;
    private MedicineViewModel viewModel;
    private MedicineAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicineListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MedicineViewModel.class);

        // Setup RecyclerView
        adapter = new MedicineAdapter();
        binding.recyclerMedicines.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMedicines.setAdapter(adapter);

        // Observe danh sách thuốc
        viewModel.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
            adapter.submitList(medicines);
            binding.tvEmpty.setVisibility(medicines.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerMedicines.setVisibility(medicines.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // Click vào item -> sang màn detail
        adapter.setOnItemClickListener(medicine -> {
            Bundle args = new Bundle();
            args.putInt("medicine_id", medicine.id);
            args.putString("medicine_name", medicine.name);
            Navigation.findNavController(view)
                    .navigate(R.id.action_list_to_detail, args);
        });

        // FAB -> sang màn thêm thuốc
        binding.fabAddMedicine.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_list_to_add)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
