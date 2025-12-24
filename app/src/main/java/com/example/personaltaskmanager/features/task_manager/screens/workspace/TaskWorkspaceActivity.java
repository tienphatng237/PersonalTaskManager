package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageView imgTaskIcon;

    private final List<NotionBlock> blocks = new ArrayList<>();
    private NotionBlockAdapter adapter;

    private TaskViewModel vm;
    private Task task;
    private int taskId;
    private String taskUuid;

    private static final int REQ_PICK_FILE = 2001;
    private static final int REQ_EDIT_TASK = 3001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_workspace);

        setLightStatusBar();

        vm = new ViewModelProvider(this).get(TaskViewModel.class);
        taskId = getIntent().getIntExtra("task_id", -1);
        taskUuid = getIntent().getStringExtra("task_uuid");

        // Reset dữ liệu
        task = null;
        blocks.clear();

        initViews();
        initRecycler();
        observeTask();
        setupActions();
    }

    /** Light status bar (giống create task) */
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

    /**
     * Observe Task từ ViewModel
     * Khi Task thay đổi → load lại thông tin + block
     */
    private void observeTask() {
        // Ưu tiên dùng UUID nếu có, nếu không thì dùng ID
        androidx.lifecycle.LiveData<Task> taskLiveData;
        if (taskUuid != null && !taskUuid.isEmpty()) {
            taskLiveData = vm.getTaskByUuid(taskUuid);
        } else if (taskId != -1) {
            taskLiveData = vm.getTaskById(taskId);
        } else {
            finish();
            return;
        }

        taskLiveData.observe(this, t -> {
            // Verify bằng cả ID và UUID để đảm bảo chính xác
            boolean isValid = false;
            if (t != null) {
                if (taskUuid != null && !taskUuid.isEmpty()) {
                    isValid = taskUuid.equals(t.getUuid());
                } else {
                    isValid = t.getId() == taskId;
                }
            }

            if (isValid) {
                task = t;
                taskId = t.getId(); // Update taskId từ task loaded
                applyTaskInfo();
                loadBlocks();
            } else if (t == null) {
                // Task không tồn tại
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo update lại khi vào lại activity
        if (task != null && task.getId() == taskId) {
            applyTaskInfo();
            loadBlocks();
        }
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
        imgTaskIcon = findViewById(R.id.img_task_icon);

        findViewById(R.id.card_top_bar).setOnClickListener(v -> openTaskDetail());
    }

    private void initRecycler() {
        rvWorkspace.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotionBlockAdapter(blocks);
        rvWorkspace.setAdapter(adapter);

        // Setup menu listener - show AI menu for text blocks, file menu for file blocks
        adapter.setFileMenuListener((block, position, anchor) -> {
            // Show AI menu for text blocks (PARAGRAPH, TODO, BULLET)
            if (block.type == NotionBlock.Type.PARAGRAPH 
                    || block.type == NotionBlock.Type.TODO 
                    || block.type == NotionBlock.Type.BULLET) {
                
                AIActionBottomSheet aiSheet = new AIActionBottomSheet(block, new AIActionBottomSheet.Listener() {
                    @Override
                    public void onTextUpdated(NotionBlock b, String newText) {
                        b.text = newText;
                        adapter.notifyItemChanged(position);
                        save();
                    }

                    @Override
                    public void onDelete(NotionBlock b) {
                        blocks.remove(b);
                        adapter.notifyItemRemoved(position);
                        save();
                    }

                    @Override
                    public void onDuplicate(NotionBlock b) {
                        NotionBlock copy = new NotionBlock(
                                java.util.UUID.randomUUID().toString(),
                                b.type,
                                b.text,
                                b.isChecked
                        );
                        copy.fileUri = b.fileUri;
                        copy.fileName = b.fileName;
                        copy.deadline = b.deadline;
                        blocks.add(position + 1, copy);
                        adapter.notifyItemInserted(position + 1);
                        save();
                    }

                    @Override
                    public void onMove(NotionBlock b) {
                        MoveBlockDialog dialog = new MoveBlockDialog(b, taskId, vm, targetTaskId -> {
                            moveBlockToTask(b, targetTaskId);
                        });
                        dialog.show(getSupportFragmentManager(), "MoveBlockDialog");
                    }
                });
                aiSheet.show(getSupportFragmentManager(), "AIActions");
                
            } else {
                // Show file menu for FILE blocks
                TaskFileActionBottomSheet sheet = new TaskFileActionBottomSheet(block, new TaskFileActionBottomSheet.Listener() {
                    @Override
                    public void onDelete(NotionBlock b) {
                        blocks.remove(b);
                        adapter.notifyItemRemoved(position);
                        save();
                    }

                    @Override
                    public void onDuplicate(NotionBlock b) {
                        NotionBlock copy = new NotionBlock(
                                java.util.UUID.randomUUID().toString(),
                                b.type,
                                b.text,
                                b.isChecked
                        );
                        copy.fileUri = b.fileUri;
                        copy.fileName = b.fileName;
                        copy.deadline = b.deadline;
                        blocks.add(position + 1, copy);
                        adapter.notifyItemInserted(position + 1);
                        save();
                    }

                    @Override
                    public void onMove(NotionBlock b) {
                        MoveBlockDialog dialog = new MoveBlockDialog(b, taskId, vm, targetTaskId -> {
                            moveBlockToTask(b, targetTaskId);
                        });
                        dialog.show(getSupportFragmentManager(), "MoveBlockDialog");
                    }
                });
                sheet.show(getSupportFragmentManager(), "FileActions");
            }
        });

        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        ItemTouchHelper helper =
                new ItemTouchHelper(new BlockDragCallback(blocks, adapter, vib, this));
        helper.attachToRecyclerView(rvWorkspace);
    }

    private void moveBlockToTask(NotionBlock block, int targetTaskId) {
        // Xóa block khỏi task hiện tại
        blocks.remove(block);
        adapter.notifyDataSetChanged();
        save();

        // Thêm block vào task đích - chạy trong background thread để tránh vòng lặp
        com.example.personaltaskmanager.features.task_manager.data.local.db.AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                com.example.personaltaskmanager.features.task_manager.data.repository.TaskRepository repo = 
                    new com.example.personaltaskmanager.features.task_manager.data.repository.TaskRepository(this);
                
                Task targetTask = repo.getTaskByIdSync(targetTaskId);
                if (targetTask != null) {
                    List<NotionBlock> targetBlocks = NotionBlockParser.fromJson(targetTask.getNotesJson());
                    targetBlocks.add(block);
                    targetTask.setNotesJson(NotionBlockParser.toJson(targetBlocks));
                    repo.updateTask(targetTask);
                    
                    // Hiển thị toast trên main thread
                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(this, "Đã di chuyển block", android.widget.Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(this, "Lỗi khi di chuyển block", android.widget.Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void applyTaskInfo() {
        tvTaskTitle.setText(task.getTitle());

        long dl = task.getDeadline();
        if (dl > 0) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            tvTaskDeadline.setText(sdf.format(dl));
        } else {
            tvTaskDeadline.setText("No deadline");
        }

        // ===== IMAGE (ĐỒNG BỘ VỚI TASK ADAPTER) =====
        if (task.getImageUri() != null && !task.getImageUri().isEmpty()) {
            imgTaskIcon.setImageURI(Uri.parse(task.getImageUri()));
        } else {
            imgTaskIcon.setImageResource(
                    R.drawable.feature_task_manager_ic_image_placeholder
            );
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

        // ===== XỬ LÝ PICK FILE =====
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
        }
    }

    /**
     * Lưu block xuống DB
     * Calendar đọc TODO con hoàn toàn từ notesJson
     */
    private void save() {
        if (task == null) return;
        task.setNotesJson(NotionBlockParser.toJson(blocks));
        vm.updateTask(task, task.getTitle(), task.getDescription(), task.getDeadline());
    }

    private void openTaskDetail() {
        Intent i = new Intent(this, TaskDetailActivity.class);
        i.putExtra("task_id", taskId);
        if (task != null && task.getUuid() != null) {
            i.putExtra("task_uuid", task.getUuid());
        }
        startActivityForResult(i, REQ_EDIT_TASK);
    }

    @Override public void onItemMove(int fromPos, int toPos) {}

    @Override
    public void onItemDrop() {
        save();
    }

    /**
     * ĐẢM BẢO:
     * - TODO con + deadline luôn được lưu
     * - Calendar đọc được ngay khi chuyển tab
     */
    @Override
    protected void onPause() {
        super.onPause();
        save();
    }
}
