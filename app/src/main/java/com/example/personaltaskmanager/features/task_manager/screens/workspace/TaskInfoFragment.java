package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.utils.DateUtils;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;

import java.util.Calendar;

public class TaskInfoFragment extends Fragment {

    private static final String ARG_TASK_ID = "task_id";

    private int taskId = -1;
    private Task currentTask = null;

    private EditText edtTitle, edtDesc, edtDate;
    private Button btnSave;

    private long selectedDeadline = System.currentTimeMillis();
    private TaskViewModel viewModel;

    public static TaskInfoFragment newInstance(int taskId) {
        TaskInfoFragment fragment = new TaskInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskId = getArguments().getInt(ARG_TASK_ID, -1);
        viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.feature_task_manager_fragment_task_info, container, false);

        edtTitle = v.findViewById(R.id.edt_task_title);
        edtDesc = v.findViewById(R.id.edt_task_description);
        edtDate = v.findViewById(R.id.edt_task_date);
        btnSave = v.findViewById(R.id.btn_save_task);

        // Nếu đang edit
        if (taskId != -1) {
            currentTask = viewModel.getTaskById(taskId);
            if (currentTask != null) {
                edtTitle.setText(currentTask.getTitle());
                edtDesc.setText(currentTask.getDescription());

                selectedDeadline = currentTask.getDeadline();
                edtDate.setText(DateUtils.formatDate(selectedDeadline));

                btnSave.setText("Cập nhật công việc");
            }
        }

        // Date picker
        edtDate.setOnClickListener(vx -> openDatePicker());

        // Save
        btnSave.setOnClickListener(vx -> saveTask());

        return v;
    }

    private void openDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDeadline);

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day, 0, 0, 0);
                    selectedDeadline = c.getTimeInMillis();
                    edtDate.setText(DateUtils.formatDate(selectedDeadline));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void saveTask() {
        String title = edtTitle.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError("Không được để trống");
            return;
        }

        if (currentTask != null) {
            viewModel.updateTask(currentTask, title, desc, selectedDeadline);
            return;
        }

        viewModel.addTask(title, desc, selectedDeadline);
    }
}
