package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.personaltaskmanager.R;

public class FileActionsActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_URI = "file_uri";
    public static final String EXTRA_FILE_NAME = "file_name";

    public static final String RESULT_ACTION = "result_action";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_up, R.anim.none);

        // làm mờ background
        Window window = getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);

        setContentView(R.layout.feature_task_manager_file_actions);

        String fileUri = getIntent().getStringExtra(EXTRA_FILE_URI);
        String fileName = getIntent().getStringExtra(EXTRA_FILE_NAME);

        TextView btnView = findViewById(R.id.btn_action_view);
        TextView btnCopy = findViewById(R.id.btn_action_copy);
        TextView btnDuplicate = findViewById(R.id.btn_action_duplicate);
        TextView btnMove = findViewById(R.id.btn_action_move);
        TextView btnDelete = findViewById(R.id.btn_action_delete);

        btnView.setOnClickListener(v -> {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(Uri.parse(fileUri), "*/*");
            viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(viewIntent);
        });

        btnCopy.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("file", fileUri));
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
        });

        btnDuplicate.setOnClickListener(v -> finishWithResult("duplicate"));

        btnMove.setOnClickListener(v -> finishWithResult("move"));

        btnDelete.setOnClickListener(v -> finishWithResult("delete"));
    }

    private void finishWithResult(String action) {
        Intent data = new Intent();
        data.putExtra(RESULT_ACTION, action);
        setResult(Activity.RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.none, R.anim.slide_down);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.slide_down);
    }
}
