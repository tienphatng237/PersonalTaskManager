package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.personaltaskmanager.R;

public class TablesFragment extends Fragment {

    private int taskId;

    public TablesFragment() {
        // Required empty constructor
    }

    public static TablesFragment newInstance(int taskId) {
        TablesFragment fragment = new TablesFragment();
        Bundle args = new Bundle();
        args.putInt("task_id", taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            taskId = getArguments().getInt("task_id", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.feature_task_manager_fragment_tables,
                container,
                false
        );
    }
}
