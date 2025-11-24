package com.example.personaltaskmanager.features.task_manager.screens;

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
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;

/**
 * Màn hình thêm / sửa Task.
 * Sử dụng đúng kiến trúc MVVM → gọi ViewModel để lưu DB.
 */
public class TaskDetailActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private Button btnSave;
    private ImageButton btnBack;   // Bổ sung

    private TaskViewModel viewModel;

    // Dùng để biết người dùng đang EDIT hay ADD
    private int taskId = -1;
    private Task currentTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_detail);

        setLightStatusBar();

        // Ánh xạ view
        edtTitle = findViewById(R.id.edt_task_title);
        edtDescription = findViewById(R.id.edt_task_description);
        btnSave = findViewById(R.id.btn_save_task);
        btnBack = findViewById(R.id.btn_back);

        // KHỞI TẠO VIEWMODEL
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Nhận task_id nếu đang EDIT
        taskId = getIntent().getIntExtra("task_id", -1);

        if (taskId != -1) {
            currentTask = viewModel.getTaskById(taskId);
            if (currentTask != null) {
                edtTitle.setText(currentTask.getTitle());
                edtDescription.setText(currentTask.getDescription());
                btnSave.setText("Cập nhật công việc");
            }
        }

        // Xử lý nút BACK
        btnBack.setOnClickListener(v -> finish());

        // Nút LƯU
        btnSave.setOnClickListener(v -> {

            String title = edtTitle.getText().toString().trim();
            String desc = edtDescription.getText().toString().trim();

            if (title.isEmpty()) {
                edtTitle.setError("Tên công việc không được để trống");
                return;
            }

            // EDIT
            if (currentTask != null) {
                viewModel.updateTask(currentTask, title, desc);
                setResult(RESULT_OK);
                finish();
                return;
            }

            // ADD
            viewModel.addTask(title, desc);
            setResult(RESULT_OK);
            finish();
        });
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
