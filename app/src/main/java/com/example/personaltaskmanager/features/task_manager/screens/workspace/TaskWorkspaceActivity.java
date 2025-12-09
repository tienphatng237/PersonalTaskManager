package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.screens.TaskDetailActivity;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlock;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlockParser;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TaskWorkspaceActivity extends AppCompatActivity implements MoveHandler {

    private RecyclerView rvWorkspace;
    private Chip btnAddParagraph, btnAddTodo, btnAddBullet, btnAddDivider, btnAddFile;
    private ImageButton btnBack;

    private TextView tvTaskTitle, tvTaskDeadline;

    private final List<NotionBlock> blocks = new ArrayList<>();
    private NotionBlockAdapter adapter;
    private TaskViewModel vm;
    private Task task;

    private static final int REQ_PICK_FILE = 2001;
    private static final int REQ_EDIT_TASK = 3001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_workspace);

        vm = new ViewModelProvider(this).get(TaskViewModel.class);

        int taskId = getIntent().getIntExtra("task_id", -1);
        task = vm.getTaskById(taskId);

        initViews();
        initRecycler();
        applyTaskInfo();
        loadBlocks();
        setupActions();
    }

    private void initViews() {
        rvWorkspace = findViewById(R.id.rv_workspace);

        btnAddParagraph = findViewById(R.id.btn_add_paragraph);
        btnAddTodo = findViewById(R.id.btn_add_todo);
        btnAddBullet = findViewById(R.id.btn_add_bullet);
        btnAddDivider = findViewById(R.id.btn_add_divider);
        btnAddFile = findViewById(R.id.btn_add_file);

        btnBack = findViewById(R.id.btn_back_ws);

        tvTaskTitle = findViewById(R.id.tv_task_title);
        tvTaskDeadline = findViewById(R.id.tv_task_deadline);

        View topBar = findViewById(R.id.card_top_bar);
        topBar.setOnClickListener(v -> openTaskDetail());
    }

    private void initRecycler() {
        rvWorkspace.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotionBlockAdapter(blocks);

        adapter.setFileMenuListener((block, position, anchor) -> {
            showBottomSheet(block, position);
        });

        rvWorkspace.setAdapter(adapter);

        // ⭐ Drag–drop + Haptic
        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ItemTouchHelper dragHelper =
                new ItemTouchHelper(new BlockDragCallback(blocks, adapter, vib, this));

        dragHelper.attachToRecyclerView(rvWorkspace);
    }

    private void applyTaskInfo() {
        if (task != null) {
            tvTaskTitle.setText(task.getTitle());

            long dl = task.getDeadline();
            if (dl > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                tvTaskDeadline.setText(sdf.format(dl));
            } else {
                tvTaskDeadline.setText("No deadline");
            }
        }
    }

    private void loadBlocks() {
        blocks.clear();
        blocks.addAll(NotionBlockParser.fromJson(task.getNotesJson()));
        adapter.notifyDataSetChanged();
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> {
            save();
            finish();
        });

        btnAddParagraph.setOnClickListener(v -> addBlock(NotionBlock.Type.PARAGRAPH));
        btnAddTodo.setOnClickListener(v -> addBlock(NotionBlock.Type.TODO));
        btnAddBullet.setOnClickListener(v -> addBlock(NotionBlock.Type.BULLET));
        btnAddDivider.setOnClickListener(v -> addBlock(NotionBlock.Type.DIVIDER));

        btnAddFile.setOnClickListener(v -> pickFile());
    }

    private void addBlock(NotionBlock.Type type) {
        blocks.add(new NotionBlock(
                UUID.randomUUID().toString(),
                type,
                "",
                false
        ));

        adapter.notifyItemInserted(blocks.size() - 1);
        rvWorkspace.scrollToPosition(blocks.size() - 1);
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQ_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        if (req == REQ_PICK_FILE && res == RESULT_OK && data != null) {
            Uri uri = data.getData();

            NotionBlock block = new NotionBlock(
                    UUID.randomUUID().toString(),
                    NotionBlock.Type.FILE,
                    "",
                    false
            );

            block.fileUri = uri.toString();
            block.fileName = getFileName(uri);

            blocks.add(block);
            adapter.notifyItemInserted(blocks.size() - 1);
            rvWorkspace.scrollToPosition(blocks.size() - 1);
        }

        if (req == REQ_EDIT_TASK && res == RESULT_OK) {
            task = vm.getTaskById(task.getId());
            applyTaskInfo();
            showIOSPopup("Đã cập nhật công việc");
        }
    }

    private String getFileName(Uri uri) {
        String name = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            name = cursor.getString(idx);
            cursor.close();
        }
        return name;
    }

    private void showBottomSheet(NotionBlock block, int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(
                R.layout.feature_task_manager_file_actions, null
        );

        dialog.setContentView(view);

        TextView btnView = view.findViewById(R.id.btn_action_view);
        TextView btnCopy = view.findViewById(R.id.btn_action_copy);
        TextView btnDel = view.findViewById(R.id.btn_action_delete);

        btnView.setOnClickListener(v -> {
            dialog.dismiss();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(block.fileUri), "*/*");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(i);
        });

        btnCopy.setOnClickListener(v -> {
            dialog.dismiss();
            android.content.ClipboardManager cm =
                    (android.content.ClipboardManager)
                            getSystemService(CLIPBOARD_SERVICE);

            cm.setPrimaryClip(
                    android.content.ClipData.newPlainText("file", block.fileUri)
            );
        });

        btnDel.setOnClickListener(v -> {
            dialog.dismiss();
            blocks.remove(position);
            adapter.notifyItemRemoved(position);
        });

        dialog.show();
    }

    private void save() {
        String json = NotionBlockParser.toJson(blocks);
        task.setNotesJson(json);

        vm.updateTask(
                task,
                task.getTitle(),
                task.getDescription(),
                task.getDeadline()
        );
    }

    private void openTaskDetail() {
        Intent i = new Intent(this, TaskDetailActivity.class);
        i.putExtra("task_id", task.getId());
        startActivityForResult(i, REQ_EDIT_TASK);
    }

    private void showIOSPopup(String message) {

        TextView popup = new TextView(this);
        popup.setText(message);
        popup.setTextSize(15);
        popup.setTextColor(Color.parseColor("#00332E"));
        popup.setPadding(40, 28, 40, 28);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#F2FFFFFF"));
        bg.setCornerRadius(22f);
        popup.setBackground(bg);

        popup.setElevation(20f);
        popup.setAlpha(0f);
        popup.setTranslationY(-250);

        addContentView(popup,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        popup.post(() -> popup.setX(
                (getWindow().getDecorView().getWidth() - popup.getWidth()) / 2f
        ));

        popup.animate()
                .translationY(120)
                .alpha(1f)
                .setDuration(380)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        popup.postDelayed(() ->
                        popup.animate()
                                .translationY(-200)
                                .alpha(0f)
                                .setDuration(350)
                                .withEndAction(() -> {
                                    ViewGroup parent = (ViewGroup) popup.getParent();
                                    if (parent != null) parent.removeView(popup);
                                })
                                .start()
                , 1700
        );
    }

    // ============================================================
    //  MOVE HANDLER IMPLEMENTATION
    // ============================================================

    @Override
    public void onItemMove(int fromPos, int toPos) {
        // Swap đã được xử lý trong BlockDragCallback — không cần xử lý thêm
    }

    @Override
    public void onItemDrop() {
        // Lưu thứ tự block ngay khi thả
        save();
    }
}
