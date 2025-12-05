package com.example.personaltaskmanager.features.task_manager.screens;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.utils.DateUtils;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;

import java.util.Calendar;

/**
 * Màn hình thêm / sửa Task.
 * Giữ nguyên code cũ, chỉ bổ sung deadline + DatePicker.
 */
public class TaskDetailActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription, edtDate;
    private Button btnSave;
    private ImageButton btnBack;

    private TaskViewModel viewModel;

    private int taskId = -1;
    private Task currentTask = null;

    // Deadline timestamp
    private long selectedDeadline = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_detail);

        setLightStatusBar();
        initViews();

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        loadTaskIfEditMode();
        setupListeners();
    }

    private void initViews() {
        edtTitle = findViewById(R.id.edt_task_title);
        edtDescription = findViewById(R.id.edt_task_description);
        edtDate = findViewById(R.id.edt_task_date);
        btnSave = findViewById(R.id.btn_save_task);
        btnBack = findViewById(R.id.btn_back);
    }

    private void loadTaskIfEditMode() {
        taskId = getIntent().getIntExtra("task_id", -1);

        if (taskId != -1) {
            currentTask = viewModel.getTaskById(taskId);

            if (currentTask != null) {
                edtTitle.setText(currentTask.getTitle());
                edtDescription.setText(currentTask.getDescription());

                selectedDeadline = currentTask.getDeadline();
                edtDate.setText(DateUtils.formatDate(selectedDeadline));

                btnSave.setText("Cập nhật công việc");
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        edtDate.setOnClickListener(v -> openDatePicker());

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void openDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDeadline);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                R.style.TaskManagerDatePickerTheme,
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
        String desc = edtDescription.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError("Tên công việc không được để trống");
            return;
        }

        // UPDATE
        if (currentTask != null) {
            viewModel.updateTask(currentTask, title, desc, selectedDeadline);
            setResult(RESULT_OK);
            finish();
            return;
        }

        // ADD
        viewModel.addTask(title, desc, selectedDeadline);
        setResult(RESULT_OK);
        finish();
    }

    private void setLightStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.WHITE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        } else {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
    }
}
