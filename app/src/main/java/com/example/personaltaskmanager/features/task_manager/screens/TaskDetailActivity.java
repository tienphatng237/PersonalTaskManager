package com.example.personaltaskmanager.features.task_manager.screens;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.TaskWorkspaceActivity;
import com.example.personaltaskmanager.features.task_manager.utils.DateUtils;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;

import java.util.Calendar;

/**
 * Màn hình thêm / sửa Task.
 * Giữ nguyên code cũ, chỉ bổ sung deadline + DatePicker + chọn ảnh công việc.
 */
public class TaskDetailActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription, edtDate, edtTags;
    private Button btnSave;
    private ImageButton btnBack;
    private ImageView imgTask;
    private TextView btnPickImage;
    private RadioGroup rgPriority;
    private Spinner spinnerRecurring;

    private TaskViewModel viewModel;

    private int taskId = -1;
    private String taskUuid;
    private Task currentTask = null;

    private long selectedDeadline = System.currentTimeMillis();
    private String selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_detail);

        setLightStatusBar();
        initViews();

        // Reset dữ liệu
        currentTask = null;
        taskId = -1;
        taskUuid = null;

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        loadTaskIfEditMode();
        setupListeners();
    }

    private void initViews() {
        edtTitle = findViewById(R.id.edt_task_title);
        edtDescription = findViewById(R.id.edt_task_description);
        edtDate = findViewById(R.id.edt_task_date);
        edtTags = findViewById(R.id.edt_tags);
        btnSave = findViewById(R.id.btn_save_task);
        btnBack = findViewById(R.id.btn_back);

        imgTask = findViewById(R.id.img_task);
        btnPickImage = findViewById(R.id.btn_pick_image);

        rgPriority = findViewById(R.id.rg_priority);
        spinnerRecurring = findViewById(R.id.spinner_recurring);

        // Setup recurring spinner
        String[] recurringOptions = {"Không lặp lại", "Hàng ngày", "Hàng tuần", "Hàng tháng"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recurringOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecurring.setAdapter(adapter);
    }

    /** Load dữ liệu nếu đang sửa task */
    private void loadTaskIfEditMode() {
        taskId = getIntent().getIntExtra("task_id", -1);
        taskUuid = getIntent().getStringExtra("task_uuid");

        if (taskId != -1 || (taskUuid != null && !taskUuid.isEmpty())) {
            // Ưu tiên dùng UUID nếu có, nếu không thì dùng ID
            androidx.lifecycle.LiveData<Task> taskLiveData;
            if (taskUuid != null && !taskUuid.isEmpty()) {
                taskLiveData = viewModel.getTaskByUuid(taskUuid);
            } else {
                taskLiveData = viewModel.getTaskById(taskId);
            }

            taskLiveData.observe(this, task -> {
                // Verify bằng cả ID và UUID để đảm bảo chính xác
                boolean isValid = false;
                if (task != null) {
                    if (taskUuid != null && !taskUuid.isEmpty()) {
                        isValid = taskUuid.equals(task.getUuid());
                    } else {
                        isValid = task.getId() == taskId;
                    }
                }

                if (isValid) {
                    currentTask = task;
                    taskId = task.getId(); // Update taskId từ task loaded
                    updateTaskInfo();
                } else if (task == null) {
                    // Task không tồn tại
                    finish();
                }
            });
        }
    }

    private void updateTaskInfo() {
        if (currentTask == null) return;

        edtTitle.setText(currentTask.getTitle());
        edtDescription.setText(currentTask.getDescription());

        selectedDeadline = currentTask.getDeadline();
        edtDate.setText(DateUtils.formatDate(selectedDeadline));

        // Priority
        String priority = currentTask.getPriority();
        if (priority == null || priority.isEmpty()) priority = "medium";
        RadioButton rb = findViewById(
            priority.equals("high") ? R.id.rb_priority_high :
            priority.equals("low") ? R.id.rb_priority_low : R.id.rb_priority_medium
        );
        if (rb != null) rb.setChecked(true);

        // Tags
        List<String> tags = currentTask.getTagsList();
        StringBuilder tagsBuilder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) tagsBuilder.append(", ");
            tagsBuilder.append(tags.get(i));
        }
        edtTags.setText(tagsBuilder.toString());

        // Recurring
        String recurring = currentTask.getRecurringPattern();
        if (recurring == null || recurring.isEmpty()) recurring = "none";
        int position = recurring.equals("daily") ? 1 :
                       recurring.equals("weekly") ? 2 :
                       recurring.equals("monthly") ? 3 : 0;
        spinnerRecurring.setSelection(position);

        if (currentTask.getImageUri() != null && !currentTask.getImageUri().isEmpty()) {
            selectedImageUri = currentTask.getImageUri();
            try {
                imgTask.setImageURI(Uri.parse(currentTask.getImageUri()));
                imgTask.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                imgTask.setVisibility(View.GONE);
            }
        } else {
            imgTask.setVisibility(View.GONE);
        }

        btnSave.setText("Cập nhật công việc");
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo update lại khi vào lại activity
        if (currentTask != null && currentTask.getId() == taskId) {
            updateTaskInfo();
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        edtDate.setOnClickListener(v -> openDatePicker());
        btnSave.setOnClickListener(v -> saveTask());

        btnPickImage.setOnClickListener(v -> openGallery());
        imgTask.setOnClickListener(v -> openGallery());
        
        // Add workspace button if editing existing task
        if (taskId != -1 || (taskUuid != null && !taskUuid.isEmpty())) {
            Button btnWorkspace = findViewById(R.id.btn_open_workspace);
            if (btnWorkspace != null) {
                btnWorkspace.setVisibility(View.VISIBLE);
                btnWorkspace.setOnClickListener(v -> openWorkspace());
            }
        }
    }
    
    private void openWorkspace() {
        Intent intent = new Intent(this, TaskWorkspaceActivity.class);
        intent.putExtra("task_id", taskId);
        if (taskUuid != null && !taskUuid.isEmpty()) {
            intent.putExtra("task_uuid", taskUuid);
        } else if (currentTask != null && currentTask.getUuid() != null) {
            intent.putExtra("task_uuid", currentTask.getUuid());
        }
        startActivity(intent);
    }

    /**
     * Mở Gallery chọn ảnh
     * Dùng ACTION_OPEN_DOCUMENT để đảm bảo persist permission
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );
        pickImageLauncher.launch(intent);
    }

    /**
     * Nhận kết quả chọn ảnh + persist quyền đọc URI
     */
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                final int takeFlags =
                                        result.getData().getFlags()
                                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                                getContentResolver()
                                        .takePersistableUriPermission(uri, takeFlags);

                                selectedImageUri = uri.toString();
                                imgTask.setImageURI(uri);
                            }
                        }
                    }
            );

    private void openDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDeadline);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                R.style.TaskManagerDatePickerTheme,
                (view, year, month, day) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day, 0, 0, 0);
                    selectedDeadline = c.getTimeInMillis();
                    edtDate.setText(DateUtils.formatDate(selectedDeadline));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void saveTask() {
        String title = edtTitle.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError("Tên công việc không được để trống");
            return;
        }

        // Get priority
        int selectedId = rgPriority.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedId);
        String priority = rb != null ? (String) rb.getTag() : "medium";

        // Get tags
        String tagsStr = edtTags.getText().toString().trim();
        List<String> tagsList = new ArrayList<>();
        if (!tagsStr.isEmpty()) {
            String[] parts = tagsStr.split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    tagsList.add(trimmed);
                }
            }
        }
        JSONArray tagsJson = new JSONArray(tagsList);
        String tags = tagsJson.toString();

        // Get recurring
        String recurringPattern = "none";
        int recurringPos = spinnerRecurring.getSelectedItemPosition();
        if (recurringPos == 1) recurringPattern = "daily";
        else if (recurringPos == 2) recurringPattern = "weekly";
        else if (recurringPos == 3) recurringPattern = "monthly";

        // UPDATE
        if (currentTask != null) {
            currentTask.setImageUri(selectedImageUri);
            currentTask.setPriority(priority);
            currentTask.setTags(tags);
            currentTask.setRecurringPattern(recurringPattern);
            viewModel.updateTask(currentTask, title, desc, selectedDeadline);
            setResult(RESULT_OK);
            finish();
            return;
        }

        // ADD
        Task newTask = new Task(title, desc, System.currentTimeMillis(),
            selectedDeadline, "", "", 0);
        newTask.setImageUri(selectedImageUri);
        newTask.setPriority(priority);
        newTask.setTags(tags);
        newTask.setRecurringPattern(recurringPattern);
        viewModel.addTask(newTask);
        setResult(RESULT_OK);
        finish();
    }

    /** Light status bar */
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
}
