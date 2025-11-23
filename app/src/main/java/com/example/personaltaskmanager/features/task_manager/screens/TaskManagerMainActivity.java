package com.example.personaltaskmanager.features.task_manager.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personaltaskmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Màn hình chính của module Task Manager.
 * Gồm:
 *  - Thanh tìm kiếm (search bar)
 *  - Card tổng quan
 *  - Danh sách task (hiển thị ở màn hình List)
 *  - Bottom navigation (Home / Tasks / Profile)
 *  - FAB thêm task
 */
public class TaskManagerMainActivity extends AppCompatActivity {

    // -----------------------
    // VIEW trong layout
    // -----------------------
    private LinearLayout navHome, navTasks, navProfile;
    private FloatingActionButton fabAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_main);

        initViews();      // Ánh xạ view
        setupBottomNav(); // Xử lý bottom navigation
        setupActions();   // Xử lý các nút bấm
    }

    /**
     * ÁNH XẠ VIEW từ XML
     */
    private void initViews() {

        // Bottom nav container
        LinearLayout bottomNav = findViewById(R.id.bottom_nav);

        navHome = bottomNav.findViewById(R.id.nav_home);
        navTasks = bottomNav.findViewById(R.id.nav_tasks);
        navProfile = bottomNav.findViewById(R.id.nav_profile);

        fabAddTask = findViewById(R.id.fab_add_task);
    }

    /**
     * XỬ LÝ SỰ KIỆN CHO BOTTOM NAV
     */
    private void setupBottomNav() {

        // Home → (Đang để trống)
        navHome.setOnClickListener(v -> {
            // TODO: Sau này callback về MainActivity chính của App
        });

        // Tasks → mở danh sách Task
        navTasks.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskListActivity.class);
            startActivity(intent);
        });

        // Profile → (Đang để trống)
        navProfile.setOnClickListener(v -> {
            // TODO: Mở màn hình profile sau này
        });
    }

    /**
     * XỬ LÝ SỰ KIỆN CỦA CÁC NÚT (+) & SEARCH BAR
     */
    private void setupActions() {

        // Nút FAB — Thêm Task
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            startActivity(intent);
        });

        // Nút + nhỏ trong thanh Search
        findViewById(R.id.btn_add_small).setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            startActivity(intent);
        });
    }
}
