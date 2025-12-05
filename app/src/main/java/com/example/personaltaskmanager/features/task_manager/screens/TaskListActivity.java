package com.example.personaltaskmanager.features.task_manager.screens;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.TaskWorkspaceActivity;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Màn hình danh sách Task.
 * Sử dụng ViewModel + LiveData → UI tự cập nhật khi DB thay đổi.
 *
 * ✔ Giữ nguyên toàn bộ cấu trúc cũ
 * ✔ Chỉ thay đổi: mở Workspace thay vì TaskDetailActivity
 */
public class TaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private TaskAdapter adapter;
    private TaskViewModel viewModel;

    private static final int REQUEST_ADD_TASK = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_list);

        setLightStatusBar();

        // Ánh xạ View
        recyclerView = findViewById(R.id.rv_list_tasks);
        fabAdd = findViewById(R.id.fab_add_task);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter hiển thị danh sách
        adapter = new TaskAdapter(
                task -> {
                    // ⭐⭐ CLICK → MỞ WORKSPACE ⭐⭐
                    Intent intent = new Intent(TaskListActivity.this, TaskWorkspaceActivity.class);
                    intent.putExtra("task_id", task.getId());
                    startActivity(intent);
                },
                task -> viewModel.deleteTask(task),
                (task, done) -> viewModel.toggleCompleted(task, done)
        );

        recyclerView.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Quan sát danh sách Task (LiveData)
        viewModel.getAllTasks().observe(this, tasks -> adapter.setData(tasks));

        // ADD task → vẫn mở màn TaskDetailActivity cũ
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            startActivityForResult(intent, REQUEST_ADD_TASK);
        });
    }

    /**
     * Thiết lập status bar sáng (icon tối)
     */
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
