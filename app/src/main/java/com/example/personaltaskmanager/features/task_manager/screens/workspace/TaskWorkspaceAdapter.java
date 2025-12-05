package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TaskWorkspaceAdapter extends FragmentStateAdapter {

    private final int taskId;

    public TaskWorkspaceAdapter(@NonNull FragmentActivity activity, int taskId) {
        super(activity);
        this.taskId = taskId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return TaskInfoFragment.newInstance(taskId);
            case 1:
                return QuickNotesFragment.newInstance(taskId);
            case 2:
                return TablesFragment.newInstance(taskId);
        }
        return TaskInfoFragment.newInstance(taskId);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
