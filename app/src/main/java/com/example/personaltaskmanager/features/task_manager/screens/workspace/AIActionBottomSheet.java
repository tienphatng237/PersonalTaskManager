package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.ai.GeminiApiService;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlock;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * AI Action Bottom Sheet
 * -----------------------
 * Hiển thị menu các tính năng AI cho block text:
 * - Complete: Tự động hoàn thiện
 * - Improve: Cải thiện văn bản
 * - Summarize: Tóm tắt
 * - Expand: Mở rộng
 */
public class AIActionBottomSheet extends BottomSheetDialogFragment {

    public interface Listener {
        void onTextUpdated(NotionBlock block, String newText);
        void onDelete(NotionBlock block);
        void onDuplicate(NotionBlock block);
        void onMove(NotionBlock block);
    }

    private NotionBlock block;
    private Listener listener;
    private GeminiApiService geminiService;
    private ProgressDialog progressDialog;

    public AIActionBottomSheet(NotionBlock block, Listener listener) {
        this.block = block;
        this.listener = listener;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feature_task_manager_ai_actions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        Context context = requireContext();
        geminiService = GeminiApiService.getInstance(context);

        // Title
        TextView tvTitle = v.findViewById(R.id.tv_ai_title);
        tvTitle.setText("AI Assistant");

        // Complete
        TextView btnComplete = v.findViewById(R.id.btn_ai_complete);
        btnComplete.setOnClickListener(view -> {
            if (block.text == null || block.text.trim().isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập nội dung trước", Toast.LENGTH_SHORT).show();
                return;
            }
            showProgress("Đang hoàn thiện...");
            geminiService.completeText(block.text, new GeminiApiService.GeminiCallback() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    if (listener != null) {
                        listener.onTextUpdated(block, block.text + " " + result);
                    }
                    dismiss();
                }

                @Override
                public void onError(String error) {
                    hideProgress();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Improve
        TextView btnImprove = v.findViewById(R.id.btn_ai_improve);
        btnImprove.setOnClickListener(view -> {
            if (block.text == null || block.text.trim().isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập nội dung trước", Toast.LENGTH_SHORT).show();
                return;
            }
            showProgress("Đang cải thiện...");
            geminiService.improveText(block.text, new GeminiApiService.GeminiCallback() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    if (listener != null) {
                        listener.onTextUpdated(block, result);
                    }
                    dismiss();
                }

                @Override
                public void onError(String error) {
                    hideProgress();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Summarize
        TextView btnSummarize = v.findViewById(R.id.btn_ai_summarize);
        btnSummarize.setOnClickListener(view -> {
            if (block.text == null || block.text.trim().isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập nội dung trước", Toast.LENGTH_SHORT).show();
                return;
            }
            showProgress("Đang tóm tắt...");
            geminiService.summarizeText(block.text, new GeminiApiService.GeminiCallback() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    if (listener != null) {
                        listener.onTextUpdated(block, result);
                    }
                    dismiss();
                }

                @Override
                public void onError(String error) {
                    hideProgress();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Expand
        TextView btnExpand = v.findViewById(R.id.btn_ai_expand);
        btnExpand.setOnClickListener(view -> {
            if (block.text == null || block.text.trim().isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập nội dung trước", Toast.LENGTH_SHORT).show();
                return;
            }
            showProgress("Đang mở rộng...");
            geminiService.expandText(block.text, new GeminiApiService.GeminiCallback() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    if (listener != null) {
                        listener.onTextUpdated(block, result);
                    }
                    dismiss();
                }

                @Override
                public void onError(String error) {
                    hideProgress();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Duplicate
        TextView btnDuplicate = v.findViewById(R.id.btn_action_duplicate);
        btnDuplicate.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDuplicate(block);
            }
            dismiss();
        });

        // Move
        TextView btnMove = v.findViewById(R.id.btn_action_move);
        btnMove.setOnClickListener(view -> {
            if (listener != null) {
                listener.onMove(block);
            }
            dismiss();
        });

        // Delete
        TextView btnDelete = v.findViewById(R.id.btn_action_delete);
        btnDelete.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDelete(block);
            }
            dismiss();
        });
    }

    private void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideProgress();
    }
}

