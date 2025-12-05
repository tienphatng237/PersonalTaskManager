package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TaskWorkspaceActivity extends AppCompatActivity {

    private TaskViewModel viewModel;
    private int taskId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_workspace);

        // Lấy ID task được truyền sang
        taskId = getIntent().getIntExtra("task_id", -1);

        // ViewModel dùng chung cho cả 3 Fragment
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        setupViewPager();
    }

    private void setupViewPager() {
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        TaskWorkspaceAdapter adapter = new TaskWorkspaceAdapter(this, taskId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Thông tin");
                    break;
                case 1:
                    tab.setText("Ghi chú");
                    break;
                case 2:
                    tab.setText("Bảng");
                    break;
            }
        }).attach();
    }
}
