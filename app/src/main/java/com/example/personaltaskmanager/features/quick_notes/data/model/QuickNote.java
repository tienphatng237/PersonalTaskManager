package com.example.personaltaskmanager.features.quick_notes.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quick_notes")
public class QuickNote {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int taskId;          // liên kết với Task
    public String content;      // nội dung note
    public long createdAt;      // timestamp

    public QuickNote(int taskId, String content, long createdAt) {
        this.taskId = taskId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
