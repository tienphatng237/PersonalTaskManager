package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlock;

public class TaskFileActionBottomSheet extends BottomSheetDialogFragment {

    public interface Listener {
        void onDelete(NotionBlock block);
        void onDuplicate(NotionBlock block);
    }

    private NotionBlock block;
    private Listener listener;

    public TaskFileActionBottomSheet(NotionBlock block, Listener listener) {
        this.block = block;
        this.listener = listener;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.feature_task_manager_file_actions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {

        v.<TextView>findViewById(R.id.btn_action_view).setOnClickListener(view -> {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.parse(block.fileUri), "*/*");
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Cannot open file", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });

        v.<TextView>findViewById(R.id.btn_action_copy).setOnClickListener(view -> {
            ClipboardManager cm =
                    (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("file", block.fileUri));
            Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        v.<TextView>findViewById(R.id.btn_action_duplicate).setOnClickListener(view -> {
            if (listener != null) listener.onDuplicate(block);
            dismiss();
        });

        v.<TextView>findViewById(R.id.btn_action_move).setOnClickListener(view -> {
            Toast.makeText(getContext(), "Move feature is not implemented", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        v.<TextView>findViewById(R.id.btn_action_delete).setOnClickListener(view -> {
            if (listener != null) listener.onDelete(block);
            dismiss();
        });
    }
}
