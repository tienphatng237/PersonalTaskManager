package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.view.View;
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
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Màn hình Workspace của Task — hỗ trợ block kiểu Notion.
 * Giữ nguyên toàn bộ logic cũ.
 */
public class TaskWorkspaceActivity extends AppCompatActivity implements MoveHandler {

    private RecyclerView rvWorkspace;
    private Chip btnAddParagraph, btnAddTodo, btnAddBullet, btnAddDivider, btnAddFile;
    private ImageButton btnBack;

    private TextView tvTaskTitle, tvTaskDeadline;

    private final List<NotionBlock> blocks = new ArrayList<>();
    private NotionBlockAdapter adapter;

    private TaskViewModel vm;
    private Task task;
    private int taskId;

    private static final int REQ_PICK_FILE = 2001;
    private static final int REQ_EDIT_TASK = 3001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_workspace);

        vm = new ViewModelProvider(this).get(TaskViewModel.class);
        taskId = getIntent().getIntExtra("task_id", -1);

        initViews();
        initRecycler();
        observeTask();
        setupActions();
    }

    private void observeTask() {
        vm.getTaskById(taskId).observe(this, t -> {
            if (t == null) return;
            task = t;
            applyTaskInfo();
            loadBlocks();
        });
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

        findViewById(R.id.card_top_bar).setOnClickListener(v -> openTaskDetail());
    }

    private void initRecycler() {
        rvWorkspace.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotionBlockAdapter(blocks);
        rvWorkspace.setAdapter(adapter);

        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        ItemTouchHelper helper =
                new ItemTouchHelper(new BlockDragCallback(blocks, adapter, vib, this));
        helper.attachToRecyclerView(rvWorkspace);
    }

    private void applyTaskInfo() {
        tvTaskTitle.setText(task.getTitle());
        long dl = task.getDeadline();
        if (dl > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            tvTaskDeadline.setText(sdf.format(dl));
        } else {
            tvTaskDeadline.setText("No deadline");
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
        blocks.add(new NotionBlock(UUID.randomUUID().toString(), type, "", false));
        adapter.notifyItemInserted(blocks.size() - 1);
        rvWorkspace.scrollToPosition(blocks.size() - 1);
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQ_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        // ===== XỬ LÝ PICK FILE (FIX CHÍNH) =====
        if (req == REQ_PICK_FILE && res == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) return;

            String fileName = "File";
            Cursor c = getContentResolver().query(uri, null, null, null, null);
            if (c != null) {
                int nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (c.moveToFirst() && nameIndex >= 0) {
                    fileName = c.getString(nameIndex);
                }
                c.close();
            }

            NotionBlock fileBlock =
                    new NotionBlock(UUID.randomUUID().toString(), NotionBlock.Type.FILE, "", false);
            fileBlock.fileUri = uri.toString();
            fileBlock.fileName = fileName;

            blocks.add(fileBlock);
            adapter.notifyItemInserted(blocks.size() - 1);
            rvWorkspace.scrollToPosition(blocks.size() - 1);
            return;
        }

        if (req == REQ_EDIT_TASK && res == RESULT_OK) {
            // giữ nguyên
        }
    }

    private void save() {
        task.setNotesJson(NotionBlockParser.toJson(blocks));
        vm.updateTask(task, task.getTitle(), task.getDescription(), task.getDeadline());
    }

    private void openTaskDetail() {
        Intent i = new Intent(this, TaskDetailActivity.class);
        i.putExtra("task_id", taskId);
        startActivityForResult(i, REQ_EDIT_TASK);
    }

    @Override public void onItemMove(int fromPos, int toPos) {}
    @Override public void onItemDrop() { save(); }
}
