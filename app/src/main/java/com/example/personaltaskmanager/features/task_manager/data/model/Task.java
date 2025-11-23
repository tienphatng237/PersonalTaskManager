package com.example.personaltaskmanager.features.task_manager.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public long createdAt;

    public Task(String title, String description, long createdAt) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }
}
