package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.quick_notes.data.model.QuickNote;
import com.example.personaltaskmanager.features.quick_notes.screens.QuickNoteAdapter;
import com.example.personaltaskmanager.features.quick_notes.viewmodel.QuickNoteViewModel;

public class QuickNotesFragment extends Fragment {

    private int taskId;
    private QuickNoteViewModel viewModel;
    private QuickNoteAdapter adapter;

    public static QuickNotesFragment newInstance(int taskId) {
        QuickNotesFragment f = new QuickNotesFragment();
        Bundle b = new Bundle();
        b.putInt("task_id", taskId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskId = getArguments().getInt("task_id", -1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.feature_task_manager_fragment_quick_notes,
                container,
                false
        );

        RecyclerView rv = view.findViewById(R.id.rv_notes);
        EditText edt = view.findViewById(R.id.edt_new_note);
        ImageButton btnAdd = view.findViewById(R.id.btn_add);

        viewModel = new ViewModelProvider(requireActivity()).get(QuickNoteViewModel.class);

        adapter = new QuickNoteAdapter(note -> viewModel.deleteNote(note));

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        viewModel.getNotes(taskId).observe(getViewLifecycleOwner(), adapter::setData);

        btnAdd.setOnClickListener(v -> {
            String content = edt.getText().toString().trim();
            if (!content.isEmpty()) {
                viewModel.addNote(taskId, content);
                edt.setText("");
            }
        });

        return view;
    }
}
