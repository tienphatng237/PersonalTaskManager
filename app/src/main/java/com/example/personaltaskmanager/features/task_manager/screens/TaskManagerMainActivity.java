package com.example.personaltaskmanager.features.task_manager.screens;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Màn hình chính của Task Manager.
 * Giữ nguyên cấu trúc cũ, chỉ bỏ phần Bottom Navigation
 * vì layout hiện tại chưa có bottom_nav.
 */
public class TaskManagerMainActivity extends AppCompatActivity {

    private FloatingActionButton fabAddTask;
    private RecyclerView rvTasks;
    private TaskAdapter adapter;
    private TaskViewModel taskViewModel;

    private Spinner spinnerFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_main);

        setLightStatusBar();

        initViews();
        setupActions();
        setupRecyclerView();
        setupViewModelObserve();
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

    private void initViews() {
        fabAddTask = findViewById(R.id.fab_add_task);
        rvTasks = findViewById(R.id.rv_tasks);
        spinnerFilter = findViewById(R.id.spinner_filter);
    }

    private void setupActions() {
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_add_small).setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(
                task -> {
                    Intent intent = new Intent(TaskManagerMainActivity.this, TaskDetailActivity.class);
                    intent.putExtra("task_id", task.getId());
                    startActivity(intent);
                },
                this::showDeleteConfirmDialog,
                (task, done) -> taskViewModel.toggleCompleted(task, done)
        );

        rvTasks.setAdapter(adapter);
    }

    private void setupViewModelObserve() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        taskViewModel.getAllTasks().observe(this, tasks -> {
            adapter.setData(tasks);
        });
    }

    private void showDeleteConfirmDialog(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa công việc?")
                .setMessage("Bạn có chắc muốn xóa \"" + task.getTitle() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> taskViewModel.deleteTask(task))
                .setNegativeButton("Hủy", null)
                .show();
    }
}
